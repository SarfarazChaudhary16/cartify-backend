package com.sac.catify.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sac.catify.backend.entity.Brand;

public interface BrandRepository extends JpaRepository<Brand, Long> {

	
	// Frontend mein /brand/:brandSlug route hai — CategoryRepository jaisa hi pattern
    Optional<Brand> findBySlug(String slug);

    boolean existsBySlug(String slug);
}
