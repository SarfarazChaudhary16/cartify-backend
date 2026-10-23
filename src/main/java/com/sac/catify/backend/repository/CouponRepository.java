package com.sac.catify.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sac.catify.backend.entity.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

	
	// Checkout ke waqt user coupon code type karta hai (jaise "WELCOME200") —
    // us code se coupon dhoondh ke validate karna hai (expiry, min order value check karke)
    Optional<Coupon> findByCode(String code);
    
    // Admin panel mein naya coupon banate waqt duplicate code check karna
    boolean existsByCode(String code);
}
