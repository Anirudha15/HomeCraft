package com.homecraft.api.repository;

import com.homecraft.api.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface SellerRepository extends JpaRepository<Seller, Integer> {

    Optional<Seller> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Seller> findByEmailAndIsVerified(String email, Boolean isVerified);

    List<Seller> findByIsVerifiedFalse();

    List<Seller> findByIsVerifiedTrue();
}