package com.sac.catify.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// "Place Order" button click hone pe frontend se ye data aata hai.
//
// IMPORTANT SECURITY DESIGN: Is request mein "kya-kya order kar rahe ho"
// (products/quantities) NAHI puchte — kyunki agar hum client se aayi
// price/quantity par bharosa karte, to koi bhi request tamper karke
// price manipulate kar sakta tha. Iske bajaye, Order banate waqt Service
// layer KHUD database se user ke current CART ITEMS uठाएगी (jo pehle se
// CartItem table mein hain, B5 mein bana chuke hain) — is tareeke se
// PRICE hamesha server-trusted/live database se aata hai, client kabhi
// price decide nahi karta. Ye ek CRITICAL e-commerce security practice hai.
public class CheckoutRequest {

    @NotNull(message = "Please select a delivery address")
    private Long addressId;

    @NotBlank(message = "Please select a payment method")
    private String paymentMethod; // jaise "cod", "upi", "card" — Razorpay integration B7 mein isko expand karega
    
    private String couponCode; // optional — Cart mein apply kiya hua coupon-code, agar tha to
    
    public CheckoutRequest() {
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }
}
