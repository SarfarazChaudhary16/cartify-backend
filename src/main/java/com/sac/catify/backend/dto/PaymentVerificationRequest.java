package com.sac.catify.backend.dto;

//Kyun bana: Payment complete hone ke baad, Razorpay ka JS widget frontend ko 
//3 IDs deta hai. Frontend inhe backend ko bhejta hai verify karwane ke liye — 
//yehi DTO unko carry karti hai.

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// Payment complete hone ke baad, Razorpay ka JS widget (frontend mein)
// 3 cheezein return karta hai — inhe hum "signature verify" karne ke
// liye backend ko bhejte hain. Ye 3 cheezein hi PROOF hain ki payment
// GENUINE hai (Razorpay se hi aaya hai, kisi ne "fake success" nahi bheja)
public class PaymentVerificationRequest {

    @NotNull(message = "Order ID is required")
    private Long internalOrderId; // humara database wala Order ID

    @NotBlank(message = "Razorpay order ID is required")
    private String razorpayOrderId;

    @NotBlank(message = "Razorpay payment ID is required")
    private String razorpayPaymentId;

    @NotBlank(message = "Razorpay signature is required")
    private String razorpaySignature;

    public PaymentVerificationRequest() {
    }

    public Long getInternalOrderId() {
        return internalOrderId;
    }

    public void setInternalOrderId(Long internalOrderId) {
        this.internalOrderId = internalOrderId;
    }

    public String getRazorpayOrderId() {
        return razorpayOrderId;
    }

    public void setRazorpayOrderId(String razorpayOrderId) {
        this.razorpayOrderId = razorpayOrderId;
    }

    public String getRazorpayPaymentId() {
        return razorpayPaymentId;
    }

    public void setRazorpayPaymentId(String razorpayPaymentId) {
        this.razorpayPaymentId = razorpayPaymentId;
    }

    public String getRazorpaySignature() {
        return razorpaySignature;
    }

    public void setRazorpaySignature(String razorpaySignature) {
        this.razorpaySignature = razorpaySignature;
    }
}
