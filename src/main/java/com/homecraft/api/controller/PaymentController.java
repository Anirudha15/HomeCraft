package com.homecraft.api.controller;

import com.homecraft.api.dto.PaymentOrderDTO;
import com.homecraft.api.dto.PaymentVerifyDTO;
import com.homecraft.api.repository.OrderRepository;
import com.homecraft.api.service.CustomerService;
import com.homecraft.api.service.PaymentService;
import com.homecraft.api.security.RazorpaySignatureUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderRepository orderRepo;
    private final CustomerService customerService;

    @Value("${razorpay.key-secret}")
    private String razorpaySecret;

    public PaymentController(
            PaymentService paymentService,
            OrderRepository orderRepo,
            CustomerService customerService
    ) {
        this.paymentService = paymentService;
        this.orderRepo = orderRepo;
        this.customerService = customerService;
    }

    private Integer userId(Authentication auth) {
        return Integer.parseInt(auth.getName());
    }

    // Create Payment
    @PostMapping("/create")
    public ResponseEntity<?> createPayment(Authentication auth) throws Exception {

        int customerId = userId(auth);

        int totalAmount = orderRepo.findByCustomerId(customerId)
                .stream()
                .filter(o -> o.getStatus().equals("Pending"))
                .mapToInt(o -> o.getPrice().intValue() * o.getQuantity())
                .sum();

        int amountInPaise = totalAmount * 100;

        var razorpayOrder = paymentService.createOrder(amountInPaise);

        PaymentOrderDTO dto = new PaymentOrderDTO();
        dto.setOrderId(razorpayOrder.get("id"));
        dto.setAmount(amountInPaise);

        return ResponseEntity.ok(dto);
    }

    // Verify Payment
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(
            Authentication auth,
            @RequestBody PaymentVerifyDTO dto
    ) throws Exception {
        System.out.println("Received verify request:");  // ← Add this
        System.out.println("Order ID: " + dto.getRazorpayOrderId());
        System.out.println("Payment ID: " + dto.getRazorpayPaymentId());
        System.out.println("Signature from Razorpay: " + dto.getRazorpaySignature());

        String payload = dto.getRazorpayOrderId() + "|" + dto.getRazorpayPaymentId();
        System.out.println("Computed payload: '" + payload + "'");  // ← Crucial

        boolean valid = RazorpaySignatureUtil.verify(
                dto.getRazorpayOrderId(),
                dto.getRazorpayPaymentId(),
                dto.getRazorpaySignature(),
                razorpaySecret
        );

        System.out.println("Verification result: " + valid);

        if (!valid) {
            return ResponseEntity.badRequest().body("Payment verification failed");
        }

        customerService.confirmOrder(userId(auth));
        return ResponseEntity.ok().build();
    }
}