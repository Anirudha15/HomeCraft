package com.homecraft.api.repository;

import com.homecraft.api.entity.Workshop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WorkshopRepository extends JpaRepository<Workshop, Integer> {

    List<Workshop> findBySellerId(int sellerId);

    List<Workshop> findByStatus(String status);

    @Query("SELECT DISTINCT w.location FROM Workshop w")
    List<String> findDistinctLocations();
}