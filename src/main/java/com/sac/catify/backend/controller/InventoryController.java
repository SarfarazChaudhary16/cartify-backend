package com.sac.catify.backend.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sac.catify.backend.dto.InventoryHistoryResponse;
import com.sac.catify.backend.dto.ProductResponse;
import com.sac.catify.backend.dto.StockAdjustmentRequest;
import com.sac.catify.backend.service.InventoryService;

import java.util.List;

// @RequestMapping("/api/admin/inventory") — "/api/admin/**" prefix ki
// wajah se, SecurityConfig ka pehle se bana rule automatically in saare
// endpoints ko ADMIN-only bana deta hai, koi extra config nahi chahiye
@RestController
@RequestMapping("/api/admin/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // ===================================================================
    // GET /api/admin/inventory/products
    // ===================================================================
    // Kab call hota hai: Admin Inventory page ke "Stock Levels" tab
    // load hote hi — saare products unke current stock ke saath
    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> getAllProductsWithStock() {
    	
        return ResponseEntity.ok(inventoryService.getAllProductsWithStock());
    }

    // ===================================================================
    // POST /api/admin/inventory/{productId}/adjust
    // ===================================================================
    // Kab call hota hai: "Adjust Stock" modal mein Admin quantity/reason
    // bhar ke "Confirm Adjustment" click karta hai
    @PostMapping("/{productId}/adjust")
    public ResponseEntity<ProductResponse> adjustStock(
            @PathVariable Long productId,
            @Valid @RequestBody StockAdjustmentRequest request) {

        ProductResponse response = inventoryService.adjustStock(
                productId, request.getChange(), request.getReason());
        return ResponseEntity.ok(response);
    }

    // ===================================================================
    // GET /api/admin/inventory/history
    // ===================================================================
    // Kab call hota hai: Admin Inventory page ke "History Log" tab
    // load hote hi
    @GetMapping("/history")
    public ResponseEntity<List<InventoryHistoryResponse>> getHistory() {
    	
        return ResponseEntity.ok(inventoryService.getHistory());
    }
}
