package com.homeservices.offerservice.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class OfferRequestDTO {
    private String category;
    private String description;
    private Double price;
    private LocalDate availableDate;
}