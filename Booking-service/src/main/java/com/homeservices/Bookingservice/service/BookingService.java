package com.homeservices.Bookingservice.service;

import com.homeservices.Bookingservice.client.OfferResponse;
import com.homeservices.Bookingservice.client.OfferServiceClient;
import com.homeservices.Bookingservice.client.UserServiceClient;
import com.homeservices.Bookingservice.config.RabbitMQConfig;
import com.homeservices.Bookingservice.entity.*;
import com.homeservices.Bookingservice.repository.BookingRepository;
import com.homeservices.Bookingservice.repository.ServiceRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final UserServiceClient userServiceClient;
    private final OfferServiceClient offerServiceClient;
    private final RabbitTemplate rabbitTemplate;
    private final BookingStatsManager statsManager;


    public Booking createBooking(Long customerId, String customerName, Long offerId) {

        OfferResponse offer = offerServiceClient.getOffer(offerId);
        if (offer == null) {
            throw new RuntimeException("Offer not found!");
        }

        if (!"ACTIVE".equals(offer.getStatus())) {
            throw new RuntimeException("Offer is no longer available!");
        }

        Double balance = userServiceClient.getUserBalance(customerId);
        if (balance < offer.getPrice()) {
            sendFailureNotification(customerId, customerName,
                    "Booking failed! Insufficient balance. Required: $" + offer.getPrice()
                            + ", Available: $" + balance);

            sendPaymentFailedNotification(customerId, customerName,
                    "Payment failed - insufficient balance. Required: $" + offer.getPrice()
                            + ", Available: $" + balance);

            statsManager.incrementCancelled();
            throw new RuntimeException("Insufficient balance!");
        }

        try {
            userServiceClient.deductBalance(customerId, offer.getPrice());

            try {
                offerServiceClient.markOfferCompleted(offerId);
            } catch (Exception e) {
                // ROLLBACK: refund the deducted balance
                userServiceClient.refundBalance(customerId, offer.getPrice());
                sendFailureNotification(customerId, customerName,
                        "Booking failed! Offer could not be completed. Balance refunded.");
                sendPaymentFailedNotification(customerId, customerName,
                        "Payment failed during booking - balance refunded. Reason: " + e.getMessage());
                statsManager.incrementCancelled();
                throw new RuntimeException("Booking failed! Balance has been refunded.");
            }

            Booking booking = new Booking();
            booking.setCustomerId(customerId);
            booking.setCustomerName(customerName);
            booking.setOfferId(offerId);
            booking.setProviderId(offer.getProviderId());
            booking.setProviderName(offer.getProviderName());
            booking.setCategory(offer.getCategory());
            booking.setPrice(offer.getPrice());
            booking.setBookingDate(LocalDate.now());
            booking.setStatus(BookingStatus.CONFIRMED);

            Booking saved = bookingRepository.save(booking);

            Map<String, Object> confirmMsg = new HashMap<>();
            confirmMsg.put("bookingId", saved.getId());
            confirmMsg.put("customerId", customerId);
            confirmMsg.put("customerName", customerName);
            confirmMsg.put("providerId", offer.getProviderId());
            confirmMsg.put("providerName", offer.getProviderName());
            confirmMsg.put("category", offer.getCategory());
            confirmMsg.put("price", offer.getPrice());
            confirmMsg.put("message", "Booking confirmed successfully!");

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.BOOKING_EXCHANGE,
                    RabbitMQConfig.CONFIRMATION_ROUTING_KEY,
                    confirmMsg
            );

            statsManager.incrementConfirmed();
            return saved;

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {

            userServiceClient.refundBalance(customerId, offer.getPrice());
            sendFailureNotification(customerId, customerName,
                    "Booking failed unexpectedly. Balance refunded.");
            sendPaymentFailedNotification(customerId, customerName,
                    "Payment failed unexpectedly - balance refunded.");
            statsManager.incrementCancelled();
            throw new RuntimeException("Booking failed! Balance has been refunded.");
        }
    }


    public List<Booking> getMyBookings(Long customerId) {
        return bookingRepository.findByCustomerId(customerId);
    }


    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public List<Booking> getProviderCompletedBookings(Long providerId) {
        return bookingRepository.findByProviderIdAndStatus(providerId, BookingStatus.CONFIRMED);
    }

    public List<Booking> getProviderBookings(Long providerId) {
        return bookingRepository.findByProviderId(providerId);
    }

    public Map<String, Integer> getStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("total", statsManager.getTotalBookings());
        stats.put("confirmed", statsManager.getConfirmedBookings());
        stats.put("cancelled", statsManager.getCancelledBookings());
        return stats;
    }

    public ServiceRequest createServiceRequest(Long customerId, String customerName,
                                                String category, Double maxPrice, LocalDate requiredDate) {
        ServiceRequest request = new ServiceRequest();
        request.setCustomerId(customerId);
        request.setCustomerName(customerName);
        request.setCategory(category);
        request.setMaxPrice(maxPrice);
        request.setRequiredDate(requiredDate);
        request.setStatus(ServiceRequestStatus.PENDING);

        ServiceRequest saved = serviceRequestRepository.save(request);

        try {
            List<OfferResponse> matches = offerServiceClient.getMatchingOffers(category, maxPrice, requiredDate);
            if (matches != null && !matches.isEmpty()) {
                OfferResponse matchedOffer = matches.get(0);
                saved.setMatchedOfferId(matchedOffer.getId());
                saved.setStatus(ServiceRequestStatus.MATCHED);
                serviceRequestRepository.save(saved);

                Map<String, Object> notifMsg = new HashMap<>();
                notifMsg.put("requestId", saved.getId());
                notifMsg.put("providerId", matchedOffer.getProviderId());
                notifMsg.put("providerName", matchedOffer.getProviderName());
                notifMsg.put("category", category);
                notifMsg.put("maxPrice", maxPrice);
                notifMsg.put("requiredDate", requiredDate.toString());
                notifMsg.put("customerName", customerName);

                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.BOOKING_EXCHANGE,
                        RabbitMQConfig.SERVICE_REQUEST_ROUTING_KEY,
                        notifMsg
                );
            }
        } catch (Exception e) {
        }

        return saved;
    }

    public Booking acceptServiceRequest(Long requestId, Long providerId, String providerName) {
        ServiceRequest request = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Service request not found"));

        if (request.getStatus() != ServiceRequestStatus.MATCHED) {
            throw new RuntimeException("Service request is not in MATCHED state");
        }

        if (request.getMatchedOfferId() == null) {
            throw new RuntimeException("No matched offer for this request");
        }

        Booking booking = createBooking(request.getCustomerId(), request.getCustomerName(),
                request.getMatchedOfferId());

        // Update request status
        request.setStatus(ServiceRequestStatus.ACCEPTED);
        serviceRequestRepository.save(request);

        return booking;
    }

    public List<ServiceRequest> getMyServiceRequests(Long customerId) {
        return serviceRequestRepository.findByCustomerId(customerId);
    }


    private void sendFailureNotification(Long customerId, String customerName, String message) {
        Map<String, Object> failureMsg = new HashMap<>();
        failureMsg.put("customerId", customerId);
        failureMsg.put("customerName", customerName);
        failureMsg.put("message", message);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.BOOKING_EXCHANGE,
                RabbitMQConfig.FAILURE_ROUTING_KEY,
                failureMsg
        );
    }

    private void sendPaymentFailedNotification(Long customerId, String customerName, String message) {
        Map<String, Object> paymentMsg = new HashMap<>();
        paymentMsg.put("customerId", customerId);
        paymentMsg.put("customerName", customerName);
        paymentMsg.put("message", message);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PAYMENT_EXCHANGE,
                RabbitMQConfig.PAYMENT_FAILED_ROUTING_KEY,
                paymentMsg
        );
    }
}