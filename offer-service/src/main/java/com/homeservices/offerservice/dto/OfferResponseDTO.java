package com.homeservices.offerservice.dto;

import com.homeservices.offerservice.entity.OfferStatus;
import lombok.Data;
import java.time.LocalDate;

@Data
public class OfferResponseDTO {
    private Long id;
    private Long providerId;
    private String providerName;
    private String category;
    private String description;
    private Double price;
    private LocalDate availableDate;
    private OfferStatus status;
}
