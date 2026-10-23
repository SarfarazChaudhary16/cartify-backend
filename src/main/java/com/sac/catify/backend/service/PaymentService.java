package com.sac.catify.backend.service;

import com.sac.catify.backend.dto.PaymentVerificationRequest;
import com.sac.catify.backend.dto.RazorpayOrderResponse;

// Payment ke saare operations ka contract. Sirf 2 methods hain — dono
// steps ke liye ek-ek (Order Create, Verify)
public interface PaymentService {

    // Step 1: Humare database ke Order (jo abhi PENDING hai) ke liye,
    // Razorpay pe ek "payment order" create karta hai
    RazorpayOrderResponse createRazorpayOrder(Long internalOrderId);

    // Step 2: Payment ho jaane ke baad, uski GENUINENESS verify karta hai,
    // aur agar sahi nikla to humare Order ka paymentStatus="PAID" kar deta hai
    void verifyPayment(PaymentVerificationRequest request);
}
