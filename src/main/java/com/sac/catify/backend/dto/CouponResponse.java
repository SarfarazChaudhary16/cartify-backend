package com.sac.catify.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

// Coupon Entity ka "safe response version" — Checkout page (coupon apply
// karte waqt) aur Admin Coupons page (list dikhane ke liye), dono isi
// shape ka use karenge.
public class CouponResponse {

    // Har field Entity ke corresponding field jaisa hi hai, bas ye ek
    // ALAG class hai (Entity nahi) — taaki hum control mein rakh sakein
    // ki EXACTLY kya-kya JSON mein frontend ko jaye
    private Long id;
    private String code;               // jaise "WELCOME200"
    private String description;
    private String discountType;       // "FLAT" ya "PERCENT" — enum ko String ki tarah bhejenge
    private BigDecimal discountValue;  // jaise 200 (agar FLAT) ya 20 (agar PERCENT, matlab 20%)
    private BigDecimal minOrderValue;  // nullable — agar coupon ke liye minimum order-amount chahiye
    private LocalDate expiresAt;
    private boolean isActive;

    // Khaali constructor — Java/Spring ke internal JSON-conversion process
    // ke liye zaroori hai (jab JSON banaya jata hai object se, ya object
    // banaya jata hai JSON se, "empty object pehle banao phir fields bharo"
    // wala pattern kaam karta hai)
    public CouponResponse() {
    }

    // Ye "all-args constructor" hai — jab hum Service layer mein naya
    // CouponResponse banayenge (Entity se convert karke), isi constructor
    // ko ek hi line mein call karke saari values ek saath de denge
    public CouponResponse(Long id, String code, String description, String discountType,
                           BigDecimal discountValue, BigDecimal minOrderValue,
                           LocalDate expiresAt, boolean isActive) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minOrderValue = minOrderValue;
        this.expiresAt = expiresAt;
        this.isActive = isActive;
    }

    // ===== Getters and Setters =====
    // Ye har field ke liye ek "get" method aur ek "set" method hain.
    // Kyun zaroori hain: Java mein fields "private" hote hain (encapsulation
    // ka OOP principle — bahar se direct access nahi milna chahiye), isliye
    // bahar se value PADHNE ke liye getter, aur value BADALNE ke liye
    // setter chahiye hota hai. Spring Boot ke JSON-conversion (Jackson
    // library, jo Spring Boot ke andar hi built-in hai) bhi INHI getters/
    // setters ka use karta hai object ko JSON mein convert karne ke liye
    // (isi liye Lombok use na karne pe bhi humein ye sab manually likhna
    // padta hai — Lombok bas inhe automatic generate kar deta, hum manually
    // kar rahe hain jaisa tumne pehle bola tha).

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public BigDecimal getMinOrderValue() {
        return minOrderValue;
    }

    public void setMinOrderValue(BigDecimal minOrderValue) {
        this.minOrderValue = minOrderValue;
    }

    public LocalDate getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDate expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
