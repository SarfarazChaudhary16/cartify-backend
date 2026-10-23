package com.sac.catify.backend.service.impl;

import org.springframework.stereotype.Service;

import com.sac.catify.backend.dto.CouponRequest;
import com.sac.catify.backend.dto.CouponResponse;
import com.sac.catify.backend.dto.CouponValidationResponse;
import com.sac.catify.backend.entity.Coupon;
import com.sac.catify.backend.entity.DiscountType;
import com.sac.catify.backend.exception.ApiException;
import com.sac.catify.backend.repository.CouponRedemptionRepository;
import com.sac.catify.backend.repository.CouponRepository;
import com.sac.catify.backend.service.CouponService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CouponServiceImpl implements CouponService {

    // Sirf EK repository chahiye is baar (Product/Brand/Category jaisa
    // "lookup" karne ki zarurat nahi hai coupon ke case mein)
    private final CouponRepository couponRepository;
    
    private final CouponRedemptionRepository couponRedemptionRepository;

    public CouponServiceImpl(CouponRepository couponRepository,
    		CouponRedemptionRepository couponRedemptionRepository ) {
    	
        this.couponRepository = couponRepository;
        this.couponRedemptionRepository = couponRedemptionRepository;
    }

    @Override
    public List<CouponResponse> getAllCoupons() {
        // findAll() -> "SELECT * FROM coupons" chalata hai (JpaRepository
        // ka built-in method, humein khud SQL likhne ki zarurat nahi)
        //
        // .stream().map(this::mapToResponse).collect(...) -> Java Streams
        // pattern hai jo humne baar-baar dekha hai: List<Coupon> (Entity)
        // ko List<CouponResponse> (DTO) mein convert kar raha hai, HAR EK
        // element ko mapToResponse() method se guzaar ke
        return couponRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CouponResponse createCoupon(CouponRequest request) {

        // Step 1: Check karte hain — kya ye coupon-code PEHLE SE exist
        // karta hai? (jaise agar koi dobara "WELCOME200" banane ki koshish
        // kare, to duplicate NAHI banna chahiye — coupon codes unique hone chahiye)
        if (couponRepository.existsByCode(request.getCode())) {
            // ApiException.conflict() -> HTTP 409 status. Ye status-code
            // specifically "resource already exists" jaisi situations ke
            // liye use hota hai (bilkul jaisa AuthServiceImpl mein
            // "Email already registered" ke liye use kiya tha)
            throw ApiException.conflict("Coupon code already exists: " + request.getCode());
        }

        // Step 2: Khaali Coupon object banate hain, fields bharte hain
        Coupon coupon = new Coupon();
        coupon.setCode(request.getCode().toUpperCase()); // hamesha UPPERCASE store karte hain, taaki "welcome200" aur "WELCOME200" dono match ho jayein consistently
        coupon.setDescription(request.getDescription());

        // ===== String ko Enum mein Convert karna =====
        // request.getDiscountType() ek String hai (jaise "flat" ya "FLAT" —
        // frontend se kaise bhi aaye), lekin Entity mein "discountType"
        // field ka type DiscountType ENUM hai (FLAT/PERCENT). Isliye
        // convert karna zaroori hai.
        //
        // DiscountType.valueOf(...) ek built-in Java-enum method hai jo
        // STRING ko uske corresponding ENUM CONSTANT mein badalta hai.
        // .toUpperCase() lagaya hai taaki agar frontend "flat" (chhote
        // letters mein) bheje, wo bhi match ho jaye (kyunki enum constants
        // hamesha UPPERCASE mein define hote hain: FLAT, PERCENT)
        coupon.setDiscountType(DiscountType.valueOf(request.getDiscountType().toUpperCase()));

        coupon.setDiscountValue(request.getDiscountValue());
        coupon.setMinOrderValue(request.getMinOrderValue());
        coupon.setExpiresAt(request.getExpiresAt());
        coupon.setActive(true); // naya coupon hamesha ACTIVE state se shuru hota hai

        Coupon saved = couponRepository.save(coupon);
        return mapToResponse(saved);
    }

    @Override
    public CouponResponse updateCoupon(Long id, CouponRequest request) {
        // Existing coupon dhoondhte hain (agar na mile, 404 error)
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Coupon not found with id: " + id));

        coupon.setCode(request.getCode().toUpperCase());
        coupon.setDescription(request.getDescription());
        coupon.setDiscountType(DiscountType.valueOf(request.getDiscountType().toUpperCase()));
        coupon.setDiscountValue(request.getDiscountValue());
        coupon.setMinOrderValue(request.getMinOrderValue());
        coupon.setExpiresAt(request.getExpiresAt());
        // NOTE: "isActive" ko yahan touch nahi kar rahe — wo alag
        // "toggleActive()" method se manage hota hai (separate concern)

        Coupon updated = couponRepository.save(coupon);
        return mapToResponse(updated);
    }

    @Override
    public void deleteCoupon(Long id) {
        // existsById() se PEHLE check karte hain (jaisa Brand/Category
        // mein bhi tha) — taaki agar ID galat ho, clean 404 error mile,
        // na ki deleteById() chup-chaap kuch na kare
        if (!couponRepository.existsById(id)) {
            throw ApiException.notFound("Coupon not found with id: " + id);
        }
        couponRepository.deleteById(id);
    }

    @Override
    public CouponResponse toggleActive(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Coupon not found with id: " + id));

        // "!" (NOT operator) se current value ko ULTA kar dete hain —
        // agar abhi true hai to false ho jayega, agar false hai to true.
        // Ye bilkul wahi "toggle" behavior hai jo frontend ke Switch component mein tha.
        coupon.setActive(!coupon.isActive());

        Coupon updated = couponRepository.save(coupon);
        return mapToResponse(updated);
    }

    // ===================================================================
    // VALIDATE COUPON — Checkout ke "Apply Coupon" button ka core logic
    // ===================================================================
    @Override
    public CouponValidationResponse validateCoupon(String code, BigDecimal orderTotal, Long userId) {

        var couponOptional = couponRepository.findByCode(code.toUpperCase());

        if (couponOptional.isEmpty()) {
            return new CouponValidationResponse(false, "Invalid coupon code.", BigDecimal.ZERO);
        }

        Coupon coupon = couponOptional.get();

        if (!coupon.isActive()) {
            return new CouponValidationResponse(false, "This coupon is no longer active.", BigDecimal.ZERO);
        }

        if (coupon.getExpiresAt().isBefore(LocalDate.now())) {
            return new CouponValidationResponse(false, "This coupon has expired.", BigDecimal.ZERO);
        }

        if (coupon.getMinOrderValue() != null && orderTotal.compareTo(coupon.getMinOrderValue()) < 0) {
            return new CouponValidationResponse(false,
                    "Minimum order value of ₹" + coupon.getMinOrderValue() + " required.", BigDecimal.ZERO);
        }

        // NAYA: Per-user "already used" check — is user ne ye coupon
        // PEHLE kabhi redeem kiya hai kya?
        if (couponRedemptionRepository.existsByCouponCodeIgnoreCaseAndUserId(code, userId)) {
            return new CouponValidationResponse(false, "You have already used this coupon.", BigDecimal.ZERO);
        }

        BigDecimal discountAmount;

        if (coupon.getDiscountType() == DiscountType.FLAT) {
            discountAmount = coupon.getDiscountValue();
        } else {
            BigDecimal percentageFactor = coupon.getDiscountValue()
                    .divide(BigDecimal.valueOf(100), 4, java.math.RoundingMode.HALF_UP);
            discountAmount = orderTotal.multiply(percentageFactor);
        }

        return new CouponValidationResponse(true, "Coupon applied successfully!", discountAmount);
    }

    @Override
    public List<CouponResponse> getActiveCoupons() {
        return couponRepository.findAll().stream()
                .filter(c -> c.isActive() && !c.getExpiresAt().isBefore(LocalDate.now()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void recordRedemption(String code, Long userId) {
        couponRedemptionRepository.save(new com.sac.catify.backend.entity.CouponRedemption(code, userId));
    }

    // ===================================================================
    // PRIVATE HELPER
    // ===================================================================

    // Coupon Entity ko CouponResponse DTO mein convert karta hai
    private CouponResponse mapToResponse(Coupon coupon) {
        return new CouponResponse(
                coupon.getId(),
                coupon.getCode(),
                coupon.getDescription(),
                coupon.getDiscountType().name(), // enum ko String mein convert (jaise DiscountType.FLAT -> "FLAT")
                coupon.getDiscountValue(),
                coupon.getMinOrderValue(),
                coupon.getExpiresAt(),
                coupon.isActive()
        );
    }
}
