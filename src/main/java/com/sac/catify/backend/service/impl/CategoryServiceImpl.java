package com.sac.catify.backend.service.impl;

import org.springframework.stereotype.Service;

import com.sac.catify.backend.dto.CategoryRequest;
import com.sac.catify.backend.dto.CategoryResponse;
import com.sac.catify.backend.entity.Category;
import com.sac.catify.backend.exception.ApiException;
import com.sac.catify.backend.repository.CategoryRepository;
import com.sac.catify.backend.repository.ProductRepository;
import com.sac.catify.backend.service.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    // ProductRepository yahan isliye chahiye taaki har category ke saath
    // "kitne products hain" ye count nikal sakein (real-time calculation,
    // productCount ko database mein store nahi kiya, yaad hai?)
    private final ProductRepository productRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());

        // Slug khaali hai to naam se generate karo (Product jaisa hi pattern)
        String slug = (request.getSlug() == null || request.getSlug().isBlank())
                ? generateSlug(request.getName())
                : request.getSlug();
        category.setSlug(slug);
        category.setImage(request.getImage());

        Category saved = categoryRepository.save(category);

        // Naye category mein abhi 0 products honge (abhi-abhi bana hai)
        return mapToResponse(saved, 0);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                // .map(category -> ...) -> har category ke liye uska product count
                // nikalte hain aur DTO banate hain
                .map(category -> mapToResponse(category, countProducts(category.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse getCategoryBySlug(String slug) {
    	
        Category category = categoryRepository.findBySlug(slug)
        		
        		.orElseThrow(() -> ApiException.notFound("Category not found with slug: " + slug));
        
        return mapToResponse(category, countProducts(category.getId()));
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
        		.orElseThrow(() -> ApiException.notFound("Category not found with id: " + id));

        category.setName(request.getName());
        if (request.getSlug() != null && !request.getSlug().isBlank()) {
            category.setSlug(request.getSlug());
        }
        category.setImage(request.getImage());

        Category updated = categoryRepository.save(category);
        return mapToResponse(updated, countProducts(updated.getId()));
    }

    @Override
    public void deleteCategory(Long id) {
    	
        if (!categoryRepository.existsById(id)) {
        	
        	throw ApiException.notFound("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
        // Note: Agar is category ke andar abhi bhi products hain, to Product entity
        // ka "category" field @JoinColumn(nullable = false) hone ki wajah se
        // MySQL foreign-key constraint error dega delete karte waqt. Ye jaan-boojh
        // kar aisa rakha hai — admin ko pehle products dusri category mein move
        // karne padenge, tabhi khaali category delete hogi (data-safety ke liye achha hai).
    }

    // ===== Private Helpers =====

    private long countProducts(Long categoryId) {
        // findByCategoryId() already ProductRepository mein bana hua hai (Phase B2),
        // hum bas uski List ka size() nikal rahe hain. (Bade scale pe ye inefficient
        // hai — better hota ek dedicated COUNT query, lekin abhi ke liye simple/readable
        // rakha hai; optimize karna chahein to countByCategoryId() method Repository
        // mein add kar sakte hain baad mein.)
        return productRepository.findByCategoryId(categoryId).size();
    }

    private String generateSlug(String name) {
        return name.toLowerCase().trim()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }

    private CategoryResponse mapToResponse(Category category, long productCount) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getImage(),
                productCount
        );
    }
}
