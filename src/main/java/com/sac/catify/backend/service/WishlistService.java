package com.sac.catify.backend.service;


import java.util.List;

import com.sac.catify.backend.dto.WishlistItemResponse;

// Wishlist ke saare operations ka contract. Frontend ke useWishlistStore.ts
// (toggle, has) ka backend-equivalent — bas yahan "toggle" ko hum 2 alag
// methods (add/remove) mein split kar rahe hain, kyunki REST API mein
// "explicit intent" (POST=add, DELETE=remove) rakhna behtar practice hai
// bajaye ek hi "toggle" endpoint ke jo kabhi add karega kabhi remove
// (client ko pata nahi chalega result kya hoga bina response padhe).
public interface WishlistService {

    // Wishlist page load hote hi: is user ki saari wishlist items
    List<WishlistItemResponse> getWishlistItems(Long userId);

    // "Heart" icon click karke wishlist mein add karna
    WishlistItemResponse addToWishlist(Long userId, Long productId);

    // Heart icon dobara click karke (ya wishlist page se) remove karna
    void removeFromWishlist(Long userId, Long productId);

    // Product card pe heart icon "filled" dikhana hai ya "outline" —
    // ye check karne ke liye (frontend ke has() function jaisa)
    boolean isInWishlist(Long userId, Long productId);
}
