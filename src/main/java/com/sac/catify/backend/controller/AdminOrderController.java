package com.sac.catify.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sac.catify.backend.dto.OrderResponse;
import com.sac.catify.backend.service.OrderService;

import java.util.List;

// Admin-facing endpoints — "/api/admin/orders" base URL. Inka access-control
// (sirf ADMIN role) SecurityConfig mein set hai.
@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // GET /api/admin/orders
    // Admin Orders page — SAARE users ke SAARE orders
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
    	
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // GET /api/admin/orders/{orderId}
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long orderId) {
    	
        return ResponseEntity.ok(orderService.getOrderByIdForAdmin(orderId));
    }

    // PUT /api/admin/orders/{orderId}/advance-status
    // "Mark as Packed/Shipped/..." button
    @PutMapping("/{orderId}/advance-status")
    public ResponseEntity<OrderResponse> advanceStatus(@PathVariable Long orderId) {
    	
        return ResponseEntity.ok(orderService.advanceStatus(orderId));
    }

    // PUT /api/admin/orders/{orderId}/refund
    // "Process Refund" button
    @PutMapping("/{orderId}/refund")
    public ResponseEntity<OrderResponse> processRefund(@PathVariable Long orderId) {
    	
        return ResponseEntity.ok(orderService.processRefund(orderId));
    }
}
