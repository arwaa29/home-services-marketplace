package com.homeservices.Bookingservice.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class UserServiceClient {

    private final RestTemplate restTemplate;

    private static final String USER_SERVICE_URL = "http://localhost:8081";

    // get user balance
    public Double getUserBalance(Long userId) {
        String url = USER_SERVICE_URL + "/users/" + userId + "/balance-internal";
        return restTemplate.getForObject(url, Double.class);
    }

    // deduct balance
    public void deductBalance(Long userId, Double amount) {
        String url = USER_SERVICE_URL + "/users/" + userId + "/deduct?amount=" + amount;
        restTemplate.put(url, null);
    }

    // refund balance (rollback)
    public void refundBalance(Long userId, Double amount) {
        String url = USER_SERVICE_URL + "/users/" + userId + "/add-funds-internal?amount=" + amount;
        restTemplate.put(url, null);
    }
}