package com.homeservices.notificationservice.listener;

import com.homeservices.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationService service;

    @RabbitListener(queues = "booking.confirmation")
    public void handleSuccess(Map<String, Object> event) {
        Long customerId = ((Number) event.get("customerId")).longValue();
        String customerName = (String) event.get("customerName");
        String providerName = (String) event.get("providerName");
        String category = (String) event.get("category");
        Object price = event.get("price");
        String message = (String) event.get("message");

        // Notify customer
        service.save(customerId,
                "Booking confirmed! Service: " + category + " by " + providerName +
                        " - $" + price + ". " + message,
                "SUCCESS");

        // Notify provider
        if (event.containsKey("providerId") && event.get("providerId") != null) {
            Long providerId = ((Number) event.get("providerId")).longValue();
            service.save(providerId,
                    "New booking! Customer: " + customerName +
                            " booked your " + category + " service for $" + price,
                    "SUCCESS");
        }
    }

    @RabbitListener(queues = "booking.failure")
    public void handleFailure(Map<String, Object> event) {
        Long customerId = ((Number) event.get("customerId")).longValue();
        String message = (String) event.get("message");
        service.save(customerId, "Booking FAILED: " + message, "FAILED");
    }

    @RabbitListener(queues = "payment.failed.queue")
    public void handlePaymentFailed(Map<String, Object> event) {
        Long customerId = ((Number) event.get("customerId")).longValue();
        String message = (String) event.get("message");
        service.save(customerId,
                "ADMIN ALERT: Payment failed for customer ID " + customerId + " - " + message,
                "ADMIN");
    }

    @RabbitListener(queues = "service.request.notification")
    public void handleServiceRequestMatch(Map<String, Object> event) {
        Long providerId = ((Number) event.get("providerId")).longValue();
        String category = (String) event.get("category");
        Object maxPrice = event.get("maxPrice");
        String requiredDate = String.valueOf(event.get("requiredDate"));
        Long requestId = ((Number) event.get("requestId")).longValue();

        service.save(providerId,
                "New service request matching your services! Category: " + category +
                        ", Budget: $" + maxPrice + ", Date: " + requiredDate +
                        ". Request ID: " + requestId + ". Accept it via the API.",
                "SERVICE_REQUEST");
    }
}