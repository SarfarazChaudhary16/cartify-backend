package com.sac.catify.backend.entity;


import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

//Kyun alag Entity: Frontend ke Coupon system (WELCOME200, FLASH30 waghera) ka database version — 
//admin panel mein CRUD hota tha isi pe (Phase 23).

@Entity
@Table(name = "coupons")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Coupon code hi unique identifier hai (jaise "WELCOME200") — isliye unique=true
    @Column(nullable = false, unique = true)
    private String code;

    private String description;

    // Enum banane ka isse simple tareeka: sirf 2 fixed options hain (flat/percent),
    // isliye poora naya enum file banane ke bajaye ek chhota inline enum bhi bana sakte the,
    // lekin consistency ke liye hum ek proper Enum banayenge (DiscountType.java) - agli file mein.
    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType;

    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "min_order_value", precision = 10, scale = 2)
    private BigDecimal minOrderValue; // nullable, optional minimum order requirement

    @Column(name = "expires_at", nullable = false)
    private LocalDate expiresAt;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    public Coupon() {
    }

    public Coupon(String code, String description, DiscountType discountType,
                  BigDecimal discountValue, BigDecimal minOrderValue, LocalDate expiresAt) {
        this.code = code;
        this.description = description;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minOrderValue = minOrderValue;
        this.expiresAt = expiresAt;
        this.isActive = true;
    }

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

    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountType discountType) {
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
