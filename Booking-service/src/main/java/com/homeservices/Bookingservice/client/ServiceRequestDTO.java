package com.homeservices.Bookingservice.client;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ServiceRequestDTO {
    private String category;
    private Double maxPrice;
    private LocalDate requiredDate;
}
