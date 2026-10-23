package com.sac.catify.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// Frontend "Add to Cart" ya "Update Quantity" button click karne pe
// ye data bhejta hai. Same DTO dono operations (add aur update) ke
// liye reuse ho raha hai kyunki shape same hai.
public class CartItemRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private String color; // optional — sabhi products ke color-variants nahi hote
    private String size;  // optional

    public CartItemRequest() {
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
