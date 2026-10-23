package com.sac.catify.backend.service;

import java.util.List;

import com.sac.catify.backend.dto.CartItemRequest;
import com.sac.catify.backend.dto.CartItemResponse;

//Cart se related saare operations ka contract. Frontend ke useCartStore.ts
//(addItem, removeItem, updateQuantity, clearCart) ka backend-equivalent.
//
//Dhyan do: har method mein "userId" pehla parameter hai — kyunki Cart
//hamesha kisi SPECIFIC logged-in user ka hota hai (koi "global" cart nahi
//hota). Ye userId hume JWT token se milega (Controller mein SecurityContext
//se nikalenge) — user khud apni ID frontend se nahi bhejega (security:
//warna koi user "userId": 5 bhej ke kisi AUR ke cart mein cheez daal sakta tha!)
public interface CartService {

	
	// Cart page load hote hi: is user ke cart ke saare items
    List<CartItemResponse> getCartItems(Long userId);
    
    // "Add to Cart" button — agar same product+color+size already cart
    // mein hai, to sirf quantity BADHATA hai (naya row nahi banata).
    // Warna naya CartItem row banata hai.
    CartItemResponse addToCart(Long userId, CartItemRequest request);
    
    // Quantity stepper (+/-) use karne pe — specific cart-item-id ki
    // quantity update karta hai
    CartItemResponse updateQuantity(Long userId, Long cartItemId, Integer quantity);
    
    // Cart se ek item hatane ke liye ("Remove" button, ya quantity 0 tak
    // le jaane pe)
    void removeFromCart(Long userId, Long cartItemId);
    
    // Checkout complete hone ke baad poora cart khaali karna (frontend
    // ka clearCart() action)
    void clearCart(Long userId);
}
