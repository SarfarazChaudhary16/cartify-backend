package com.sac.catify.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sac.catify.backend.dto.CategorySalesResponse;
import com.sac.catify.backend.dto.DashboardStatsResponse;
import com.sac.catify.backend.dto.OrderResponse;
import com.sac.catify.backend.dto.RevenuePointResponse;
import com.sac.catify.backend.dto.TopProductResponse;
import com.sac.catify.backend.service.AdminAnalyticsService;

import java.util.List;
import java.util.Map;

// @RequestMapping("/api/admin/analytics") — is base-URL ki wajah se,
// SecurityConfig ka pehle se maujood rule (".requestMatchers("/api/admin/**")
// .hasRole("ADMIN")") in saare endpoints ko AUTOMATICALLY protect kar
// dega — humein koi NAYA SecurityConfig-rule add karne ki zarurat nahi padi!
@RestController
@RequestMapping("/api/admin/analytics")
public class AdminAnalyticsController {

    private final AdminAnalyticsService adminAnalyticsService;

    public AdminAnalyticsController(AdminAnalyticsService adminAnalyticsService) {
        this.adminAnalyticsService = adminAnalyticsService;
    }

    // ===================================================================
    // GET /api/admin/analytics/dashboard-stats
    // ===================================================================
    // Kab call hota hai: Admin Dashboard page load hote hi, sabse pehle
    // (upar ke 4 stat-cards render karne ke liye)
    @GetMapping("/dashboard-stats")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats() {
        return ResponseEntity.ok(adminAnalyticsService.getDashboardStats());
    }

    // ===================================================================
    // GET /api/admin/analytics/revenue-series
    // ===================================================================
    // Kab call hota hai: Revenue Area-Chart render karne ke liye
    @GetMapping("/revenue-series")
    public ResponseEntity<List<RevenuePointResponse>> getRevenueSeries() {
        return ResponseEntity.ok(adminAnalyticsService.getRevenueSeries());
    }

    // ===================================================================
    // GET /api/admin/analytics/orders-by-status
    // ===================================================================
    // Kab call hota hai: Orders-by-Status Donut-Chart render karne ke liye
    @GetMapping("/orders-by-status")
    public ResponseEntity<Map<String, Long>> getOrdersByStatus() {
        return ResponseEntity.ok(adminAnalyticsService.getOrdersByStatus());
    }

    // ===================================================================
    // GET /api/admin/analytics/top-products?limit=5
    // ===================================================================
    // Kab call hota hai: "Top Selling Products" section render karne ke liye
    //
    // @RequestParam(defaultValue = "5") -> agar frontend "?limit=" bilkul
    // na bheje, ye automatically 5 maan lega (koi error nahi aayega
    // missing-parameter ki wajah se)
    @GetMapping("/top-products")
    public ResponseEntity<List<TopProductResponse>> getTopProducts(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(adminAnalyticsService.getTopProducts(limit));
    }

    // ===================================================================
    // GET /api/admin/analytics/recent-orders?limit=5
    // ===================================================================
    // Kab call hota hai: "Recent Orders" section render karne ke liye
    @GetMapping("/recent-orders")
    public ResponseEntity<List<OrderResponse>> getRecentOrders(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(adminAnalyticsService.getRecentOrders(limit));
    }
    
    // GET /api/admin/analytics/sales-by-category
    @GetMapping("/sales-by-category")
    public ResponseEntity<List<CategorySalesResponse>> getSalesByCategory() {
        return ResponseEntity.ok(adminAnalyticsService.getSalesByCategory());
    }
}
