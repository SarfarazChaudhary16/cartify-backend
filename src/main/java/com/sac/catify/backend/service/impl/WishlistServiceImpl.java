package com.sac.catify.backend.service.impl;


import org.springframework.stereotype.Service;

import com.sac.catify.backend.dto.WishlistItemResponse;
import com.sac.catify.backend.entity.Product;
import com.sac.catify.backend.entity.User;
import com.sac.catify.backend.entity.WishlistItem;
import com.sac.catify.backend.exception.ApiException;
import com.sac.catify.backend.repository.ProductRepository;
import com.sac.catify.backend.repository.UserRepository;
import com.sac.catify.backend.repository.WishlistItemRepository;
import com.sac.catify.backend.service.WishlistService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WishlistServiceImpl implements WishlistService {

    private final WishlistItemRepository wishlistItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public WishlistServiceImpl(WishlistItemRepository wishlistItemRepository,
                                ProductRepository productRepository,
                                UserRepository userRepository) {
        this.wishlistItemRepository = wishlistItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<WishlistItemResponse> getWishlistItems(Long userId) {
        return wishlistItemRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public WishlistItemResponse addToWishlist(Long userId, Long productId) {

        // Step 1: Check karte hain — kya ye product PEHLE SE is user ki
        // wishlist mein hai? Agar hai, to duplicate row NAHI banate,
        // seedha EXISTING wala hi return kar dete hain (idempotent behavior —
        // matlab same operation baar-baar karne se bhi result same/safe rehta hai)
        var existing = wishlistItemRepository.findByUserIdAndProductId(userId, productId);
        if (existing.isPresent()) {
            return mapToResponse(existing.get());
        }

        // Step 2: Naya wishlist item banate hain
        User user = userRepository.findById(userId)
        		.orElseThrow(() -> ApiException.notFound("User not found with id: " + userId));

        Product product = productRepository.findById(productId)
        		.orElseThrow(() -> ApiException.notFound("Product not found with id: " + productId));

        WishlistItem wishlistItem = new WishlistItem(user, product);
        WishlistItem saved = wishlistItemRepository.save(wishlistItem);

        return mapToResponse(saved);
    }

    @Override
    public void removeFromWishlist(Long userId, Long productId) {
        // Repository mein Phase B2 mein ye method bana diya tha:
        // "is user ke is product ki wishlist-entry delete karo"
        // Agar entry exist hi nahi karti thi, ye method chup-chaap kuch
        // nahi karega (koi error nahi) — jo yahan acceptable hai, kyunki
        // "remove" ka result end-mein same hi hai: product wishlist mein NAHI hai
        wishlistItemRepository.deleteByUserIdAndProductId(userId, productId);
    }

    @Override
    public boolean isInWishlist(Long userId, Long productId) {
        return wishlistItemRepository.findByUserIdAndProductId(userId, productId).isPresent();
    }

    // ===== Private Helper =====

    private WishlistItemResponse mapToResponse(WishlistItem wishlistItem) {
        Product product = wishlistItem.getProduct();

        return new WishlistItemResponse(
                wishlistItem.getId(),
                product.getId(),
                product.getTitle(),
                product.getImages().isEmpty() ? null : product.getImages().get(0).getUrl(),
                product.getPrice(),
                product.getRating(),
                product.getStock()
        );
    }
}
