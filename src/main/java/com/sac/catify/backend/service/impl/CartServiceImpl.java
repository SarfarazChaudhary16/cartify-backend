package com.sac.catify.backend.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sac.catify.backend.dto.CartItemRequest;
import com.sac.catify.backend.dto.CartItemResponse;
import com.sac.catify.backend.entity.CartItem;
import com.sac.catify.backend.entity.Product;
import com.sac.catify.backend.entity.User;
import com.sac.catify.backend.exception.ApiException;
import com.sac.catify.backend.repository.CartItemRepository;
import com.sac.catify.backend.repository.ProductRepository;
import com.sac.catify.backend.repository.UserRepository;
import com.sac.catify.backend.service.CartService;

@Service
public class CartServiceImpl implements CartService {

	// 3 repositories chahiye: CartItem (khud cart entries ke liye),
    // Product (jab naya item add ho ya live price/stock chahiye ho),
    // User (jab naya CartItem banana ho, usmein "User" object link karna
    // padta hai — @ManyToOne relationship, sirf userId (Long) se kaam
    // nahi chalta, poora User Entity object chahiye hota hai JPA ko)
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartServiceImpl(CartItemRepository cartItemRepository,
                            ProductRepository productRepository,
                            UserRepository userRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }
	
//================================================================================================
	
	@Override
	public List<CartItemResponse> getCartItems(Long userId) {

		
		 return cartItemRepository.findByUserId(userId).stream()
	                .map(this::mapToResponse)
	                .collect(Collectors.toList());
	}

//================================================================================================
	
	@Override
	public CartItemResponse addToCart(Long userId, CartItemRequest request) {
		
		// Step 1: Product exist karta hai ya nahi check karte hain, aur
        // uska current data (price, stock) bhi chahiye hoga
        Product product = productRepository.findById(request.getProductId())
        		
        		.orElseThrow(() -> ApiException.notFound("Product not found with id: " + request.getProductId()));
        
        // Step 2: Check karte hain — kya YE EXACT combination (same product,
        // same color, same size) is user ke cart mein PEHLE SE hai?
        // Repository mein humne Phase B2 mein ye method banaya tha:
        // findByUserIdAndProductIdAndColorAndSize()
        Optional<CartItem> existingItem = cartItemRepository
                .findByUserIdAndProductIdAndColorAndSize(userId, request.getProductId(),
                        request.getColor(), request.getSize());
        
        CartItem cartItem;
        
        if (existingItem.isPresent()) {
            // Case A: Already cart mein hai — sirf QUANTITY badha dete hain,
            // naya row NAHI banate (frontend mein bhi yehi expected behavior
            // tha — "add to cart" dobara click karne pe duplicate row nahi
            // banti, existing item ki quantity badh jati hai)
            cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        } else {
            // Case B: Bilkul naya combination hai — naya CartItem row banate hain
            User user = userRepository.findById(userId)
            		
            		.orElseThrow(() -> ApiException.notFound("User not found with id: " + userId));

            cartItem = new CartItem(user, product, request.getQuantity(),
                    request.getColor(), request.getSize());
        }

        CartItem saved = cartItemRepository.save(cartItem);
        return mapToResponse(saved);
	}

//================================================================================================

	@Override
	public CartItemResponse updateQuantity(Long userId, Long cartItemId, Integer quantity) {
		
		 CartItem cartItem = cartItemRepository.findById(cartItemId)
				 
				 .orElseThrow(() -> ApiException.notFound("Cart item not found with id: " + cartItemId));
		 
		    // ===== SECURITY CHECK — bahut IMPORTANT step hai ye =====
	        // Hum verify karte hain ki ye cart-item, JISKA userId diya gaya hai
	        // (jo JWT token se aaya, Controller se), USI user ka hai.
	        // Agar hum ye check NA karein, to koi bhi logged-in user (jaise
	        // userId=5) URL mein kisi AUR ka cartItemId (jaise userId=8 ka
	        // item, id=42) daal ke uski quantity change kar sakta tha!
	        // Ye "Insecure Direct Object Reference" (IDOR) naam ki common
	        // security vulnerability se bachne ka tareeka hai.
	        if (!cartItem.getUser().getId().equals(userId)) {
	        	
	        	throw ApiException.forbidden("This cart item does not belong to you.");
	        }
	        
	        cartItem.setQuantity(quantity);
	        CartItem updated = cartItemRepository.save(cartItem);
	        return mapToResponse(updated);

	}

//================================================================================================

	@Override
	public void removeFromCart(Long userId, Long cartItemId) {


		CartItem cartItem = cartItemRepository.findById(cartItemId)
				
				.orElseThrow(() -> ApiException.notFound("Cart item not found with id: " + cartItemId));

        // Yahan bhi wahi security check — apne cart ke alawa kisi aur
        // ka item delete NAHI kar sakte
        if (!cartItem.getUser().getId().equals(userId)) {
        	
        	throw ApiException.forbidden("This cart item does not belong to you.");
        }

        cartItemRepository.delete(cartItem);
		
	}

//================================================================================================
	
	@Override
	public void clearCart(Long userId) {
		
		 // Repository mein Phase B2 mein hi ye method bana tha —
        // "is userId ke SAARE cart items ek saath delete kar do"
        cartItemRepository.deleteByUserId(userId);
		
	}
	
//================================================================================================

	 // ===== Private Helper =====

    // CartItem Entity ko CartItemResponse DTO mein convert karta hai —
    // is process mein Product ka LIVE price/stock/title/image nikalte hain
    // (cart_items table mein khud ye values store NAHI hain, hamesha
    // Product entity se "fresh" nikalte hain, taaki agar admin ne price
    // badla ho to cart turant naya price dikhaye)
    private CartItemResponse mapToResponse(CartItem cartItem) {
        Product product = cartItem.getProduct();

        // lineTotal = price * quantity. BigDecimal ka multiply() method
        // use kar rahe hain (normal "*" operator BigDecimal pe kaam nahi
        // karta, kyunki BigDecimal ek class hai, primitive number nahi —
        // isliye uske apne special methods hote hain: add(), multiply(), waghera)
        BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        return new CartItemResponse(
                cartItem.getId(),
                product.getId(),
                product.getTitle(),
                product.getImages().isEmpty() ? null : product.getImages().get(0).getUrl(),
                product.getPrice(),
                product.getStock(),
                cartItem.getQuantity(),
                cartItem.getColor(),
                cartItem.getSize(),
                lineTotal
        );
    }
}

