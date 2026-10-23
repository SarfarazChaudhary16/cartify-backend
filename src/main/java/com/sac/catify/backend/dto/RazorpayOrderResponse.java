package com.sac.catify.backend.dto;



//Jab frontend "Pay Now" click karega, is response mein wo saari details
//milengi jo Razorpay ka Checkout widget (frontend-side JS SDK) khud
//khulne ke liye maangta hai — razorpayOrderId, amount, currency, aur
//hamari "public" key (keyId — ye secret nahi hai, frontend ko dena SAFE hai)
public class RazorpayOrderResponse {

 private String razorpayOrderId;
 private String keyId;      // Razorpay Checkout widget ko chahiye (public key)
 private long amount;       // paise mein (Razorpay hamesha PAISE mein kaam karta hai, rupees nahi — 100 paise = 1 rupee)
 private String currency;
 private Long internalOrderId; // humara khud ka Order ID (database wala), jo verify-karte-waqt wapas chahiye hoga

 public RazorpayOrderResponse() {
 }

 public RazorpayOrderResponse(String razorpayOrderId, String keyId, long amount, String currency, Long internalOrderId) {
     this.razorpayOrderId = razorpayOrderId;
     this.keyId = keyId;
     this.amount = amount;
     this.currency = currency;
     this.internalOrderId = internalOrderId;
 }

 public String getRazorpayOrderId() {
     return razorpayOrderId;
 }

 public void setRazorpayOrderId(String razorpayOrderId) {
     this.razorpayOrderId = razorpayOrderId;
 }

 public String getKeyId() {
     return keyId;
 }

 public void setKeyId(String keyId) {
     this.keyId = keyId;
 }

 public long getAmount() {
     return amount;
 }

 public void setAmount(long amount) {
     this.amount = amount;
 }

 public String getCurrency() {
     return currency;
 }

 public void setCurrency(String currency) {
     this.currency = currency;
 }

 public Long getInternalOrderId() {
     return internalOrderId;
 }

 public void setInternalOrderId(Long internalOrderId) {
     this.internalOrderId = internalOrderId;
 }
}
