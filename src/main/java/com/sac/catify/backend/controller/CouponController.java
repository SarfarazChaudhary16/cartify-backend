package com.sac.catify.backend.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sac.catify.backend.dto.CouponRequest;
import com.sac.catify.backend.dto.CouponResponse;
import com.sac.catify.backend.dto.CouponValidationResponse;
import com.sac.catify.backend.service.CouponService;

import java.math.BigDecimal;

// @RestController + @RequestMapping — pehle bhi explain kiya tha:
// @RestController batata hai "response JSON format mein jayega", aur
// @RequestMapping class-level base URL define karta hai
@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    // GET /api/coupons
    // Admin > Coupons page — saare coupons ki list
    @GetMapping
    public ResponseEntity<java.util.List<CouponResponse>> getAllCoupons() {
        return ResponseEntity.ok(couponService.getAllCoupons());
    }

    // POST /api/coupons
    // Admin > "Add Coupon"
    @PostMapping
    public ResponseEntity<CouponResponse> createCoupon(@Valid @RequestBody CouponRequest request) {
        CouponResponse response = couponService.createCoupon(request);
        // HttpStatus.CREATED = 201 — REST convention jab NAYI cheez ban jati hai
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // PUT /api/coupons/{id}
    // Admin > "Edit Coupon"
    @PutMapping("/{id}")
    public ResponseEntity<CouponResponse> updateCoupon(
            @PathVariable Long id,
            @Valid @RequestBody CouponRequest request) {
        return ResponseEntity.ok(couponService.updateCoupon(id, request));
    }

    // DELETE /api/coupons/{id}
    // Admin > "Delete Coupon"
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        // noContent() = HTTP 204 — "operation successful hui, lekin
        // return karne layak koi data nahi hai" (delete ke baad standard practice)
        return ResponseEntity.noContent().build();
    }

    // PUT /api/coupons/{id}/toggle-active
    // Admin > Active/Inactive switch toggle
    @PutMapping("/{id}/toggle-active")
    public ResponseEntity<CouponResponse> toggleActive(@PathVariable Long id) {
        return ResponseEntity.ok(couponService.toggleActive(id));
    }

    // GET /api/coupons/validate?code=WELCOME200&orderTotal=1500
    // Checkout page ka "Apply Coupon" button — ye PUBLIC/authenticated
    // dono ho sakta hai (koi security-sensitive kaam nahi ho raha, sirf
    // validation hai) — @RequestParam se query-string ke 2 values lete hain
    @GetMapping("/validate")
    public ResponseEntity<CouponValidationResponse> validateCoupon(
            @org.springframework.security.core.annotation.AuthenticationPrincipal
            com.sac.catify.backend.security.CustomUserDetails currentUser,
            @RequestParam String code,
            @RequestParam BigDecimal orderTotal) {
        return ResponseEntity.ok(couponService.validateCoupon(code, orderTotal, currentUser.getId()));
    }

    // NAYA: Customer-facing endpoint — "available coupons" dikhाने ke liye
    @GetMapping("/active")
    public ResponseEntity<java.util.List<CouponResponse>> getActiveCoupons() {
        return ResponseEntity.ok(couponService.getActiveCoupons());
    }
}
