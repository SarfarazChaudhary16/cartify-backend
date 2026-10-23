package com.sac.catify.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sac.catify.backend.entity.InventoryHistory;

import java.util.List;

// JpaRepository extend karte hi save(), findAll(), findById() jaise
// basic CRUD methods FREE mein mil jate hain
public interface InventoryHistoryRepository extends JpaRepository<InventoryHistory, Long> {

    // Admin Inventory page ke "History Log" tab ke liye — SAARE stock-
    // adjustments, sabse NAYE pehle. "OrderByCreatedAtDesc" Spring Data
    // JPA ko batata hai: "createdAt field ke hisaab se DESCENDING
    // (naye-se-purane) order mein sort karke do"
    List<InventoryHistory> findAllByOrderByCreatedAtDesc();
}
