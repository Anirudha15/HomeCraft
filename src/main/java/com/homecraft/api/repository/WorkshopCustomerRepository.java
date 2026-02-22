package com.homecraft.api.repository;

import com.homecraft.api.entity.WorkshopCustomer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkshopCustomerRepository
        extends JpaRepository<WorkshopCustomer, Integer> {

    Optional<WorkshopCustomer> findByWorkshopId(int workshopId);

    List<WorkshopCustomer> findByCustListContaining(String email);
}