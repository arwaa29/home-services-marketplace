package com.homeservices.Bookingservice.repository;

import com.homeservices.Bookingservice.entity.ServiceRequest;
import com.homeservices.Bookingservice.entity.ServiceRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
    List<ServiceRequest> findByCustomerId(Long customerId);
    List<ServiceRequest> findByStatus(ServiceRequestStatus status);
}
