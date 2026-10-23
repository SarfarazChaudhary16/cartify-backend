package com.sac.catify.backend.service;

import java.util.List;

import com.sac.catify.backend.dto.CheckoutRequest;
import com.sac.catify.backend.dto.OrderResponse;

// Order se related SAARE operations ka contract — customer-side
// (checkout, order-history dekhna) aur admin-side (status advance, refund,
// saare orders dekhna) dono isi ek interface mein hain, kyunki dono
// ultimately "Order" entity pe hi kaam karte hain.
public interface OrderService {

    // ===== CUSTOMER OPERATIONS =====

    // "Place Order" button — user ke current cart se ek Order banata hai,
    // cart clear karta hai, aur bana hua Order wapas deta hai
    OrderResponse placeOrder(Long userId, CheckoutRequest request);

    // Account > Orders page — logged-in user ke SAARE orders (naye-se-purane)
    List<OrderResponse> getOrdersForUser(Long userId);

    // Order Detail page — specific order dekhna, LEKIN sirf agar wo
    // ORDER isi user ka hai (security check andar hoga, jaise Address mein tha)
    OrderResponse getOrderForUser(Long userId, Long orderId);

    // "Cancel Order" button — order ko CANCELLED status mein daalta hai,
    // reason ke saath tracking-step add karta hai
    OrderResponse cancelOrder(Long userId, Long orderId, String reason);

    // "Return / Replace" button — order ko RETURNED status mein daalta hai
    OrderResponse requestReturnOrReplace(Long userId, Long orderId, String type, String reason);

    // ===== ADMIN OPERATIONS =====

    // Admin > Orders page — SAARE users ke SAARE orders (koi userId filter
    // nahi, admin ko sab dikhna chahiye)
    List<OrderResponse> getAllOrders();

    // Admin Order Detail page — koi bhi order dekhna, ownership-check
    // ki zarurat NAHI (admin sabka dekh sakta hai)
    OrderResponse getOrderByIdForAdmin(Long orderId);

    // Admin ka "Mark as Packed/Shipped/..." button — status ko agle
    // step pe le jata hai (PENDING->PACKED->SHIPPED->...)
    OrderResponse advanceStatus(Long orderId);

    // Admin ka "Process Refund" button — CANCELLED/RETURNED order ko
    // REFUNDED mark karta hai
    OrderResponse processRefund(Long orderId);
}
