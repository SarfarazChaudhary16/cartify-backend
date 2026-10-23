package com.sac.catify.backend.service;

import java.util.List;

import com.sac.catify.backend.dto.ProductRequest;
import com.sac.catify.backend.dto.ProductResponse;

// Product se related saare business operations ka "contract".
// Frontend ke Catalog page, Product Detail page, aur Admin Products page (CRUD) —
// teeno isi ek service ke different methods use karke chalenge.
public interface ProductService {

    // Admin > Add Product form submit hone pe
    ProductResponse createProduct(ProductRequest request);

    // Product Detail page (/product/:slug) ke liye
    ProductResponse getProductBySlug(String slug);

    // Admin Edit Product page load karte waqt (existing data form mein bharne ke liye)
    ProductResponse getProductById(Long id);

    // Catalog page (/products) — saare products list karne ke liye
    List<ProductResponse> getAllProducts();

    // Category page (/category/:slug) ke liye — sirf us category ke products
    List<ProductResponse> getProductsByCategory(Long categoryId);

    // Brand page (/brand/:slug) ke liye
    List<ProductResponse> getProductsByBrand(Long brandId);

    // Search page (/search?q=...) ke liye
    List<ProductResponse> searchProducts(String keyword);

    // Home page ke "New Arrivals" section ke liye
    List<ProductResponse> getNewArrivals();

    // Home page ke "Best Sellers" section ke liye
    List<ProductResponse> getBestSellers();

    // Home page ke "Flash Sale" section ke liye
    List<ProductResponse> getFlashSaleProducts();

    // Admin > Edit Product form submit hone pe
    ProductResponse updateProduct(Long id, ProductRequest request);

    // Admin > Delete Product button click hone pe
    void deleteProduct(Long id);
}
