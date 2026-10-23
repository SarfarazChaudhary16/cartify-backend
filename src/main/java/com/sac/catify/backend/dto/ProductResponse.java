package com.sac.catify.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.sac.catify.backend.entity.ProductColor;
import com.sac.catify.backend.entity.ProductSpecification;

//Note: Isme ProductColor aur ProductSpecification (jo humne Entity 
//package mein @Embeddable banaye the) ko seedha reuse kar rahe hain — 
//kyunki wo Entity nahi hain (koi ID/table nahi), sirf plain data-holder hain, 
//isliye inhe DTO mein use karna safe hai, koi circular reference risk nahi.

//Product Detail page + Catalog page + Admin Products table — sabke liye
//ye ek hi "safe shape" hai jo frontend ko bhejte hain. Brand/Category ko
//poora Entity na bhej ke sirf unki id+name+slug flatten karke bhej rahe hain
//(taaki JSON halka rahe aur circular reference (Product->Brand->Products->...) na ho).
public class ProductResponse {

	
	
	private Long id;
    private String slug;
    private String title;
    private String description;
    private BigDecimal price;
    private BigDecimal compareAtPrice;
    private Double rating;
    private Integer reviewCount;
    private Integer stock;
    private String material;
    private boolean isNew;
    private boolean isBestSeller;
    private boolean isFlashSale;
    private Integer discountPercent;
    private LocalDateTime createdAt;
    
    // Brand/Category ka poora object nahi, sirf zaroori fields flatten kiye
    private Long brandId;
    private String brandName;
    private String brandSlug;

    private Long categoryId;
    private String categoryName;
    private String categorySlug;
    
    private List<ProductImageResponse> images;
    private List<ProductVariantResponse> variants;
    private List<ProductColor> colors;               // Embeddable hai, direct reuse safe hai
    private List<ProductSpecification> specifications; // Embeddable hai, direct reuse safe hai
    private List<String> tags;
    private List<String> sizes;
    
    
	public ProductResponse() {
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getSlug() {
		return slug;
	}


	public void setSlug(String slug) {
		this.slug = slug;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
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


	public Double getRating() {
		return rating;
	}


	public void setRating(Double rating) {
		this.rating = rating;
	}


	public Integer getReviewCount() {
		return reviewCount;
	}


	public void setReviewCount(Integer reviewCount) {
		this.reviewCount = reviewCount;
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


	public boolean isNew() {
		return isNew;
	}


	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}


	public boolean isBestSeller() {
		return isBestSeller;
	}


	public void setBestSeller(boolean isBestSeller) {
		this.isBestSeller = isBestSeller;
	}


	public boolean isFlashSale() {
		return isFlashSale;
	}


	public void setFlashSale(boolean isFlashSale) {
		this.isFlashSale = isFlashSale;
	}


	public Integer getDiscountPercent() {
		return discountPercent;
	}


	public void setDiscountPercent(Integer discountPercent) {
		this.discountPercent = discountPercent;
	}


	public LocalDateTime getCreatedAt() {
		return createdAt;
	}


	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}


	public Long getBrandId() {
		return brandId;
	}


	public void setBrandId(Long brandId) {
		this.brandId = brandId;
	}


	public String getBrandName() {
		return brandName;
	}


	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}


	public String getBrandSlug() {
		return brandSlug;
	}


	public void setBrandSlug(String brandSlug) {
		this.brandSlug = brandSlug;
	}


	public Long getCategoryId() {
		return categoryId;
	}


	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}


	public String getCategoryName() {
		return categoryName;
	}


	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}


	public String getCategorySlug() {
		return categorySlug;
	}


	public void setCategorySlug(String categorySlug) {
		this.categorySlug = categorySlug;
	}


	public List<ProductImageResponse> getImages() {
		return images;
	}


	public void setImages(List<ProductImageResponse> images) {
		this.images = images;
	}


	public List<ProductVariantResponse> getVariants() {
		return variants;
	}


	public void setVariants(List<ProductVariantResponse> variants) {
		this.variants = variants;
	}


	public List<ProductColor> getColors() {
		return colors;
	}


	public void setColors(List<ProductColor> colors) {
		this.colors = colors;
	}


	public List<ProductSpecification> getSpecifications() {
		return specifications;
	}


	public void setSpecifications(List<ProductSpecification> specifications) {
		this.specifications = specifications;
	}


	public List<String> getTags() {
		return tags;
	}


	public void setTags(List<String> tags) {
		this.tags = tags;
	}


	public List<String> getSizes() {
		return sizes;
	}


	public void setSizes(List<String> sizes) {
		this.sizes = sizes;
	}
    
}
