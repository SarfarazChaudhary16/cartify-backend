package com.sac.catify.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sac.catify.backend.entity.Review;

//Ye kyun bana: Product Detail page ke Reviews tab (frontend Phase 12) ke liye — 
//kisi specific product ke saare reviews nikalne honge, aur Admin Reviews Moderation page 
//(Phase 23) ke liye bhi yehi repository kaam aayega.

public interface ReviewRepository extends JpaRepository<Review, Long> {

	
	// Product Detail page ke Reviews tab ke liye:
    // "is product ke saare reviews do, sabse naye wale pehle" (frontend mein reviews
    // list top pe latest review dikhata tha)
    // Method naam ka breakdown: findBy + "Product" (Entity field) + "Id" (uska sub-field)
    // + "OrderBy" + "CreatedAt" (field) + "Desc" (sorting direction)
    List<Review> findByProductIdOrderByCreatedAtDesc(Long productId);
    
    
    // "My Reviews" page (Account > My Reviews, frontend Phase 12) ke liye —
    // logged-in user ne khud jo reviews likhe hain, wo saare
    List<Review> findByUserIdOrderByCreatedAtDesc(Long userId);
}
