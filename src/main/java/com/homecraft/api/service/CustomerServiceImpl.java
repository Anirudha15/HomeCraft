package com.homecraft.api.service;

import com.homecraft.api.dto.*;
import com.homecraft.api.entity.CustRequest;
import com.homecraft.api.entity.Order;
import com.homecraft.api.entity.Product;
import com.homecraft.api.repository.CustRequestRepository;
import com.homecraft.api.repository.OrderRepository;
import com.homecraft.api.repository.ProductRepository;
import com.homecraft.api.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.homecraft.api.repository.SellerRepository;
import com.homecraft.api.entity.Seller;

import java.util.*;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepo;
    private final ProductRepository productRepo;
    private final OrderRepository orderRepo;
    private final CustRequestRepository requestRepo;
    private final SellerRepository sellerRepo;

    public CustomerServiceImpl(
            CustomerRepository customerRepo,
            ProductRepository productRepo,
            OrderRepository orderRepo,
            CustRequestRepository requestRepo,
            SellerRepository sellerRepo
    ) {
        this.customerRepo = customerRepo;
        this.productRepo = productRepo;
        this.orderRepo = orderRepo;
        this.requestRepo = requestRepo;
        this.sellerRepo = sellerRepo;
    }

    // Profile
    @Override
    public Map<String, Object> getProfile(Integer customerId) {

        var customer = customerRepo.findById(customerId)
                .orElseThrow();

        return Map.of(
                "name", customer.getName(),
                "phone", customer.getPhone(),
                "email", customer.getEmail(),
                "location", customer.getLocation(),
                "interests", customer.getInterests(),
                "profileImage", customer.getProfileImage()
        );
    }

    // Products
    @Override
    public List<Map<String, Object>> getProducts(
            String category,
            String type,
            String location
    ) {
        List<Product> products = productRepo.findAll();

        return products.stream()
                .filter(p ->
                        category == null || category.isBlank()
                                || p.getCategory().equals(category)
                )
                .filter(p ->
                        type == null || type.isBlank()
                                || p.getType().equals(type)
                )
                .filter(p ->
                        location == null || location.isBlank()
                                || p.getLocation().equals(location)
                )
                .map(p -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", p.getId());
                    m.put("name", p.getName());
                    m.put("description", p.getDescription());
                    m.put("image", p.getImagePath());
                    m.put("type", p.getType());
                    m.put("price", p.getPrice());
                    m.put("customizations",
                            p.getCustomizations() == null ? "None" : p.getCustomizations());
                    Seller seller = sellerRepo.findById(p.getSellerId()).orElse(null);
                    m.put("seller", seller != null ? seller.getName() : "Unknown Seller");
                    m.put("location", p.getLocation());
                    return m;
                })
                .toList();
    }

    @Override
    public List<String> getProductLocations() {
        return productRepo.findAll()
                .stream()
                .map(Product::getLocation)
                .distinct()
                .toList();
    }

    // Cart
    @Override
    @Transactional
    public void addToCart(Integer customerId, CartAddDTO dto) {

        var existing = orderRepo.findByCustomerId(customerId)
                .stream()
                .filter(o ->
                        o.getProductId().equals(dto.getProductId())
                                && o.getStatus().equals("Pending"))
                .findFirst();

        if (existing.isPresent()) {
            Order order = existing.get();
            order.setQuantity(order.getQuantity() + dto.getQuantity());
            orderRepo.save(order);
            return;
        }

        Product p = productRepo.findById(dto.getProductId()).orElseThrow();

        Order order = new Order();
        order.setProductId(p.getId());
        order.setCustomerId(customerId);
        order.setSellerId(p.getSellerId());
        order.setName(p.getName());
        order.setPrice(p.getPrice());
        order.setQuantity(dto.getQuantity());
        order.setCustomizations(p.getCustomizations());
        order.setStatus("Pending");

        orderRepo.save(order);
    }

    @Override
    public List<Order> getCart(Integer customerId) {
        return orderRepo.findByCustomerId(customerId)
                .stream()
                .filter(o -> o.getStatus().equals("Pending"))
                .toList();
    }

    @Override
    @Transactional
    public void updateQuantity(CartQtyDTO dto) {
        Order order = orderRepo.findById(dto.getOrderId()).orElseThrow();
        int newQty = order.getQuantity() + dto.getDelta();
        if (newQty >= 1) {
            order.setQuantity(newQty);
            orderRepo.save(order);
        }
    }

    @Override
    @Transactional
    public void removeFromCart(Integer orderId) {
        orderRepo.deleteById(orderId);
    }

    // Customize
    @Override
    public void customizeOrder(Integer customerId, CustomizeDTO dto) {

        Optional<CustRequest> existing =
                requestRepo.findByOrdersId(dto.getOrderId())
                        .stream()
                        .findFirst();

        if (existing.isPresent()) {
            CustRequest r = existing.get();
            r.setInfo(dto.getInfo());
            requestRepo.save(r);
            return;
        }

        Order order = orderRepo.findById(dto.getOrderId()).orElseThrow();

        CustRequest r = new CustRequest();
        r.setOrdersId(order.getId());
        r.setProductId(order.getProductId());
        r.setCustomerId(customerId);
        r.setSellerId(order.getSellerId());
        r.setInfo(dto.getInfo());

        requestRepo.save(r);
    }

    // Order
    @Override
    @Transactional
    public void confirmOrder(Integer customerId) {
        orderRepo.findByCustomerId(customerId)
                .forEach(o -> {
                    o.setStatus("Confirmed");
                    orderRepo.save(o);
                });
    }

    @Override
    @Transactional
    public void cancelOrder(Integer customerId) {
        orderRepo.findByCustomerId(customerId)
                .forEach(o -> {
                    o.setStatus("Pending");
                    orderRepo.save(o);
                });
    }

    @Override
    public List<Order> getConfirmedOrders(Integer customerId) {
        return orderRepo.findByCustomerId(customerId)
                .stream()
                .filter(o -> o.getStatus().equals("Confirmed"))
                .toList();
    }
}