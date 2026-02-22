package com.homecraft.api.service;

import com.homecraft.api.entity.*;
import com.homecraft.api.repository.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final CustomerRepository customerRepo;
    private final SellerRepository sellerRepo;
    private final SellerRejectedRepository rejectedRepo;
    private final AdminRepository adminRepo;
    private final PasswordService passwordService;

    public AuthServiceImpl(
            CustomerRepository customerRepo,
            SellerRepository sellerRepo,
            SellerRejectedRepository rejectedRepo,
            AdminRepository adminRepo,
            PasswordService passwordService
    ) {
        this.customerRepo = customerRepo;
        this.sellerRepo = sellerRepo;
        this.rejectedRepo = rejectedRepo;
        this.adminRepo = adminRepo;
        this.passwordService = passwordService;
    }

    @Override
    public AuthResult login(String email, String password) {

        // Admin
        Optional<Admin> admin = adminRepo.findByEmail(email);
        if (admin.isPresent()) {

            if (!passwordService.matches(password, admin.get().getPasswordHash())) {
                return fail("Invalid email or password");
            }

            return success(
                    admin.get().getId(),
                    "ROLE_ADMIN",
                    email
            );
        }

        // Seller Rejected
        Optional<SellerRejected> rejected = rejectedRepo.findByEmail(email);
        if (rejected.isPresent()) {

            boolean valid = passwordService.matches(
                    password,
                    rejected.get().getPasswordHash()
            );

            if (valid) {
                return fail("Registration rejected. Re-create account.");
            }
            return fail("Invalid email or password");
        }

        // Customer
        Optional<Customer> customer = customerRepo.findByEmail(email);
        if (customer.isPresent()) {

            if (!passwordService.matches(password, customer.get().getPasswordHash())) {
                return fail("Invalid email or password");
            }

            return success(
                    customer.get().getId(),
                    "ROLE_CUSTOMER",
                    email
            );
        }

        // Seller
        Optional<Seller> seller = sellerRepo.findByEmail(email);
        if (seller.isPresent()) {

            if (!passwordService.matches(password, seller.get().getPasswordHash())) {
                return fail("Invalid email or password");
            }

            if (!seller.get().getIsVerified()) {
                return fail("Pending verification from admin.");
            }

            return success(
                    seller.get().getId(),
                    "ROLE_SELLER",
                    email
            );
        }

        return fail("Invalid email or password");
    }

    // Helpers

    private AuthResult success(Integer userId, String role, String email) {
        AuthResult result = new AuthResult();
        result.setSuccess(true);
        result.setUserId(userId);
        result.setRole(role);
        result.setEmail(email);
        return result;
    }

    private AuthResult fail(String message) {
        AuthResult result = new AuthResult();
        result.setSuccess(false);
        result.setErrorMessage(message);
        return result;
    }
}