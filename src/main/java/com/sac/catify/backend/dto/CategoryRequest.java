package com.sac.catify.backend.dto;

import jakarta.validation.constraints.NotBlank;

// Admin ke Add/Edit Category form (AdminCategoriesPage.tsx modal) se aane wala data
public class CategoryRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String slug; // optional, khaali ho to Service khud generate karega

    @NotBlank(message = "Image URL is required")
    private String image;

    public CategoryRequest() {
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
