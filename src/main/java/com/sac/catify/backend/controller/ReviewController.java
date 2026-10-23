package com.sac.catify.backend.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.sac.catify.backend.dto.ReviewRequest;
import com.sac.catify.backend.dto.ReviewResponse;
import com.sac.catify.backend.security.CustomUserDetails;
import com.sac.catify.backend.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // GET /api/reviews/product/{productId}
    // Product Detail page ke Reviews tab — PUBLIC hai, bina login ke
    // bhi dekh sakte hain (SecurityConfig mein GET rule add karna hoga)
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsForProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getReviewsForProduct(productId));
    }

    // GET /api/reviews/my-reviews
    // Account > My Reviews page — login required
    @GetMapping("/my-reviews")
    public ResponseEntity<List<ReviewResponse>> getMyReviews(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(reviewService.getReviewsByUser(currentUser.getId()));
    }

    // POST /api/reviews
    // "Submit Review" button
    @PostMapping
    public ResponseEntity<ReviewResponse> addReview(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody ReviewRequest request) {
        ReviewResponse response = reviewService.addReview(currentUser.getId(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // PUT /api/reviews/{reviewId}/helpful
    // "Helpful" button click
    @PutMapping("/{reviewId}/helpful")
    public ResponseEntity<ReviewResponse> markHelpful(@PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.markHelpful(reviewId));
    }

    // DELETE /api/reviews/{reviewId}
    // My Reviews page se apna review delete karna (customer)
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteMyReview(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long reviewId) {
        // isAdmin=false — ownership check lagega (sirf apna review delete kar sakta hai)
        reviewService.deleteReview(currentUser.getId(), reviewId, false);
        return ResponseEntity.noContent().build();
    }

    // ===== ADMIN ENDPOINTS =====

    // GET /api/admin/reviews
    @GetMapping("/admin/all")
    public ResponseEntity<List<ReviewResponse>> getAllReviewsForAdmin() {
        return ResponseEntity.ok(reviewService.getAllReviewsForAdmin());
    }

    // DELETE /api/admin/reviews/{reviewId}
    // Admin Reviews Moderation page — kisi bhi review ko delete kar sakta hai
    @DeleteMapping("/admin/{reviewId}")
    public ResponseEntity<Void> deleteReviewAsAdmin(@PathVariable Long reviewId) {
        // isAdmin=true — ownership check SKIP hoga, koi bhi review delete ho sakta hai
        reviewService.deleteReview(null, reviewId, true);
        return ResponseEntity.noContent().build();
    }
}
