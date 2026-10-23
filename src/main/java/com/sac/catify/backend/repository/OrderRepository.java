package com.sac.catify.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sac.catify.backend.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

	
	// Account > Orders page ke liye — logged-in user ke saare orders
    List<Order> findByUserIdOrderByPlacedAtDesc(Long userId);
    // "OrderByPlacedAtDesc" -> results ko placedAt (date) ke hisaab se
    // NAYE-SE-PURANE order mein sort karke deta hai (jaise frontend mein
    // naya order hamesha list ke top pe dikhta tha)
    
    // Order confirmation/details page ke liye — order number se dhoondhna
    // (jaise "CF-10293" se, na ki internal database ID se)
    Optional<Order> findByOrderNumber(String orderNumber);
    
    // Admin ke Order search feature ke liye
    List<Order> findByOrderNumberContainingIgnoreCase(String orderNumber);
    
    // Admin Dashboard ke "Recent Orders" section ke liye — SAARE orders
    // (kisi bhi user ke), sabse NAYE pehle. Phir Service layer mein hum
    // isme se sirf pehle 5 (ya jitne chahiye) le lenge.
    List<Order> findAllByOrderByPlacedAtDesc();
}
