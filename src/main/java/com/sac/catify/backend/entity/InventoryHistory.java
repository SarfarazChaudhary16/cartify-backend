package com.sac.catify.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

//Kyun alag Entity: Frontend ke Admin Inventory page (Phase 22) mein "Adjust Stock" karte
//waqt ek history log banta tha (kisne, kab, kitna stock badla, kyun). Ye audit trail hamesha
//permanent record hona chahiye — chahe product delete ho jaye, history rehni chahiye, isliye
//humne productId/productTitle yahan bhi (Order ki tarah) snapshot ki tarah copy kiya hai, 
//seedha Product reference nahi kiya.

// Admin ke stock-adjustment ka audit log — Inventory page (frontend Phase 22) mein
// "Adjust Stock" karte waqt yahan entry banti hai. productTitle yahan snapshot ki tarah
// store kiya hai (Product delete ho jaye tab bhi purana history record readable rahe).
@Entity
@Table(name = "inventory_history")
public class InventoryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_title", nullable = false)
    private String productTitle;

    // Positive = stock add hua, Negative = stock kam hua
    // Column name explicitly "stock_change" diya hai kyunki "change" MySQL ka
    // reserved keyword hai (CHANGE COLUMN syntax mein use hota hai) — agar hum
    // @Column(name=...) na dete to Hibernate "change" hi column name bana deta
    // aur MySQL syntax error de deta (jo abhi mila).
    @Column(name = "stock_change", nullable = false)
    private Integer change;

    @Column(name = "previous_stock", nullable = false)
    private Integer previousStock;

    @Column(name = "new_stock", nullable = false)
    private Integer newStock;

    @Column(nullable = false)
    private String reason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public InventoryHistory() {
    }

    public InventoryHistory(Long productId, String productTitle, Integer change,
                             Integer previousStock, Integer newStock, String reason) {
        this.productId = productId;
        this.productTitle = productTitle;
        this.change = change;
        this.previousStock = previousStock;
        this.newStock = newStock;
        this.reason = reason;
        this.createdAt = LocalDateTime.now();
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

    public Integer getChange() {
        return change;
    }

    public void setChange(Integer change) {
        this.change = change;
    }

    public Integer getPreviousStock() {
        return previousStock;
    }

    public void setPreviousStock(Integer previousStock) {
        this.previousStock = previousStock;
    }

    public Integer getNewStock() {
        return newStock;
    }

    public void setNewStock(Integer newStock) {
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
