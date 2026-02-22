package com.homecraft.api.service;

import com.homecraft.api.dto.AddProductDTO;
import com.homecraft.api.entity.*;
import com.homecraft.api.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SellerServiceImpl implements SellerService {

    private final SellerRepository sellerRepo;
    private final ProductRepository productRepo;
    private final OrderRepository orderRepo;
    private final CustRequestRepository requestRepo;
    private final CustomerRepository customerRepo;
    private final FileStorageService fileStorage;

    public SellerServiceImpl(
            SellerRepository sellerRepo,
            ProductRepository productRepo,
            OrderRepository orderRepo,
            CustRequestRepository requestRepo,
            CustomerRepository customerRepo,
            FileStorageService fileStorage
    ) {
        this.sellerRepo = sellerRepo;
        this.productRepo = productRepo;
        this.orderRepo = orderRepo;
        this.requestRepo = requestRepo;
        this.customerRepo = customerRepo;
        this.fileStorage = fileStorage;
    }

    // Profile
    @Override
    public Map<String, Object> getProfile(Integer sellerId) {

        Seller s = sellerRepo.findById(sellerId).orElseThrow();

        return Map.of(
                "name", s.getName(),
                "phone", s.getPhone(),
                "email", s.getEmail(),
                "location", s.getLocation(),
                "licenseNumber", s.getLicenseNumber(),
                "craft", s.getCraft(),
                "profileImage", s.getProfileImage()
        );
    }

    // Add Products
    @Override
    public void addProduct(Integer sellerId, AddProductDTO dto) throws Exception {

        Seller seller = sellerRepo.findById(sellerId).orElseThrow();

        String imageName = fileStorage.store(
                dto.getImage(),
                "uploads/sellers/product/image"
        );

        Product p = new Product();
        p.setSellerId(sellerId);
        p.setName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setImagePath("/uploads/sellers/product/image/" + imageName);
        p.setCategory(dto.getCategory());
        p.setType(dto.getType());
        p.setPrice(dto.getPrice());
        p.setCustomizations(dto.getCustomizations());
        p.setLocation(seller.getLocation());

        productRepo.save(p);
    }

    // Products
    @Override
    public List<Map<String, Object>> getProducts(Integer sellerId) {

        return productRepo.findBySellerId(sellerId)
                .stream()
                .map(p -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", p.getId());
                    m.put("name", p.getName());
                    m.put("image", p.getImagePath());
                    m.put("type", p.getType());
                    m.put("price", p.getPrice());
                    m.put("customizations",
                            p.getCustomizations() == null ? "None" : p.getCustomizations());
                    return m;
                })
                .toList();
    }

    @Override
    @Transactional
    public void deleteProduct(Integer sellerId, Integer productId) {
        productRepo.deleteByIdAndSellerId(productId, sellerId);
    }

    // Orders
    @Override
    public List<Map<String, Object>> getConfirmedCustomers(Integer sellerId) {

        Map<Integer, Map<String, Object>> grouped = new LinkedHashMap<>();

        orderRepo.findBySellerIdAndStatus(sellerId, "Confirmed")
                .forEach(o -> {
                    grouped.computeIfAbsent(o.getCustomerId(), cid -> {
                        Customer c = customerRepo.findById(cid).orElseThrow();
                        Map<String, Object> m = new HashMap<>();
                        m.put("customerId", c.getId());
                        m.put("customerName", c.getName());
                        m.put("grandTotal", o.getPrice().multiply(
                                new java.math.BigDecimal(o.getQuantity())));
                        return m;
                    });

                    Map<String, Object> m = grouped.get(o.getCustomerId());
                    java.math.BigDecimal current =
                            (java.math.BigDecimal) m.get("grandTotal");

                    m.put("grandTotal",
                            current.add(o.getPrice()
                                    .multiply(new java.math.BigDecimal(o.getQuantity()))));
                });

        return new ArrayList<>(grouped.values());
    }

    @Override
    public List<Map<String, Object>> getCustomerOrderDetails(
            Integer sellerId,
            Integer customerId
    ) {

        return orderRepo.findBySellerIdAndCustomerIdAndStatus(
                        sellerId, customerId, "Confirmed")
                .stream()
                .map(o -> {
                    Map<String, Object> m = new HashMap<>();
                    Product p = productRepo.findById(o.getProductId()).orElseThrow();

                    m.put("productName", p.getName());
                    m.put("price", o.getPrice());
                    m.put("quantity", o.getQuantity());
                    m.put("total",
                            o.getPrice().multiply(
                                    new java.math.BigDecimal(o.getQuantity())));

                    requestRepo.findByOrdersId(o.getId())
                            .stream()
                            .findFirst()
                            .ifPresentOrElse(
                                    r -> m.put("customization", r.getInfo()),
                                    () -> m.put("customization", "—")
                            );

                    return m;
                })
                .toList();
    }
}