package com.homeservices.notificationservice.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingFailedEvent implements Serializable {
    private Long userId;
    private String reason;
}