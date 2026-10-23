package com.sac.catify.backend.repository;

import com.sac.catify.backend.entity.CouponRedemption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRedemptionRepository extends JpaRepository<CouponRedemption, Long> {

    // "Already used" check karne ke liye — ek user, ek coupon-code
    // KABHI EK BAAR se zyada redeem nahi kar sakta
    boolean existsByCouponCodeIgnoreCaseAndUserId(String couponCode, Long userId);
}
