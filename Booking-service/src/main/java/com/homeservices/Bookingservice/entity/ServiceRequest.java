package com.homeservices.Bookingservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "service_requests")
public class ServiceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private Double maxPrice;

    @Column(nullable = false)
    private LocalDate requiredDate;

    // Matched offer ID (set when system finds a match)
    private Long matchedOfferId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceRequestStatus status;
}
