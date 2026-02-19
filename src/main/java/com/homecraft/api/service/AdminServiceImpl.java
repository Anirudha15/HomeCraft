package com.homecraft.api.service;

import com.homecraft.api.entity.*;
import com.homecraft.api.repository.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AdminServiceImpl implements AdminService {

    private final CustomerRepository customerRepo;
    private final SellerRepository sellerRepo;
    private final SellerRejectedRepository rejectedRepo;

    public AdminServiceImpl(
            CustomerRepository customerRepo,
            SellerRepository sellerRepo,
            SellerRejectedRepository rejectedRepo
    ) {
        this.customerRepo = customerRepo;
        this.sellerRepo = sellerRepo;
        this.rejectedRepo = rejectedRepo;
    }

    // Customers
    @Override
    public List<Map<String, Object>> getCustomers() {
        return customerRepo.findAll()
                .stream()
                .map(c -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", c.getId());
                    m.put("name", c.getName());
                    m.put("phone", c.getPhone());
                    m.put("email", c.getEmail());
                    return m;
                })
                .toList();
    }

    @Override
    public void deleteCustomer(Integer id) {
        customerRepo.deleteById(id);
    }

    // Sellers
    @Override
    public List<Map<String, Object>> getPendingSellers() {
        return sellerRepo.findByIsVerifiedFalse()
                .stream()
                .map(s -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", s.getId());
                    m.put("name", s.getName());
                    m.put("phone", s.getPhone());
                    m.put("email", s.getEmail());
                    m.put("license", s.getLicenseNumber());
                    m.put("profile", s.getProfileImage());
                    m.put("aadhar", s.getAadharImage());
                    return m;
                })
                .toList();
    }

    @Override
    public void acceptSeller(Integer id) {
        Seller s = sellerRepo.findById(id).orElseThrow();
        s.setIsVerified(true);
        sellerRepo.save(s);
    }

    @Override
    public void rejectSeller(Integer id) {

        Seller s = sellerRepo.findById(id).orElseThrow();

        SellerRejected r = new SellerRejected();
        r.setEmail(s.getEmail());
        r.setPasswordHash(s.getPasswordHash());

        rejectedRepo.save(r);
        sellerRepo.delete(s);
    }

    @Override
    public List<Map<String, Object>> getConfirmedSellers() {
        return sellerRepo.findByIsVerifiedTrue()
                .stream()
                .map(s -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", s.getId());
                    m.put("name", s.getName());
                    m.put("phone", s.getPhone());
                    m.put("email", s.getEmail());
                    m.put("license", s.getLicenseNumber());
                    m.put("profile", s.getProfileImage());
                    return m;
                })
                .toList();
    }

    @Override
    public void deleteSeller(Integer id) {
        sellerRepo.deleteById(id);
    }

    @Override
    public List<Map<String, Object>> getRejectedSellers() {
        return rejectedRepo.findAll()
                .stream()
                .map(r -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("email", r.getEmail());
                    return m;
                })
                .toList();
    }
}