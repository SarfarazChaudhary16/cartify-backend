package com.sac.catify.backend.entity;

import jakarta.persistence.*;

//Kyun alag entity: Frontend ke mocks/categories.ts ka database version. 
//Products isse link honge (categoryId). Admin panel (Phase 20) mein CRUD hota tha isi pe.

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // Slug = URL-friendly version of name, jaise "Sneakers & Footwear" -> "sneakers-footwear"
    // Frontend mein /category/:categorySlug route isi se match karta hai
    @Column(nullable = false, unique = true)
    private String slug;

    private String image; // category card ki image URL

    // Note: "productCount" frontend mein dikhta hai, lekin hum use database mein
    // ek column ki tarah STORE nahi karenge — kyunki agar products add/delete hote rahein
    // to ye number hamesha manually update karna padega (error-prone).
    // Iske bajaye hum ise Service layer mein REAL-TIME COUNT QUERY se calculate karenge
    // (jaise: "SELECT COUNT(*) FROM products WHERE category_id = ?").
    // Isliye yahan koi productCount field nahi hai — ye ek design decision hai.

    // ===== Constructors =====

    public Category() {
    }

    public Category(String name, String slug, String image) {
        this.name = name;
        this.slug = slug;
        this.image = image;
    }

    // ===== Getters and Setters =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
