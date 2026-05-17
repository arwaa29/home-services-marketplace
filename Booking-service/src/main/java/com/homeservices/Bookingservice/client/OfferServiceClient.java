package com.homeservices.Bookingservice.client;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OfferServiceClient {

    private final RestTemplate restTemplate;

    private static final String OFFER_SERVICE_URL = "http://localhost:8082";

    //offer details
    public OfferResponse getOffer(Long offerId) {
        String url = OFFER_SERVICE_URL + "/offers/" + offerId;
        return restTemplate.getForObject(url, OfferResponse.class);
    }

    // offer tmam
    public void markOfferCompleted(Long offerId) {
        String url = OFFER_SERVICE_URL + "/offers/complete/" + offerId;
        restTemplate.put(url, null);
    }

    // find matching offers for service requests
    public List<OfferResponse> getMatchingOffers(String category, Double maxPrice, LocalDate date) {
        String url = OFFER_SERVICE_URL + "/offers/match?category=" + category
                + "&maxPrice=" + maxPrice + "&date=" + date;
        var response = restTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<OfferResponse>>() {});
        return response.getBody();
    }
}