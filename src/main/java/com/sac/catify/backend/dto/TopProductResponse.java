package com.sac.catify.backend.dto;

import java.math.BigDecimal;

// Admin Dashboard ke "Top Selling Products" section ke liye. Ye data
// FRONTEND-MOCK ki tarah hardcoded NAHI hai — Service layer mein hum
// ise REAL order-items se calculate karenge (kis product ki kitni units
// bikin, kitna revenue aaya — sab actual database se).
public class TopProductResponse {

    private Long productId;
    private String title;
    private String image;
    private long unitsSold;
    private BigDecimal revenue;

    public TopProductResponse() {
    }

    public TopProductResponse(Long productId, String title, String image, long unitsSold, BigDecimal revenue) {
        this.productId = productId;
        this.title = title;
        this.image = image;
        this.unitsSold = unitsSold;
        this.revenue = revenue;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getUnitsSold() {
        return unitsSold;
    }

    public void setUnitsSold(long unitsSold) {
        this.unitsSold = unitsSold;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }
}
