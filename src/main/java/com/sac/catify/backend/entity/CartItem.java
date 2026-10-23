package com.sac.catify.backend.entity;


import jakarta.persistence.*; 

//Kyun ye entity zaroori hai: Frontend mein cart localStorage (Zustand persist) mein tha. 
//Real backend mein hume cart server-side persist karna hoga — taaki user kisi bhi device 
//se login kare, uska cart wahi ka wahi mile (localStorage sirf ek hi browser tak limited hota hai). 
//Isliye User ke saath Many-to-One relationship.

// Cart ka ek single item — kis user ne, kaunsa product, kitni quantity mein daala hai.
// Server-side store karne ka fayda: user kisi bhi device se login kare, cart same rahega
// (frontend ke localStorage-only cart ka limitation yahi tha — device-specific tha).
@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Product ko seedha reference karte hain (Order ke OrderItem jaisa snapshot nahi banate),
    // kyunki cart mein hume hamesha LIVE/current price aur stock dikhana hai, purana snapshot nahi.
    // Jab tak order place nahi hota, price change ho sakta hai aur cart mein wahi latest dikhna chahiye.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    private String color; // selected variant color, nullable

    private String size; // selected variant size, nullable

    public CartItem() {
    }

    public CartItem(User user, Product product, Integer quantity, String color, String size) {
        this.user = user;
        this.product = product;
        this.quantity = quantity;
        this.color = color;
        this.size = size;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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
