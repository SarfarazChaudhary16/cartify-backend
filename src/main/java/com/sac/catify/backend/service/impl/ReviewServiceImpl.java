package com.sac.catify.backend.service.impl;


import org.springframework.stereotype.Service;

import com.sac.catify.backend.dto.ReviewRequest;
import com.sac.catify.backend.dto.ReviewResponse;
import com.sac.catify.backend.entity.Product;
import com.sac.catify.backend.entity.Review;
import com.sac.catify.backend.entity.ReviewImage;
import com.sac.catify.backend.entity.User;
import com.sac.catify.backend.exception.ApiException;
import com.sac.catify.backend.repository.OrderItemRepository;
import com.sac.catify.backend.repository.ProductRepository;
import com.sac.catify.backend.repository.ReviewRepository;
import com.sac.catify.backend.repository.UserRepository;
import com.sac.catify.backend.service.ReviewService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    // OrderItemRepository chahiye "Verified Purchase" check karne ke liye
    private final OrderItemRepository orderItemRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, ProductRepository productRepository,
                              UserRepository userRepository, OrderItemRepository orderItemRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public List<ReviewResponse> getReviewsForProduct(Long productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponse> getReviewsByUser(Long userId) {
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewResponse addReview(Long userId, ReviewRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("User not found with id: " + userId));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> ApiException.notFound("Product not found with id: " + request.getProductId()));

        // ===== VERIFIED PURCHASE CHECK =====
        // Client se "verifiedPurchase: true" jaisi koi field NAHI le rahe
        // request mein — hum KHUD database se check karte hain ki is user
        // ne is product ko kabhi order kiya hai ya nahi. Agar hum client
        // pe bharosa karte, koi bhi bina-khareede "Verified" badge le sakta tha!
        boolean isVerified = orderItemRepository.existsByOrder_User_IdAndProductId(userId, request.getProductId());

        Review review = new Review(product, user, request.getRating(), request.getTitle(),
                request.getComment(), isVerified);

        // Images list ko ReviewImage Embeddable objects mein convert karte hain
        if (request.getImages() != null) {
            List<ReviewImage> reviewImages = new ArrayList<>();
            for (String url : request.getImages()) {
                reviewImages.add(new ReviewImage(url));
            }
            review.setImages(reviewImages);
        }

        Review saved = reviewRepository.save(review);

        // ===== RATING AGGREGATION — Product ka average rating update karna =====
        // Naya review add hote hi, us Product ka "rating" field (jo Home
        // page/Catalog page pe dikhta hai stars ke saath) RECALCULATE
        // karna zaroori hai, warna purana/stale rating dikhta rahega
        recalculateProductRating(product.getId());

        return mapToResponse(saved);
    }

    @Override
    public ReviewResponse markHelpful(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> ApiException.notFound("Review not found with id: " + reviewId));

        // NOTE: Ye ek SIMPLIFIED implementation hai — hum har call pe
        // sirf +1 karte hain, ye track NAHI karte "kis-kis USER ne already
        // helpful maar diya hai" (jaisa frontend ka demo bhi sirf session-
        // level tha, per-user permanent tracking nahi thi).
        review.setHelpfulCount(review.getHelpfulCount() + 1);
        Review updated = reviewRepository.save(review);
        return mapToResponse(updated);
    }

    @Override
    public void deleteReview(Long userId, Long reviewId, boolean isAdmin) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> ApiException.notFound("Review not found with id: " + reviewId));

        // Agar admin nahi hai (matlab normal customer apna review delete
        // kar raha hai My Reviews page se), to ownership-check lagate hain
        if (!isAdmin && !review.getUser().getId().equals(userId)) {
            throw ApiException.forbidden("This review does not belong to you.");
        }

        Long productId = review.getProduct().getId();
        reviewRepository.delete(review);

        // Review delete hone ke baad bhi rating recalculate karna zaroori
        // hai (average badal sakta hai jab ek review hat jaye)
        recalculateProductRating(productId);
    }

    @Override
    public List<ReviewResponse> getAllReviewsForAdmin() {
        return reviewRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ===================================================================
    // PRIVATE HELPERS
    // ===================================================================

    // Product ke SAARE reviews ka average nikal ke, Product entity ke
    // "rating" aur "reviewCount" fields ko update karta hai
    private void recalculateProductRating(Long productId) {
        List<Review> allReviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> ApiException.notFound("Product not found with id: " + productId));

        if (allReviews.isEmpty()) {
            // Koi review nahi bacha (sab delete ho gaye) — rating 0 kar dete hain
            product.setRating(0.0);
            product.setReviewCount(0);
        } else {
            // Average nikalne ke liye: saare ratings ka SUM, phir count
            // se divide. Java Streams ka mapToDouble().average() istemal
            // kar rahe hain — ye ek built-in convenient tareeka hai
            // average nikalne ka, manual for-loop+sum likhne ke bajaye.
            double average = allReviews.stream()
                    .mapToDouble(Review::getRating)
                    .average()
                    .orElse(0.0);

            // Rating ko 1 decimal place tak round karte hain (jaise 4.6666
            // -> 4.7), taaki UI mein clean dikhe
            double rounded = Math.round(average * 10.0) / 10.0;

            product.setRating(rounded);
            product.setReviewCount(allReviews.size());
        }

        productRepository.save(product);
    }

    // Review Entity ko ReviewResponse DTO mein convert karta hai
    private ReviewResponse mapToResponse(Review review) {
        List<String> imageUrls = review.getImages().stream()
                .map(ReviewImage::getUrl)
                .collect(Collectors.toList());

        return new ReviewResponse(
                review.getId(),
                review.getProduct().getId(),
                review.getUser().getId(),
                review.getUser().getName(),
                review.getUser().getAvatar(),
                review.getRating(),
                review.getTitle(),
                review.getComment(),
                imageUrls,
                review.isVerifiedPurchase(),
                review.getHelpfulCount(),
                review.getCreatedAt()
        );
    }
}
