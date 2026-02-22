package com.homecraft.api.controller;

import com.homecraft.api.dto.RegisterCustomerDTO;
import com.homecraft.api.dto.RegisterSellerDTO;
import com.homecraft.api.service.RegisterService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/register")
public class RegisterController {

    private final RegisterService registerService;

    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    // Customer
    @PostMapping(value = "/customer", consumes = "multipart/form-data")
    public ResponseEntity<?> registerCustomer(
            @Valid @ModelAttribute RegisterCustomerDTO dto
    ) {
        try {
            registerService.registerCustomer(dto);
            return ResponseEntity.ok("Customer registered");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Registration failed");
        }
    }

    // Seller
    @PostMapping(value = "/seller", consumes = "multipart/form-data")
    public ResponseEntity<?> registerSeller(
            @Valid @ModelAttribute RegisterSellerDTO dto
    ) {
        try {
            registerService.registerSeller(dto);
            return ResponseEntity.ok("Seller registered. Pending admin verification.");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Registration failed");
        }
    }
}