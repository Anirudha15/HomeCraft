package com.homecraft.api.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final RazorpayClient razorpayClient;

    public PaymentService(
            @Value("${razorpay.key-id}") String keyId,
            @Value("${razorpay.key-secret}") String keySecret
    ) throws Exception {
        this.razorpayClient = new RazorpayClient(keyId, keySecret);
    }

    public Order createOrder(int amountInPaise) throws Exception {
        JSONObject options = new JSONObject();
        options.put("amount", amountInPaise);
        options.put("currency", "INR");
        options.put("receipt", "homecraft_receipt");

        return razorpayClient.orders.create(options);
    }
}