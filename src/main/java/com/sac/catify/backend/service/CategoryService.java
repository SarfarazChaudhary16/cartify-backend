package com.sac.catify.backend.service;

import java.util.List;

import com.sac.catify.backend.dto.CategoryRequest;
import com.sac.catify.backend.dto.CategoryResponse;

// Category ke saare operations ka contract — Storefront category-menu
// aur Admin CategoriesPage dono isi service ko use karenge
public interface CategoryService {

    CategoryResponse createCategory(CategoryRequest request);

    // Frontend header ke Category dropdown/menu ke liye — saari categories
    List<CategoryResponse> getAllCategories();

    // /category/:categorySlug route ke liye
    CategoryResponse getCategoryBySlug(String slug);

    CategoryResponse updateCategory(Long id, CategoryRequest request);

    void deleteCategory(Long id);
}
