package com.sac.catify.backend.dto;

import jakarta.validation.constraints.*;
import java.util.List;

// "Write a Review" form (frontend WriteReviewModal.tsx) se aane wala data
public class ReviewRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot be more than 5")
    private Double rating;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Comment is required")
    @Size(min = 10, message = "Please write at least 10 characters")
    private String comment;

    private List<String> images; // optional, review photos ke URLs

    public ReviewRequest() {
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
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
}
