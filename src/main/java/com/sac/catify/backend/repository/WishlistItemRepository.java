package com.sac.catify.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.sac.catify.backend.entity.WishlistItem;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {

	
	// Wishlist page load karte waqt: "is user ki wishlist ke saare items"
    List<WishlistItem> findByUserId(Long userId);
    
    // "Heart" icon toggle karte waqt check karna: "kya ye product pehle se wishlist mein hai?"
    // (frontend ke useWishlistStore ka has() function jaisa kaam)
    Optional<WishlistItem> findByUserIdAndProductId(Long userId, Long productId);
    
 // Wishlist se remove karne ke liye (heart icon dobara click karne pe)
    @Transactional
    void deleteByUserIdAndProductId(Long userId, Long productId);
}
