package com.sac.catify.backend.service;


import java.util.List;

import com.sac.catify.backend.dto.ReviewRequest;
import com.sac.catify.backend.dto.ReviewResponse;

// Reviews se related saare operations ka contract
public interface ReviewService {

    // Product Detail page ke Reviews tab ke liye — is product ke saare reviews
    List<ReviewResponse> getReviewsForProduct(Long productId);

    // Account > My Reviews page ke liye — user ne khud jo reviews likhe hain
    List<ReviewResponse> getReviewsByUser(Long userId);

    // "Submit Review" button — naya review banata hai, AUR product ka
    // rating/reviewCount recalculate karta hai (average nikal ke)
    ReviewResponse addReview(Long userId, ReviewRequest request);

    // "Helpful" button click — helpfulCount ko +1 karta hai
    ReviewResponse markHelpful(Long reviewId);

    // My Reviews page se apna review delete karna, YA Admin Reviews
    // Moderation page se koi bhi review delete karna. isAdmin flag se
    // decide hota hai ownership-check lagana hai ya nahi.
    void deleteReview(Long userId, Long reviewId, boolean isAdmin);

    // Admin > Reviews Moderation page ke liye — SAARE reviews (poori
    // catalog ke, koi filter nahi)
    List<ReviewResponse> getAllReviewsForAdmin();
}
