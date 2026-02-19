package com.homecraft.api.controller;

import com.homecraft.api.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('Admin')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // Customers
    @GetMapping("/customers")
    public ResponseEntity<?> customers() {
        return ResponseEntity.ok(adminService.getCustomers());
    }

    @DeleteMapping("/customer/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Integer id) {
        adminService.deleteCustomer(id);
        return ResponseEntity.ok().build();
    }

    // Sellers
    @GetMapping("/sellers/pending")
    public ResponseEntity<?> pendingSellers() {
        return ResponseEntity.ok(adminService.getPendingSellers());
    }

    @PostMapping("/seller/accept/{id}")
    public ResponseEntity<?> acceptSeller(@PathVariable Integer id) {
        adminService.acceptSeller(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/seller/reject/{id}")
    public ResponseEntity<?> rejectSeller(@PathVariable Integer id) {
        adminService.rejectSeller(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sellers/confirmed")
    public ResponseEntity<?> confirmedSellers() {
        return ResponseEntity.ok(adminService.getConfirmedSellers());
    }

    @DeleteMapping("/seller/{id}")
    public ResponseEntity<?> deleteSeller(@PathVariable Integer id) {
        adminService.deleteSeller(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sellers/rejected")
    public ResponseEntity<?> rejectedSellers() {
        return ResponseEntity.ok(adminService.getRejectedSellers());
    }
}