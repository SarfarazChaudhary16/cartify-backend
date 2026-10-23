package com.sac.catify.backend.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.sac.catify.backend.dto.CartItemRequest;
import com.sac.catify.backend.dto.CartItemResponse;
import com.sac.catify.backend.security.CustomUserDetails;
import com.sac.catify.backend.service.CartService;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    // NOTE: Ab UserRepository yahan inject karne ki zarurat NAHI hai —
    // CustomUserDetails khud userId de deta hai, extra DB lookup hatane
    // se ye Controller aur clean/chhota ho gaya
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // GET /api/cart
    @GetMapping
    public ResponseEntity<List<CartItemResponse>> getCartItems(
            // @AuthenticationPrincipal Spring Security ko batata hai:
            // "current logged-in user ka UserDetails object seedha yahan
            // parameter mein daal do" — CustomUserDetailsService mein
            // humne jo CustomUserDetails return kiya tha, wahi yahan aata hai.
            // Ye JwtAuthFilter ke SecurityContext.setAuthentication() call
            // se hi connect hai — usi authentication object ke andar ka
            // "principal" hi yahan inject hota hai.
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(cartService.getCartItems(currentUser.getId()));
    }

    // POST /api/cart
    @PostMapping
    public ResponseEntity<CartItemResponse> addToCart(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.addToCart(currentUser.getId(), request));
    }

    // PUT /api/cart/{cartItemId}?quantity=3
    @PutMapping("/{cartItemId}")
    public ResponseEntity<CartItemResponse> updateQuantity(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long cartItemId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartService.updateQuantity(currentUser.getId(), cartItemId, quantity));
    }

    // DELETE /api/cart/{cartItemId}
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> removeFromCart(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long cartItemId) {
        cartService.removeFromCart(currentUser.getId(), cartItemId);
        return ResponseEntity.noContent().build();
    }

    // DELETE /api/cart
    @DeleteMapping
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal CustomUserDetails currentUser) {
        cartService.clearCart(currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}