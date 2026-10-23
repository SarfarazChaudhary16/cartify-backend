package com.sac.catify.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sac.catify.backend.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	
	// Frontend mein /category/:categorySlug route hai — us slug se category dhoondhne ke liye
    Optional<Category> findBySlug(String slug);
    
    // Admin panel mein naya category add karte waqt slug unique hona chahiye,
    // isliye check karna padega ki wo slug pehle se to nahi hai
    boolean existsBySlug(String slug);
}
