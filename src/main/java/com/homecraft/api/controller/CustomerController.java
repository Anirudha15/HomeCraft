package com.homecraft.api.controller;

import com.homecraft.api.dto.*;
import com.homecraft.api.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    private Integer userId(Authentication auth) {
        return Integer.parseInt(auth.getName());
    }

    @GetMapping("/profile")
    public ResponseEntity<?> profile(Authentication auth) {
        return ResponseEntity.ok(
                customerService.getProfile(userId(auth))
        );
    }

    @GetMapping("/products")
    public ResponseEntity<?> products(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String location
    ) {
        return ResponseEntity.ok(
                customerService.getProducts(category, type, location)
        );
    }

    @GetMapping("/product-locations")
    public ResponseEntity<?> locations() {
        return ResponseEntity.ok(customerService.getProductLocations());
    }

    @PostMapping(
            value = "/cart/add",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public ResponseEntity<?> addToCart(
            Authentication auth,
            @RequestParam Integer productId,
            @RequestParam Integer quantity
    ) {
        CartAddDTO dto = new CartAddDTO();
        dto.setProductId(productId);
        dto.setQuantity(quantity);

        customerService.addToCart(userId(auth), dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/cart")
    public ResponseEntity<?> cart(Authentication auth) {
        return ResponseEntity.ok(
                customerService.getCart(userId(auth))
        );
    }

    @PutMapping(
            value = "/cart/qty",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public ResponseEntity<?> updateQty(
            @RequestParam Integer orderId,
            @RequestParam Integer delta
    ) {
        CartQtyDTO dto = new CartQtyDTO();
        dto.setOrderId(orderId);
        dto.setDelta(delta);

        customerService.updateQuantity(dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/cart/{id}")
    public ResponseEntity<?> remove(@PathVariable Integer id) {
        customerService.removeFromCart(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping(
            value = "/customize",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public ResponseEntity<?> customize(
            Authentication auth,
            @RequestParam Integer orderId,
            @RequestParam String info
    ) {
        CustomizeDTO dto = new CustomizeDTO();
        dto.setOrderId(orderId);
        dto.setInfo(info);

        customerService.customizeOrder(userId(auth), dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/order/confirm")
    public ResponseEntity<?> confirm(Authentication auth) {
        customerService.confirmOrder(userId(auth));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/order/cancel")
    public ResponseEntity<?> cancel(Authentication auth) {
        customerService.cancelOrder(userId(auth));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/orders/confirmed")
    public ResponseEntity<?> confirmed(Authentication auth) {
        return ResponseEntity.ok(
                customerService.getConfirmedOrders(userId(auth))
        );
    }
}