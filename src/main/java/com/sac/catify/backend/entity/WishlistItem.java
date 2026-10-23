package com.sac.catify.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

//Kyun simple: Wishlist mein bas "kis user ne kaunsa product wishlist mein daala" 
//itni hi info chahiye — na quantity, na variant. Isliye CartItem se bhi simpler entity.

// Wishlist bas ek simple "user ne ye product save kiya" record hai —
// cart jaisi quantity/variant complexity yahan nahi chahiye.

@Entity
@Table(name = "wishlist_items", uniqueConstraints = {
    // Ek user same product ko dobara wishlist mein add na kar sake (duplicate rows avoid karne ke liye)
    @UniqueConstraint(columnNames = {"user_id", "product_id"})
})
public class WishlistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt;

    public WishlistItem() {
    }

    public WishlistItem(User user, Product product) {
        this.user = user;
        this.product = product;
        this.addedAt = LocalDateTime.now();
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

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }
}
