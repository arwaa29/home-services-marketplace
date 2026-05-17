package com.homeservices.offerservice.service;

import com.homeservices.offerservice.entity.OfferEntity;
import com.homeservices.offerservice.entity.OfferStatus;
import com.homeservices.offerservice.entity.ServiceCategory;
import com.homeservices.offerservice.repository.OfferRepository;
import com.homeservices.offerservice.repository.ServiceCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OfferService {

    private final OfferRepository offerRepository;
    private final ServiceCategoryRepository categoryRepository;

    public OfferEntity createOffer(OfferEntity offer) {
        offer.setStatus(OfferStatus.ACTIVE);
        return offerRepository.save(offer);
    }

    public List<OfferEntity> getAllActiveOffers() {
        return offerRepository.findByStatus(OfferStatus.ACTIVE);
    }

    public List<OfferEntity> getOffersByCategory(String category) {
        return offerRepository.findByCategoryAndStatus(category, OfferStatus.ACTIVE);
    }

    public List<OfferEntity> getMyOffers(Long providerId) {
        return offerRepository.findByProviderId(providerId);
    }

    public OfferEntity updateOffer(Long offerId, Long providerId, Double newPrice, LocalDate newDate) {
        OfferEntity offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found"));

        if (!offer.getProviderId().equals(providerId)) {
            throw new RuntimeException("You can only update your own offers");
        }
        if (newPrice != null) offer.setPrice(newPrice);
        if (newDate != null) offer.setAvailableDate(newDate);

        return offerRepository.save(offer);
    }

    public List<OfferEntity> getAllOffers() {
        return offerRepository.findAll();
    }

    public OfferEntity markAsCompleted(Long offerId) {
        OfferEntity offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found"));
        offer.setStatus(OfferStatus.COMPLETED);
        return offerRepository.save(offer);
    }

    public OfferEntity getOfferById(Long offerId) {
        return offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found"));
    }


    public List<OfferEntity> findMatchingOffers(String category, Double maxPrice, LocalDate date) {
        return offerRepository.findByCategoryAndStatusAndPriceLessThanEqualAndAvailableDate(
                category, OfferStatus.ACTIVE, maxPrice, date);
    }


    public ServiceCategory addCategory(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new RuntimeException("Category already exists: " + name);
        }
        ServiceCategory category = new ServiceCategory();
        category.setName(name);
        return categoryRepository.save(category);
    }

    public List<ServiceCategory> getAllCategories() {
        return categoryRepository.findAll();
    }
}