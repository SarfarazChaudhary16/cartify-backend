package com.sac.catify.backend.dto;

import java.math.BigDecimal;

// Checkout page pe user "Apply Coupon" karta hai — is response mein hum
// batate hain: "coupon valid hai ya nahi, aur agar valid hai to KITNA
// discount milega (rupees mein, already-calculated)". Frontend ko khud
// koi calculation nahi karni padti — hum "final answer" bhej dete hain.
public class CouponValidationResponse {

    private boolean valid;
    private String message;          // jaise "Coupon applied!" ya "Minimum order value not met"
    private BigDecimal discountAmount; // actual RUPEES ka discount (already calculate kiya hua)

    public CouponValidationResponse() {
    }

    public CouponValidationResponse(boolean valid, String message, BigDecimal discountAmount) {
        this.valid = valid;
        this.message = message;
        this.discountAmount = discountAmount;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
}
