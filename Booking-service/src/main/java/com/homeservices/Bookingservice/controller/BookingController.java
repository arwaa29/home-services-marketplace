package com.homeservices.Bookingservice.controller;

import com.homeservices.Bookingservice.client.ServiceRequestDTO;
import com.homeservices.Bookingservice.config.JwtUtil;
import com.homeservices.Bookingservice.entity.Booking;
import com.homeservices.Bookingservice.entity.ServiceRequest;
import com.homeservices.Bookingservice.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final JwtUtil jwtUtil;

    @PostMapping("/book/{offerId}")
    public ResponseEntity<?> createBooking(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long offerId) {

        String token = authHeader.substring(7);
        Long customerId = jwtUtil.extractUserId(token);
        String customerName = jwtUtil.extractUsername(token);

        Booking booking = bookingService.createBooking(customerId, customerName, offerId);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<List<Booking>> getMyBookings(
            @RequestHeader("Authorization") String authHeader) {
        Long customerId = jwtUtil.extractUserId(authHeader.substring(7));
        return ResponseEntity.ok(bookingService.getMyBookings(customerId));
    }

    @GetMapping("/provider/completed")
    public ResponseEntity<List<Booking>> getProviderCompletedBookings(
            @RequestHeader("Authorization") String authHeader) {
        Long providerId = jwtUtil.extractUserId(authHeader.substring(7));
        return ResponseEntity.ok(bookingService.getProviderCompletedBookings(providerId));
    }

    @GetMapping("/provider/all")
    public ResponseEntity<List<Booking>> getProviderBookings(
            @RequestHeader("Authorization") String authHeader) {
        Long providerId = jwtUtil.extractUserId(authHeader.substring(7));
        return ResponseEntity.ok(bookingService.getProviderBookings(providerId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Integer>> getStats() {
        return ResponseEntity.ok(bookingService.getStats());
    }

    @PostMapping("/service-request")
    public ResponseEntity<ServiceRequest> createServiceRequest(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ServiceRequestDTO request) {

        String token = authHeader.substring(7);
        Long customerId = jwtUtil.extractUserId(token);
        String customerName = jwtUtil.extractUsername(token);

        ServiceRequest sr = bookingService.createServiceRequest(
                customerId, customerName,
                request.getCategory(), request.getMaxPrice(), request.getRequiredDate());
        return ResponseEntity.ok(sr);
    }

    @GetMapping("/service-requests/my")
    public ResponseEntity<List<ServiceRequest>> getMyServiceRequests(
            @RequestHeader("Authorization") String authHeader) {
        Long customerId = jwtUtil.extractUserId(authHeader.substring(7));
        return ResponseEntity.ok(bookingService.getMyServiceRequests(customerId));
    }

    @PostMapping("/service-request/{requestId}/accept")
    public ResponseEntity<Booking> acceptServiceRequest(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long requestId) {

        String token = authHeader.substring(7);
        Long providerId = jwtUtil.extractUserId(token);
        String providerName = jwtUtil.extractUsername(token);

        Booking booking = bookingService.acceptServiceRequest(requestId, providerId, providerName);
        return ResponseEntity.ok(booking);
    }
}