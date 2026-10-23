package com.sac.catify.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

// Product Detail page ke Reviews tab (frontend ReviewsSection.tsx) ko
// jo shape chahiye. Dhyan do: humein Review ke saath USER ka bhi kuch
// data chahiye (naam, avatar) — lekin poora User Entity nahi bhejenge
// (uska password field waghera hai), sirf zaroori 2 fields flatten
// karke daal rahe hain (jaisa Product mein Brand/Category ke liye kiya tha)
public class ReviewResponse {

    private Long id;
    private Long productId;

    // User ka poora object nahi, sirf display ke liye zaroori fields
    private Long userId;
    private String userName;
    private String userAvatar;

    private Double rating;
    private String title;
    private String comment;
    private List<String> images; // review ke saath attach photos ke URLs
    private boolean verifiedPurchase;
    private Integer helpfulCount;
    private LocalDateTime createdAt;

    public ReviewResponse() {
    }

    public ReviewResponse(Long id, Long productId, Long userId, String userName, String userAvatar,
                           Double rating, String title, String comment, List<String> images,
                           boolean verifiedPurchase, Integer helpfulCount, LocalDateTime createdAt) {
        this.id = id;
        this.productId = productId;
        this.userId = userId;
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.rating = rating;
        this.title = title;
        this.comment = comment;
        this.images = images;
        this.verifiedPurchase = verifiedPurchase;
        this.helpfulCount = helpfulCount;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public boolean isVerifiedPurchase() {
        return verifiedPurchase;
    }

    public void setVerifiedPurchase(boolean verifiedPurchase) {
        this.verifiedPurchase = verifiedPurchase;
    }

    public Integer getHelpfulCount() {
        return helpfulCount;
    }

    public void setHelpfulCount(Integer helpfulCount) {
        this.helpfulCount = helpfulCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
