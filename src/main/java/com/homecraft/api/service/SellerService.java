package com.homecraft.api.service;

import com.homecraft.api.dto.AddProductDTO;

import java.util.List;
import java.util.Map;

public interface SellerService {

    Map<String, Object> getProfile(Integer sellerId);

    void addProduct(Integer sellerId, AddProductDTO dto) throws Exception;

    List<Map<String, Object>> getProducts(Integer sellerId);

    void deleteProduct(Integer sellerId, Integer productId);

    List<Map<String, Object>> getConfirmedCustomers(Integer sellerId);

    List<Map<String, Object>> getCustomerOrderDetails(
            Integer sellerId,
            Integer customerId
    );
}