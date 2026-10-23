package com.sac.catify.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// Record karta hai "KIS USER ne KAUNSA coupon KAB use kiya" — isی se
// hum "per-user, ek-baar-hi-use" rule enforce karte hain. userId/couponCode
// yahan SIMPLE fields hain (koi @ManyToOne relation nahi) — bilkul
// InventoryHistory jaisa pattern, kyunki humein sirf "check + record"
// karna hai, kisi navigation-object ki zarurat nahi.
@Entity
@Table(name = "coupon_redemptions")
public class CouponRedemption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coupon_code", nullable = false)
    private String couponCode;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "redeemed_at", nullable = false)
    private LocalDateTime redeemedAt;

    public CouponRedemption() {
    }

    public CouponRedemption(String couponCode, Long userId) {
        this.couponCode = couponCode.toUpperCase();
        this.userId = userId;
        this.redeemedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getRedeemedAt() {
        return redeemedAt;
    }

    public void setRedeemedAt(LocalDateTime redeemedAt) {
        this.redeemedAt = redeemedAt;
    }
}
