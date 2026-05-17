package com.homeservices.Bookingservice.repository;

import com.homeservices.Bookingservice.entity.Booking;
import com.homeservices.Bookingservice.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByCustomerId(Long customerId);

    List<Booking> findByStatus(BookingStatus status);

    List<Booking> findByOfferId(Long offerId);

    List<Booking> findByProviderId(Long providerId);

    List<Booking> findByProviderIdAndStatus(Long providerId, BookingStatus status);
}