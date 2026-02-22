package com.homecraft.api.service;

import com.homecraft.api.dto.RegisterCustomerDTO;
import com.homecraft.api.dto.RegisterSellerDTO;
import com.homecraft.api.entity.Customer;
import com.homecraft.api.entity.Seller;
import com.homecraft.api.repository.CustomerRepository;
import com.homecraft.api.repository.SellerRepository;
import org.springframework.stereotype.Service;

@Service
public class RegisterServiceImpl implements RegisterService {

    private final CustomerRepository customerRepo;
    private final SellerRepository sellerRepo;
    private final PasswordService passwordService;
    private final FileStorageService fileStorage;

    public RegisterServiceImpl(
            CustomerRepository customerRepo,
            SellerRepository sellerRepo,
            PasswordService passwordService,
            FileStorageService fileStorage
    ) {
        this.customerRepo = customerRepo;
        this.sellerRepo = sellerRepo;
        this.passwordService = passwordService;
        this.fileStorage = fileStorage;
    }

    // Customer
    @Override
    public void registerCustomer(RegisterCustomerDTO dto) throws Exception {

        if (customerRepo.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        String fileName = fileStorage.store(
                dto.getProfileImage(),
                "uploads/customers"
        );

        Customer customer = new Customer();
        customer.setName(dto.getName());
        customer.setLocation(dto.getLocation());
        customer.setInterests(dto.getInterests());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setProfileImage("/uploads/customers/" + fileName);
        customer.setPasswordHash(passwordService.hash(dto.getPassword()));

        customerRepo.save(customer);
    }

    // Seller
    @Override
    public void registerSeller(RegisterSellerDTO dto) throws Exception {

        if (sellerRepo.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        String profileFile = fileStorage.store(
                dto.getProfileImage(),
                "uploads/sellers"
        );

        String aadharFile = fileStorage.store(
                dto.getAadharImage(),
                "uploads/sellers/aadhar"
        );

        Seller seller = new Seller();
        seller.setName(dto.getName());
        seller.setLocation(dto.getLocation());
        seller.setCraft(dto.getCraft());
        seller.setEmail(dto.getEmail());
        seller.setPhone(dto.getPhone());
        seller.setLicenseNumber(dto.getLicenseNumber());
        seller.setProfileImage("/uploads/sellers/" + profileFile);
        seller.setAadharImage("/uploads/sellers/aadhar/" + aadharFile);
        seller.setPasswordHash(passwordService.hash(dto.getPassword()));
        seller.setIsVerified(false);

        sellerRepo.save(seller);
    }
}