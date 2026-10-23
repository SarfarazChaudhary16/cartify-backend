package com.sac.catify.backend.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sac.catify.backend.dto.MessageResponse;
import com.sac.catify.backend.dto.PaymentVerificationRequest;
import com.sac.catify.backend.dto.RazorpayOrderResponse;
import com.sac.catify.backend.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // POST /api/payments/create-order/{orderId}
    // Frontend "Pay Now" click karne pe — humara Order already ban chuka
    // hai (PENDING), ab uske liye Razorpay order banate hain
    @PostMapping("/create-order/{orderId}")
    public ResponseEntity<RazorpayOrderResponse> createOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.createRazorpayOrder(orderId));
    }

    // POST /api/payments/verify
    // Razorpay widget mein payment complete hone ke baad, frontend
    // (Razorpay ke JS callback se) ye endpoint call karta hai
    @PostMapping("/verify")
    public ResponseEntity<MessageResponse> verifyPayment(@Valid @RequestBody PaymentVerificationRequest request) {
        paymentService.verifyPayment(request);
        return ResponseEntity.ok(new MessageResponse("Payment verified successfully."));
    }
}
