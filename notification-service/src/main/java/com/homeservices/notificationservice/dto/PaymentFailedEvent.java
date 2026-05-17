package com.homeservices.notificationservice.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentFailedEvent implements Serializable {
    private Long userId;
    private String message;
}