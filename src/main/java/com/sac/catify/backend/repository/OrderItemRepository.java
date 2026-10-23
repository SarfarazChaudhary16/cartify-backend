package com.sac.catify.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sac.catify.backend.entity.OrderItem;


//Kyun bana: Frontend mein review ke saath "Verified Purchase" badge dikhta tha (Phase 12).
//Ye tabhi sahi hoga jab hum actually check karein ki user ne us product ko kabhi order kiya hai 
//ya nahi — sirf user ke bharose pe nahi chhod sakte.
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Ye method THODA khaas hai — dhyan do naam mein "Order_User_Id" likha
    // hai (underscore ke saath). Spring Data JPA ka ye ek special syntax
    // hai jab hume NESTED property traverse karni ho: OrderItem ke andar
    // "order" field hai (Order object), Order ke andar "user" field hai
    // (User object), User ke andar "id" hai. Underscore explicitly batata
    // hai Spring ko "Order.User.Id" ki tarah traverse karo (agar hum
    // "OrderUserId" bina underscore likhte, kabhi-kabhi Spring confuse ho
    // sakta hai ki ye "OrderUser" naam ka koi field hai ya "Order" ke andar
    // "userId" — underscore ambiguity hata deta hai).
    //
    // Query jo banegi: "SELECT COUNT(*) > 0 FROM order_items oi
    //                    JOIN orders o ON oi.order_id = o.id
    //                    WHERE o.user_id = ? AND oi.product_id = ?"
    //
    // Isse hum check karte hain: "kya is USER ne is PRODUCT ko KABHI order
    // kiya hai" — agar haan, review pe "Verified Purchase" badge dikhega
    boolean existsByOrder_User_IdAndProductId(Long userId, Long productId);
}
