package com.sac.catify.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.sac.catify.backend.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

	// Cart page load karte waqt: "is user ke cart ke saare items do"
    List<CartItem> findByUserId(Long userId);
	
	// Jab user "Add to Cart" kare, hume check karna hai: "kya ye exact product
    // (same color/size ke saath) already cart mein hai?" — agar hai to sirf quantity
    // badhaani hai, naya row nahi banana. Isliye ye specific lookup method.
    Optional<CartItem> findByUserIdAndProductIdAndColorAndSize(
            Long userId, Long productId, String color, String size);
    
 // Checkout complete hone ke baad poora cart empty karna hai (frontend ka clearCart()).
    // @Transactional zaroori hai kyunki delete queries ko ek "transaction" (atomic operation)
    // ke andar chalana safe hota hai — beech mein kuch fail ho to sab rollback ho jaye,
    // aadha-adhura delete na ho.
    @Transactional
    void deleteByUserId(Long userId);
}
