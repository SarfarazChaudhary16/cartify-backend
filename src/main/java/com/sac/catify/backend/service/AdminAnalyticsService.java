package com.sac.catify.backend.service;

import java.util.List;
import java.util.Map;

import com.sac.catify.backend.dto.CategorySalesResponse;
import com.sac.catify.backend.dto.DashboardStatsResponse;
import com.sac.catify.backend.dto.OrderResponse;
import com.sac.catify.backend.dto.RevenuePointResponse;
import com.sac.catify.backend.dto.TopProductResponse;

// Admin Dashboard ke SAARE analytics-operations ka contract. Har method
// ek "section" ke liye hai jo frontend AdminDashboardPage.tsx mein tha.
public interface AdminAnalyticsService {

    // Top-4 stat cards (Revenue, Orders, Customers, Products) + unke deltas
    DashboardStatsResponse getDashboardStats();

    // Revenue Area-Chart — last 14 din ka daily revenue
    List<RevenuePointResponse> getRevenueSeries();

    // Orders-by-Status Donut Chart — jaise { "PENDING": 5, "DELIVERED": 20, ... }
    Map<String, Long> getOrdersByStatus();

    // Top Selling Products list (real aggregation se, mock nahi)
    List<TopProductResponse> getTopProducts(int limit);

    // Recent Orders preview — sabse naye N orders
    List<OrderResponse> getRecentOrders(int limit);
    
    // Reports > Sales tab ke "Sales by Category" chart ke liye
    List<CategorySalesResponse> getSalesByCategory();
}
