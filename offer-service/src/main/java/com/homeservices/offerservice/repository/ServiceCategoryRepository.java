package com.homeservices.offerservice.repository;

import com.homeservices.offerservice.entity.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, Long> {
    boolean existsByName(String name);
}
