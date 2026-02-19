package com.homecraft.api.service;

import java.util.List;
import java.util.Map;

public interface AdminService {

    // Customers
    List<Map<String, Object>> getCustomers();
    void deleteCustomer(Integer id);

    // Sellers
    List<Map<String, Object>> getPendingSellers();
    void acceptSeller(Integer id);
    void rejectSeller(Integer id);

    List<Map<String, Object>> getConfirmedSellers();
    void deleteSeller(Integer id);

    List<Map<String, Object>> getRejectedSellers();
}