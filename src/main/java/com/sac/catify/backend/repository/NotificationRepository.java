package com.sac.catify.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sac.catify.backend.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	// Notification Center page (frontend Phase 14) ke liye — is user ki saari
    // notifications, sabse naye pehle
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    // Header bell icon ka unread count badge dikhane ke liye —
    // "isRead = false" wali kitni notifications hain, ye count chahiye.
    // Note: ye method List return karta hai, actual count Service layer mein
    // .size() se nikalenge (kyunki count-only query alag likhni padti, abhi list se kaam chal jayega)
    List<Notification> findByUserIdAndIsReadFalse(Long userId);
}
