package com.homecraft.api.repository;

import com.homecraft.api.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByCustomerId(Integer customerId);

    List<Order> findBySellerId(Integer sellerId);

    List<Order> findByProductId(Integer productId);

    List<Order> findBySellerIdAndStatus(Integer sellerId, String status);

    List<Order> findBySellerIdAndCustomerIdAndStatus(
            Integer sellerId,
            Integer customerId,
            String status
    );
}