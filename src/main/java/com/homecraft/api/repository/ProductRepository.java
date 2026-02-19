package com.homecraft.api.repository;

import com.homecraft.api.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findBySellerId(Integer sellerId);

    List<Product> findByCategory(String category);

    List<Product> findByLocation(String location);

    @Modifying
    @Transactional
    void deleteByIdAndSellerId(Integer id, Integer sellerId);
}