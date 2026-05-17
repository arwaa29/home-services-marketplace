package com.homeservices.offerservice.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class OfferUpdateDTO {
    private Double price;
    private LocalDate availableDate;
}
