package com.sac.catify.backend.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.sac.catify.backend.dto.CheckoutRequest;
import com.sac.catify.backend.dto.OrderCancelRequest;
import com.sac.catify.backend.dto.OrderResponse;
import com.sac.catify.backend.dto.OrderReturnRequest;
import com.sac.catify.backend.security.CustomUserDetails;
import com.sac.catify.backend.service.OrderService;

import java.util.List;

// Ye customer-facing endpoints hain — "/api/orders" base URL. Har method
// mein @AuthenticationPrincipal CustomUserDetails currentUser hai, taaki
// hum HAMESHA "current logged-in user KA HI" data operate karein (koi
// user kisi aur ka order nahi dekh/cancel kar sakta — Service layer mein
// getOwnedOrder() ye enforce karta hai)
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // POST /api/orders
    // "Place Order" button (Checkout ka final step)
    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(
    		
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody CheckoutRequest request) {
    	
        OrderResponse response = orderService.placeOrder(currentUser.getId(), request);
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // GET /api/orders
    // Account > Orders page — apna poora order-history
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getMyOrders(
    		
            @AuthenticationPrincipal CustomUserDetails currentUser) {
    	
        return ResponseEntity.ok(orderService.getOrdersForUser(currentUser.getId()));
    }

    // GET /api/orders/{orderId}
    // Order Detail page
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(
    		
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long orderId) {
    	
        return ResponseEntity.ok(orderService.getOrderForUser(currentUser.getId(), orderId));
    }

    // PUT /api/orders/{orderId}/cancel
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
    		
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long orderId,
            @Valid @RequestBody OrderCancelRequest request) {
    	
        return ResponseEntity.ok(orderService.cancelOrder(currentUser.getId(), orderId, request.getReason()));
    }

    // PUT /api/orders/{orderId}/return
    @PutMapping("/{orderId}/return")
    public ResponseEntity<OrderResponse> requestReturn(
    		
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long orderId,
            @Valid @RequestBody OrderReturnRequest request) {
    	
        return ResponseEntity.ok(orderService.requestReturnOrReplace(
                currentUser.getId(), orderId, request.getType(), request.getReason()));
    }
}

//FLOW
/*
Frontend (CheckoutPage) → "Place Order" click
│
│  POST /api/orders
│  Header: Authorization: Bearer <token>
│  Body: { addressId: 2, paymentMethod: "cod" }
▼
OrderController.placeOrder(currentUser, request)
▼
OrderServiceImpl.placeOrder(userId=7, request)
│
├─► cartItemRepository.findByUserId(7)
│        → [ {product: Nike Shoes, qty: 2, price: 7999}, {product: T-Shirt, qty: 1, price: 999} ]
│        → Agar KHAALI hai → "Cart is empty" error
│
├─► addressRepository.findById(2) → Address mila, ownership verify (userId match)
│
├─► Stock Check LOOP:
│        Nike Shoes: stock=25, quantity-wanted=2 → OK
│        T-Shirt: stock=0, quantity-wanted=1 → ERROR! "Insufficient stock"
│        (agar yahan fail ho jaye, POORA order fail hota hai, kuch save nahi hota)
│
├─► Total calculate: (7999*2) + (999*1) = 16997
│
├─► OrderAddress SNAPSHOT banaya (Address entity se copy, link nahi)
│
├─► new Order("CF-48213", user, orderAddress, "cod", PENDING, 16997)
│
├─► Har CartItem → OrderItem (title/image/price SNAPSHOT copy)
│        └─► Product.stock -= quantity (Nike: 25→23, T-Shirt: agar OK hota)
│
├─► Tracking Steps banaye: [PENDING(✓), PACKED(), SHIPPED(), DELIVERED()]
│
├─► orderRepository.save(order)
│        → cascade=ALL ki wajah se Order + saare OrderItems ek saath
│          INSERT ho jate hain (jaisa Product/Brand mein bhi tha)
│
└─► cartItemRepository.deleteByUserId(7)  → cart khaali ho gaya
│
▼
mapToResponse() → poora Order (items+tracking+address ke saath) DTO mein
▼
Response: 201 Created, poora order object frontend ko milta hai
▼
Frontend: CheckoutSuccessPage pe navigate karta hai order-confirmation dikhane
*/