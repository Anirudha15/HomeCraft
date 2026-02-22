package com.homecraft.api.service;

import com.homecraft.api.dto.CartAddDTO;
import com.homecraft.api.dto.CartQtyDTO;
import com.homecraft.api.dto.CustomizeDTO;

import java.util.List;
import java.util.Map;

public interface CustomerService {

    Map<String, Object> getProfile(Integer customerId);

    List<Map<String, Object>> getProducts(
            String category,
            String type,
            String location
    );

    List<String> getProductLocations();

    void addToCart(Integer customerId, CartAddDTO dto);

    List<?> getCart(Integer customerId);

    void updateQuantity(CartQtyDTO dto);

    void removeFromCart(Integer orderId);

    void customizeOrder(Integer customerId, CustomizeDTO dto);

    void confirmOrder(Integer customerId);

    void cancelOrder(Integer customerId);

    List<?> getConfirmedOrders(Integer customerId);
}