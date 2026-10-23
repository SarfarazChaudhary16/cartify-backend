package com.sac.catify.backend.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.sac.catify.backend.dto.WishlistItemResponse;
import com.sac.catify.backend.security.CustomUserDetails;
import com.sac.catify.backend.service.WishlistService;

import java.util.List;
import java.util.Map;

// CartController jaisa hi pattern — @AuthenticationPrincipal se seedha
// current user ki ID milti hai, koi extra DB lookup nahi chahiye
@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    // GET /api/wishlist
    @GetMapping
    public ResponseEntity<List<WishlistItemResponse>> getWishlistItems(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(wishlistService.getWishlistItems(currentUser.getId()));
    }

    // POST /api/wishlist/{productId}
    // Heart icon click karke add karna
    @PostMapping("/{productId}")
    public ResponseEntity<WishlistItemResponse> addToWishlist(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long productId) {
        return ResponseEntity.ok(wishlistService.addToWishlist(currentUser.getId(), productId));
    }

    // DELETE /api/wishlist/{productId}
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeFromWishlist(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long productId) {
        wishlistService.removeFromWishlist(currentUser.getId(), productId);
        return ResponseEntity.noContent().build();
    }

    // GET /api/wishlist/{productId}/check
    // Product Detail/Card pe heart icon "filled" dikhana hai ya "outline"
    // ye check karne ke liye. Map<String, Boolean> use kar rahe hain
    // taaki response JSON aise dikhe: { "isInWishlist": true }
    // (poori DTO class banane ki zarurat nahi thi itni chhoti si cheez ke liye)
    @GetMapping("/{productId}/check")
    public ResponseEntity<Map<String, Boolean>> checkWishlist(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long productId) {
        boolean exists = wishlistService.isInWishlist(currentUser.getId(), productId);
        return ResponseEntity.ok(Map.of("isInWishlist", exists));
    }
}
