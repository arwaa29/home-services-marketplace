package com.homeservices.Bookingservice.client;

import lombok.Data;
import java.time.LocalDate;

@Data
public class OfferResponse {
    private Long id;
    private Long providerId;
    private String providerName;
    private String category;
    private String description;
    private Double price;
    private LocalDate availableDate;
    private String status;
}