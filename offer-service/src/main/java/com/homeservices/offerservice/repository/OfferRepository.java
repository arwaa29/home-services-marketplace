package com.homeservices.offerservice.repository;

import com.homeservices.offerservice.entity.OfferEntity;
import com.homeservices.offerservice.entity.OfferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OfferRepository extends JpaRepository<OfferEntity, Long> {

    List<OfferEntity> findByCategory(String category);

    List<OfferEntity> findByStatus(OfferStatus status);

    List<OfferEntity> findByCategoryAndStatus(String category, OfferStatus status);

    List<OfferEntity> findByProviderId(Long providerId);

    // For service request matching: find active offers in a category within budget on a specific date
    List<OfferEntity> findByCategoryAndStatusAndPriceLessThanEqualAndAvailableDate(
            String category, OfferStatus status, Double maxPrice, LocalDate date);
}