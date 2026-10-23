package com.sac.catify.backend.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

//Kyun ye alag hai CouponResponse se: Response mein id hoti hai 
//(kyunki wo already-database-mein-hai wali cheez hai). Request 
//mein id NAHI hoti (kyunki jab Admin naya coupon banata hai, ID 
//abhi exist hi nahi karti — database khud generate karega). 
//Ye ek common pattern hai jo humne Product/Category/Brand mein 
//bhi follow kiya tha — Request (jo aata hai, kam fields) alag, 
//Response (jo jata hai, poore fields) alag.

// Admin ke "Add/Edit Coupon" form (frontend AdminCouponsPage.tsx modal)
// se aane wala data
public class CouponRequest {

    // @NotBlank ek "validation annotation" hai — Spring khud, Controller
    // mein @Valid dekhte hi, is field ko check kar lega ki khaali/null to
    // nahi hai. Agar khaali hoga, Spring khud "400 Bad Request" bhej dega
    // (jo humara GlobalExceptionHandler clean JSON format mein dikha dega),
    // hume manually if-checks likhne ki zarurat nahi.
    @NotBlank(message = "Coupon code is required")
    private String code;

    private String description; // optional, isliye koi validation nahi lagai

    // @NotBlank yahan bhi — Discount type "FLAT" ya "PERCENT" string ki
    // tarah aayega frontend se, hum ise Service layer mein enum
    // (DiscountType.FLAT / DiscountType.PERCENT) mein convert karenge
    @NotBlank(message = "Discount type is required")
    private String discountType;

    // @NotNull -> field bilkul missing/null nahi honi chahiye
    // @Positive -> value 0 se zyada honi chahiye (0 ya negative discount
    // koi maayne nahi rakhta)
    @NotNull(message = "Discount value is required")
    @Positive(message = "Discount value must be greater than 0")
    private BigDecimal discountValue;

    private BigDecimal minOrderValue; // optional

    @NotNull(message = "Expiry date is required")
    private LocalDate expiresAt;

    public CouponRequest() {
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
}
