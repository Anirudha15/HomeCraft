package com.homecraft.api.repository;

import com.homecraft.api.entity.CustRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustRequestRepository extends JpaRepository<CustRequest, Integer> {

    List<CustRequest> findByOrdersId(Integer ordersId);

    List<CustRequest> findByCustomerId(Integer customerId);

    List<CustRequest> findBySellerId(Integer sellerId);
}