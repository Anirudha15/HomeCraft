package com.homecraft.api.security;

import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;

public class RazorpaySignatureUtil {

    public static boolean verify(
            String orderId,
            String paymentId,
            String signature,
            String secret
    ) {
        if (orderId == null || paymentId == null || signature == null || secret == null) {
            System.err.println("Null values in signature verification");
            return false;
        }

        JSONObject options = new JSONObject();
        options.put("razorpay_order_id", orderId);
        options.put("razorpay_payment_id", paymentId);
        options.put("razorpay_signature", signature);

        try {
            boolean isValid = Utils.verifyPaymentSignature(options, secret);
            System.out.println("Razorpay SDK verification result: " + isValid);  // Debug log
            return isValid;
        } catch (RazorpayException e) {
            System.err.println("Razorpay signature verification failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}