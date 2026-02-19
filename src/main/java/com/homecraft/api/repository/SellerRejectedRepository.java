package com.homecraft.api.repository;

import com.homecraft.api.entity.SellerRejected;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerRejectedRepository extends JpaRepository<SellerRejected, Integer> {

    Optional<SellerRejected> findByEmail(String email);

    boolean existsByEmail(String email);


}