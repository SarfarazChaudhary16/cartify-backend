package com.sac.catify.backend.dto;

import java.time.LocalDateTime;

// Admin Inventory page ke "History Log" tab (frontend AdminInventoryPage.tsx)
// ke liye — har stock-adjustment ka ek record
public class InventoryHistoryResponse {

    private Long id;
    private Long productId;
    private String productTitle;   // snapshot — agar product baad mein delete ho jaye, history readable rahe
    private int change;            // positive = stock add hua, negative = stock kam hua
    private int previousStock;
    private int newStock;
    private String reason;
    private LocalDateTime createdAt;

    public InventoryHistoryResponse() {
    }

    public InventoryHistoryResponse(Long id, Long productId, String productTitle, int change,
                                     int previousStock, int newStock, String reason, LocalDateTime createdAt) {
        this.id = id;
        this.productId = productId;
        this.productTitle = productTitle;
        this.change = change;
        this.previousStock = previousStock;
        this.newStock = newStock;
        this.reason = reason;
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

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public int getChange() {
        return change;
    }

    public void setChange(int change) {
        this.change = change;
    }

    public int getPreviousStock() {
        return previousStock;
    }

    public void setPreviousStock(int previousStock) {
        this.previousStock = previousStock;
    }

    public int getNewStock() {
        return newStock;
    }

    public void setNewStock(int newStock) {
        this.newStock = newStock;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
