package com.sac.catify.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sac.catify.backend.entity.SupportTicket;

import java.util.List;

// JpaRepository<SupportTicket, Long> extend karte hi humein FREE mein
// save(), findById(), findAll(), deleteById() jaise saare basic CRUD
// methods mil jate hain — Spring Data JPA khud inka implementation
// runtime pe generate kar deta hai, hume ek line code likhne ki zarurat nahi.
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {

    // Account > My Tickets (ya Help Center ka "raised tickets" section)
    // ke liye — is user ne jitne bhi tickets raise kiye hain, sabse
    // NAYE pehle. Spring Data JPA method-naam se khud SQL query bana
    // leta hai: "SELECT * FROM support_tickets WHERE user_id = ?
    //            ORDER BY created_at DESC"
    List<SupportTicket> findByUserIdOrderByCreatedAtDesc(Long userId);
}
