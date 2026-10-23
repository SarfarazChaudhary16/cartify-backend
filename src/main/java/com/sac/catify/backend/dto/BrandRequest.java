package com.sac.catify.backend.dto;

import jakarta.validation.constraints.NotBlank;

// Admin ke Add/Edit Brand form (AdminBrandsPage.tsx modal) se aane wala data
public class BrandRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String slug; // optional

    private String logo; // optional (frontend mein DiceBear fallback logo tha agar khaali ho)

    private String description;

    public BrandRequest() {
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
