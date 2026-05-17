package com.homeservices.notificationservice.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingSuccessEvent implements Serializable {
    private Long userId;
    private Long providerId;
    private String message;
}