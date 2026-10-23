package com.sac.catify.backend.service;

import java.math.BigDecimal;
import java.util.List;

import com.sac.catify.backend.dto.CouponRequest;
import com.sac.catify.backend.dto.CouponResponse;
import com.sac.catify.backend.dto.CouponValidationResponse;

public interface CouponService {

    // Admin > Coupons page ke liye — SAARE coupons ki list (active + inactive dono)
    List<CouponResponse> getAllCoupons();

    // Admin > "Add Coupon" button
    CouponResponse createCoupon(CouponRequest request);

    // Admin > "Edit Coupon" (pencil icon)
    CouponResponse updateCoupon(Long id, CouponRequest request);

    // Admin > "Delete Coupon" (trash icon)
    void deleteCoupon(Long id);

    // Admin > Active/Inactive toggle switch
    CouponResponse toggleActive(Long id);

    // ===== SABSE IMPORTANT METHOD — Checkout page ke "Apply Coupon" ke liye =====
    // Parameters: coupon-code jo user ne type kiya, aur uske cart ka
    // current total (taaki minOrderValue check ho sake)
    CouponValidationResponse validateCoupon(String code, BigDecimal orderTotal, Long userId);
    
   // Customer-facing "available coupons" list ke liye — sirf ACTIVE
    // aur NON-EXPIRED coupons dikhाते hain (Admin ki poori list se ALAG)
    List<CouponResponse> getActiveCoupons();

    // Order successfully place hone ke baad, redemption-record banata hai
    void recordRedemption(String code, Long userId);
    }
