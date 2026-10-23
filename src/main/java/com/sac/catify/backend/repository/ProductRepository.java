package com.sac.catify.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sac.catify.backend.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	
	// Frontend mein /product/:slug route hai — product detail page isi se product dhoondhta hai
    Optional<Product> findBySlug(String slug);
    
    
    boolean existsBySlug(String slug);
    
    // Category page (/category/:categorySlug) ke liye — us category ke saare products
    // "findByCategoryId" -> Spring samajh jata hai Product ke andar "category" field hai (Many-to-One),
    // aur uski "id" property se match karna hai
    List<Product> findByCategoryId(Long categoryId);
    
    
    // Brand page (/brand/:brandSlug) ke liye
    List<Product> findByBrandId(Long brandId);

    // Home page ke "New Arrivals" section ke liye — jinka isNew = true hai
    List<Product> findByIsNewTrue();

    // Home page ke "Best Sellers" section ke liye
    List<Product> findByIsBestSellerTrue();

    // Home page ke "Flash Sale" section ke liye
    List<Product> findByIsFlashSaleTrue();
    
    // Search page ke liye — title mein keyword contains kare (case-insensitive)
    // "ContainingIgnoreCase" -> SQL ka "LIKE %keyword%" query banta hai, chhote-bade letters ignore karke
    List<Product> findByTitleContainingIgnoreCase(String keyword);
}
