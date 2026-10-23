package com.sac.catify.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sac.catify.backend.entity.Role;
import com.sac.catify.backend.entity.User;


//JpaRepository<User, Long> ka matlab:
//  - User  -> ye repository User entity ke liye hai
//  - Long  -> User ki Primary Key (id field) ka type Long hai
//Isse extend karte hi humein FREE mein ye saare methods mil jate hain (bina likhe):
//save(), findById(), findAll(), deleteById(), count(), existsById(), waghera.
public interface UserRepository extends JpaRepository<User, Long> {

	
	// ===== Custom Finder Methods =====
    // Spring Data JPA ka ek magic feature: agar method ka naam ek specific pattern
    // follow kare ("findBy" + FieldName), to Spring khud method ka SQL query bana deta hai —
    // hume implementation likhne ki zarurat nahi, bas method signature likhna hai.
    //
    // findByEmail(String email) -> Spring khud samajh jata hai matlab:
    // "SELECT * FROM users WHERE email = ?"

    // Optional<User> isliye use karte hain kyunki ho sakta hai email database mein na mile
    // (jaise login ke waqt galat email daale). Optional force karta hai ki hum
    // null-check zaroor karein, warna NullPointerException ka risk rehta hai.
    Optional<User> findByEmail(String email);
    
    // Login/Signup ke waqt check karna hota hai "kya ye email already registered hai?"
    // existsBy... -> Spring khud query banata hai: "SELECT COUNT(*) > 0 FROM users WHERE email = ?"
    boolean existsByEmail(String email);
    
    // Dashboard ke "Total Customers" stat card ke liye — sirf CUSTOMER
    // role wale users ginta hai (Admin accounts count mein nahi aate).
    // "countBy" pattern Spring Data JPA khud samajh leta hai: "SELECT
    // COUNT(*) FROM users WHERE role = ?" — humein query khud nahi likhni padi.
    long countByRole(Role role);
	
	
}
