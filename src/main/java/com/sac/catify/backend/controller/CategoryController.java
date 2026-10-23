package com.sac.catify.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sac.catify.backend.dto.CategoryRequest;
import com.sac.catify.backend.dto.CategoryResponse;
import com.sac.catify.backend.service.CategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

	private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    
 // POST /api/categories
    // Admin > Categories page > "Add Category" button
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
    		
    		@Valid @RequestBody CategoryRequest request) {
    	
        CategoryResponse response = categoryService.createCategory(request);
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
 // GET /api/categories
    // Header ke category-menu, Home page "Featured Categories", aur
    // Admin Categories list — teeno isi endpoint se data lete hain
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
    	
        return ResponseEntity.ok(categoryService.getAllCategories());
    }
    
 // GET /api/categories/slug/{slug}
    // Frontend route /category/:categorySlug ke liye (category ka naam/image
    // header mein dikhane ke liye chahiye ho sakta hai catalog page pe)
    @GetMapping("/slug/{slug}")
    public ResponseEntity<CategoryResponse> getCategoryBySlug(
    		
    		@PathVariable String slug) {
    
        return ResponseEntity.ok(categoryService.getCategoryBySlug(slug));
    }
    
    
    // PUT /api/categories/{id}
    // Admin > Categories page > Edit (pencil icon)
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }
    
    // DELETE /api/categories/{id}
    // Admin > Categories page > Delete (trash icon)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
    
    
}
