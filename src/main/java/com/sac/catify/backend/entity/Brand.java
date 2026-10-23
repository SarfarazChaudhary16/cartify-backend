package com.sac.catify.backend.entity;

import jakarta.persistence.*;

//Kyun alag entity: Frontend ke mocks/brands.ts ka database version, 
//Category jaisa hi simple structure. Products isse bhi link honge (brandId).
@Entity
@Table(name = "brands")
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    private String logo; // brand logo image URL

    // TEXT column type use kar rahe hain kyunki description lambi ho sakti hai
    // (normal VARCHAR ki default length limit se zyada ho sakti hai)
    @Column(columnDefinition = "TEXT")
    private String description;

    // ===== Constructors =====

    public Brand() {
    }

    public Brand(String name, String slug, String logo, String description) {
        this.name = name;
        this.slug = slug;
        this.logo = logo;
        this.description = description;
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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
