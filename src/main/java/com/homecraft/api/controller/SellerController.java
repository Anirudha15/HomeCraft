package com.homecraft.api.controller;

import com.homecraft.api.dto.AddProductDTO;
import com.homecraft.api.service.SellerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seller")
public class SellerController {

    private final SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    private Integer sellerId(Authentication auth) {
        return Integer.parseInt(auth.getName());
    }

    @GetMapping("/profile")
    public ResponseEntity<?> profile(Authentication auth) {
        return ResponseEntity.ok(
                sellerService.getProfile(sellerId(auth))
        );
    }

    @PostMapping(value = "/product", consumes = "multipart/form-data")
    public ResponseEntity<?> addProduct(
            Authentication auth,
            @Valid @ModelAttribute AddProductDTO dto
    ) throws Exception {
        sellerService.addProduct(sellerId(auth), dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/products")
    public ResponseEntity<?> products(Authentication auth) {
        return ResponseEntity.ok(
                sellerService.getProducts(sellerId(auth))
        );
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<?> delete(
            Authentication auth,
            @PathVariable Integer id
    ) {
        sellerService.deleteProduct(sellerId(auth), id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/orders/confirmed/customers")
    public ResponseEntity<?> confirmedCustomers(Authentication auth) {
        return ResponseEntity.ok(
                sellerService.getConfirmedCustomers(sellerId(auth))
        );
    }

    @GetMapping("/orders/confirmed/customer/{customerId}")
    public ResponseEntity<?> customerOrders(
            Authentication auth,
            @PathVariable Integer customerId
    ) {
        return ResponseEntity.ok(
                sellerService.getCustomerOrderDetails(
                        sellerId(auth), customerId)
        );
    }
}