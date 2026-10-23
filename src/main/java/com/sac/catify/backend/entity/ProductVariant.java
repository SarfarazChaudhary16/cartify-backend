package com.sac.catify.backend.entity;


import jakarta.persistence.*;

//Kyun alag Entity: Frontend ke admin product form mein yaad hai 
//colors x sizes ka combination table tha (SKU + stock per combination, 
//jaise "Cobalt / UK 9 / SKU123 / Stock 15"). Har variant ka apna stock 
//track hona chahiye, isliye ye bhi apni Entity hai, Product se Many-to-One.

@Entity
@Table(name = "product_variants")
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String color; // jaise "Cobalt" — nullable, sab products ke colors nahi hote

    private String size;  // jaise "UK 9" — nullable

    @Column(nullable = false, unique = true)
    private String sku; // Stock Keeping Unit — is combination ka unique identifier

    @Column(nullable = false)
    private Integer stock; // is specific color+size combination ka stock

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public ProductVariant() {
    }

    public ProductVariant(String color, String size, String sku, Integer stock, Product product) {
        this.color = color;
        this.size = size;
        this.sku = sku;
        this.stock = stock;
        this.product = product;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}