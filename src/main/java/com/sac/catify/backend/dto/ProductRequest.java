package com.sac.catify.backend.dto;

import java.math.BigDecimal;
import java.util.List;

import com.sac.catify.backend.entity.ProductColor;
import com.sac.catify.backend.entity.ProductSpecification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

//Kyun bana: Admin jab "Add Product" / "Edit Product" form submit karega 
//(frontend ke AdminProductFormPage), ye uska incoming data-shape hai. 
//Response DTO se alag isliye kyunki request mein id, rating, reviewCount, 
//createdAt jaisi cheezein nahi aati — wo server khud generate/manage karta hai.

//Admin ke Add/Edit Product form (frontend AdminProductFormPage.tsx) se aane
//wala data. Response DTO se isliye alag hai kyunki request mein sirf wahi
//fields aati hain jo USER khud bharta hai — id, rating, reviewCount, createdAt
//jaisi cheezein server khud decide/generate karta hai, form se nahi aati.

// Admin ke Add/Edit Product form (frontend AdminProductFormPage.tsx) se aane
// wala data. Response DTO se isliye alag hai kyunki request mein sirf wahi
// fields aati hain jo USER khud bharta hai — id, rating, reviewCount, createdAt
// jaisi cheezein server khud decide/generate karta hai, form se nahi aati.
public class ProductRequest {

    @NotBlank(message = "Title is required")
    private String title;

    // Slug frontend se optional aa sakta hai (auto-generate bhi hota tha agar khaali ho) —
    // isliye @NotBlank nahi lagaya, Service layer mein handle karenge agar khaali aaya
    private String slug;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    private BigDecimal price;

    private BigDecimal compareAtPrice; // optional

    @NotNull(message = "Stock is required")
    private Integer stock;

    private String material;

    @NotNull(message = "Brand is required")
    private Long brandId;

    @NotNull(message = "Category is required")
    private Long categoryId;

    private boolean isNew;
    private boolean isBestSeller;
    private boolean isFlashSale;
    private Integer discountPercent;

    // Images ke liye simple list of URLs — form mein "ImagesManager" component
    // ne yehi bheja tha (URL + alt text ka combination)
    private List<ProductImageRequest> images;

    private List<ProductColor> colors;
    private List<String> sizes;
    private List<String> tags;
    private List<ProductSpecification> specifications;
    private List<ProductVariantRequest> variants;

    public ProductRequest() {
    }

    // Getters and Setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getCompareAtPrice() {
        return compareAtPrice;
    }

    public void setCompareAtPrice(BigDecimal compareAtPrice) {
        this.compareAtPrice = compareAtPrice;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public boolean isBestSeller() {
        return isBestSeller;
    }

    public void setBestSeller(boolean bestSeller) {
        isBestSeller = bestSeller;
    }

    public boolean isFlashSale() {
        return isFlashSale;
    }

    public void setFlashSale(boolean flashSale) {
        isFlashSale = flashSale;
    }

    public Integer getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(Integer discountPercent) {
        this.discountPercent = discountPercent;
    }

    public List<ProductImageRequest> getImages() {
        return images;
    }

    public void setImages(List<ProductImageRequest> images) {
        this.images = images;
    }

    public List<ProductColor> getColors() {
        return colors;
    }

    public void setColors(List<ProductColor> colors) {
        this.colors = colors;
    }

    public List<String> getSizes() {
        return sizes;
    }

    public void setSizes(List<String> sizes) {
        this.sizes = sizes;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<ProductSpecification> getSpecifications() {
        return specifications;
    }

    public void setSpecifications(List<ProductSpecification> specifications) {
        this.specifications = specifications;
    }

    public List<ProductVariantRequest> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductVariantRequest> variants) {
        this.variants = variants;
    }

    // ===== Inner Helper Classes (nested static classes) =====
    // Ye chhote helper classes hain jo sirf ProductRequest ke andar hi use hote hain,
    // isliye inhe alag file banane ke bajaye "static nested class" ki tarah andar hi
    // define kar diya — ye Java ka normal pattern hai jab ek chhoti class sirf
    // ek hi jagah use honi ho aur uska koi independent existence na ho.

    public static class ProductImageRequest {
        private String url;
        private String alt;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getAlt() {
            return alt;
        }

        public void setAlt(String alt) {
            this.alt = alt;
        }
    }

    public static class ProductVariantRequest {
        private String color;
        private String size;
        private String sku;
        private Integer stock;

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
    }
}
