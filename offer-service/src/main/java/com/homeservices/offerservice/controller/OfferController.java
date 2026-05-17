package com.homeservices.offerservice.controller;

import com.homeservices.offerservice.config.JwtUtil;
import com.homeservices.offerservice.dto.OfferRequestDTO;
import com.homeservices.offerservice.dto.OfferResponseDTO;
import com.homeservices.offerservice.dto.OfferUpdateDTO;
import com.homeservices.offerservice.entity.OfferEntity;
import com.homeservices.offerservice.entity.ServiceCategory;
import com.homeservices.offerservice.service.OfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/offers")
@RequiredArgsConstructor
public class OfferController {

    private final OfferService offerService;
    private final JwtUtil jwtUtil;

    @PostMapping("/create")
    public ResponseEntity<OfferResponseDTO> createOffer(@RequestHeader("Authorization") String authHeader,
                                                        @RequestBody OfferRequestDTO request) {
        String token = authHeader.substring(7);
        Long providerId = jwtUtil.extractUserId(token);
        String providerName = jwtUtil.extractUsername(token);

        OfferEntity offer = new OfferEntity();
        offer.setProviderId(providerId);
        offer.setProviderName(providerName);
        offer.setCategory(request.getCategory());
        offer.setDescription(request.getDescription());
        offer.setPrice(request.getPrice());
        offer.setAvailableDate(request.getAvailableDate());

        return ResponseEntity.ok(mapToDTO(offerService.createOffer(offer)));
    }

    @GetMapping("/my-offers")
    public ResponseEntity<List<OfferResponseDTO>> getMyOffers(@RequestHeader("Authorization") String authHeader) {
        Long providerId = jwtUtil.extractUserId(authHeader.substring(7));
        return ResponseEntity.ok(
                offerService.getMyOffers(providerId)
                        .stream().map(this::mapToDTO).toList()
        );
    }

    @PutMapping("/update/{offerId}")
    public ResponseEntity<OfferResponseDTO> updateOffer(@RequestHeader("Authorization") String authHeader,
                                                        @PathVariable Long offerId,
                                                        @RequestBody OfferUpdateDTO request) {
        Long providerId = jwtUtil.extractUserId(authHeader.substring(7));
        return ResponseEntity.ok(mapToDTO(offerService.updateOffer(offerId, providerId,
                                          request.getPrice(), request.getAvailableDate())
        ));
    }


    @GetMapping("/category/{category}")
    public ResponseEntity<List<OfferResponseDTO>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(offerService.getOffersByCategory(category)
                        .stream().map(this::mapToDTO).toList()
        );
    }

    @GetMapping("/all")
    public ResponseEntity<List<OfferResponseDTO>> getAllOffers() {
        return ResponseEntity.ok(offerService.getAllOffers()
                        .stream().map(this::mapToDTO).toList()
        );
    }

    @GetMapping("/{offerId}")
    public ResponseEntity<OfferResponseDTO> getOfferById(@PathVariable Long offerId) {
        return ResponseEntity.ok(mapToDTO(offerService.getOfferById(offerId)));
    }

    @PutMapping("/complete/{offerId}")
    public ResponseEntity<OfferResponseDTO> markAsCompleted(@PathVariable Long offerId) {
        return ResponseEntity.ok(mapToDTO(offerService.markAsCompleted(offerId)));
    }

    @GetMapping("/match")
    public ResponseEntity<List<OfferResponseDTO>> getMatchingOffers(
            @RequestParam String category,
            @RequestParam Double maxPrice,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(offerService.findMatchingOffers(category, maxPrice, date)
                .stream().map(this::mapToDTO).toList()
        );
    }


    @PostMapping("/admin/categories")
    public ResponseEntity<ServiceCategory> addCategory(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        return ResponseEntity.ok(offerService.addCategory(name));
    }

    @GetMapping("/admin/categories")
    public ResponseEntity<List<ServiceCategory>> getAllCategories() {
        return ResponseEntity.ok(offerService.getAllCategories());
    }


    private OfferResponseDTO mapToDTO(OfferEntity offer) {
        OfferResponseDTO dto = new OfferResponseDTO();
        dto.setId(offer.getId());
        dto.setProviderId(offer.getProviderId());
        dto.setProviderName(offer.getProviderName());
        dto.setCategory(offer.getCategory());
        dto.setDescription(offer.getDescription());
        dto.setPrice(offer.getPrice());
        dto.setAvailableDate(offer.getAvailableDate());
        dto.setStatus(offer.getStatus());
        return dto;
    }
}
