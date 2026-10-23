package com.sac.catify.backend.entity;


import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//Sabse important entity: Poore e-commerce ka core hai ye. 
//Frontend ke Product TypeScript interface ka exact database version — 
//sab kuch (price, stock, flags, images, variants, specs) ek jagah jud raha hai.

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    // BigDecimal use kar rahe hain price ke liye, "double" nahi.
    // Kyun: double/float mein rounding errors aate hain (jaise 0.1 + 0.2 = 0.30000000004),
    // jo paison ke calculation mein bilkul acceptable nahi hai. BigDecimal exact precision deta hai.
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "compare_at_price", precision = 10, scale = 2)
    private BigDecimal compareAtPrice; // strike-through original price, nullable

    @Column(nullable = false)
    private Double rating = 0.0;

    @Column(name = "review_count", nullable = false)
    private Integer reviewCount = 0;

    @Column(nullable = false)
    private Integer stock;

    private String material;

    @Column(name = "is_new")
    private boolean isNew;

    @Column(name = "is_best_seller")
    private boolean isBestSeller;

    @Column(name = "is_flash_sale")
    private boolean isFlashSale;

    @Column(name = "discount_percent")
    private Integer discountPercent; // nullable, sirf flash sale items ke liye

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // ===== Relationships: Many Products belong to One Brand / One Category =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // ===== One Product has Many Images =====
    // mappedBy = "product" batata hai: "ye relationship ProductImage entity ke andar
    // 'product' field se already define hai, yahan dobara foreign key mat banao"
    // cascade = ALL matlab: agar Product delete/save hota hai, uske saath saari images
    // bhi automatically delete/save ho jayengi (hume manually har image handle nahi karni)
    // orphanRemoval = true matlab: agar koi image is list se hata di jaye, wo database se bhi delete ho jayegi
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();

    // ===== One Product has Many Variants (color+size+sku+stock combinations) =====
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVariant> variants = new ArrayList<>();

    // ===== Simple lists: tags, sizes =====
    // @ElementCollection use karte hain jab hume sirf ek list of simple values (String, Integer)
    // store karni ho, bina unko poori Entity banaye. JPA khud ek chhota "helper table" bana dega
    // (jaise product_tags: product_id, tag) bina hume manually us table ke liye class banane ki zarurat.
    @ElementCollection
    @CollectionTable(name = "product_tags", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "product_sizes", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "size")
    private List<String> sizes = new ArrayList<>();

    // ===== Specifications: list of label-value pairs (Embeddable) =====
    @ElementCollection
    @CollectionTable(name = "product_specifications", joinColumns = @JoinColumn(name = "product_id"))
    private List<ProductSpecification> specifications = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "product_colors", joinColumns = @JoinColumn(name = "product_id"))
    private List<ProductColor> colors = new ArrayList<>();

    // ===== Constructors =====

    public Product() {
    }

    public Product(String slug, String title, String description, BigDecimal price,
                    Integer stock, Brand brand, Category category) {
        this.slug = slug;
        this.title = title;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.brand = brand;
        this.category = category;
        this.createdAt = LocalDateTime.now();
        
    }

    // ===== Getters and Setters =====

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<ProductImage> getImages() {
        return images;
    }

    public void setImages(List<ProductImage> images) {
        this.images = images;
    }

    public List<ProductVariant> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductVariant> variants) {
        this.variants = variants;
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

    public List<ProductSpecification> getSpecifications() {
        return specifications;
    }

    public void setSpecifications(List<ProductSpecification> specifications) {
        this.specifications = specifications;
    }
    
    public List<ProductColor> getColors() {
        return colors;
    }

    public void setColors(List<ProductColor> colors) {
        this.colors = colors;
    }
}
