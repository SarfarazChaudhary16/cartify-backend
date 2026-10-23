package com.sac.catify.backend.service.impl;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.sac.catify.backend.dto.PaymentVerificationRequest;
import com.sac.catify.backend.dto.RazorpayOrderResponse;
import com.sac.catify.backend.entity.Order;
import com.sac.catify.backend.exception.ApiException;
import com.sac.catify.backend.repository.OrderRepository;
import com.sac.catify.backend.service.PaymentService;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;

    // application.properties se Razorpay ki keys lete hain
    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    public PaymentServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // ===================================================================
    // STEP 1: Razorpay Order Create karna
    // ===================================================================
    @Override
    public RazorpayOrderResponse createRazorpayOrder(Long internalOrderId) {

        // Humara khud ka Order dhoondhte hain (jo placeOrder() se pehle
        // hi PENDING status mein ban chuka hoga — Razorpay integration
        // humare Order-creation flow ke UPAR, ek EXTRA payment-step ki
        // tarah kaam karta hai)
        Order order = orderRepository.findById(internalOrderId)
                .orElseThrow(() -> ApiException.notFound("Order not found with id: " + internalOrderId));

        try {
            // RazorpayClient ye SDK ka main object hai — isi se Razorpay
            // ke API ko calls jaati hain (jaise Order create karna)
            RazorpayClient razorpayClient = new RazorpayClient(keyId, keySecret);

            // Razorpay HAMESHA amount "paise" mein maangta hai, RUPEES
            // mein nahi (jaise ₹100 ke liye 10000 bhejna hota hai — isliye
            // hum apna BigDecimal total ko 100 se multiply karke, phir
            // .longValue() se whole-number (paise) mein convert karte hain)
            long amountInPaise = order.getTotal()
                    .multiply(BigDecimal.valueOf(100))
                    .longValue();

            // JSONObject bana ke Razorpay ko batate hain "kitne amount
            // ka, kis currency mein, order banao"
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            // "receipt" ek humara khud ka reference-string hai (Razorpay
            // dashboard mein dikhta hai, humein internally kaam nahi aata,
            // bas traceability ke liye humara orderNumber bhej rahe hain)
            orderRequest.put("receipt", order.getOrderNumber());

            // Razorpay ko actual API call — ye ek "com.razorpay.Order"
            // object return karta hai (Razorpay SDK ka apna Order class,
            // HAMARE Order Entity se ALAG hai — naam clash horaha hai
            // isliye humne import mein apna "Order" hi rakha hai, aur
            // Razorpay ka wala neeche "razorpayOrder" variable mein store
            // ho raha hai bina explicit type declare kiye .toString() use karke)
            com.razorpay.Order razorpayOrder = razorpayClient.orders.create(orderRequest);

            String razorpayOrderId = razorpayOrder.get("id");

            // Humare database ke Order mein ye razorpayOrderId save kar dete
            // hain, taaki baad mein verify karte waqt match kar sakein
            order.setRazorpayOrderId(razorpayOrderId);
            order.setPaymentStatus("PENDING");
            orderRepository.save(order);

            return new RazorpayOrderResponse(razorpayOrderId, keyId, amountInPaise, "INR", order.getId());

        } catch (RazorpayException e) {
            // RazorpayException ek "checked exception" hai (jaise
            // MessagingException tha EmailService mein) — hume isse
            // apni ApiException mein wrap karna padta hai
            throw new ApiException("Failed to create Razorpay order: " + e.getMessage(),
                    org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    // ===================================================================
    // STEP 2: Payment Verify karna — SABSE IMPORTANT SECURITY STEP
    // ===================================================================
    @Override
    public void verifyPayment(PaymentVerificationRequest request) {

        Order order = orderRepository.findById(request.getInternalOrderId())
                .orElseThrow(() -> ApiException.notFound("Order not found with id: " + request.getInternalOrderId()));

        try {
            // ===== SIGNATURE VERIFICATION — YE SABSE CRITICAL STEP HAI =====
            // Razorpay se aayi 3 cheezein (order_id, payment_id, signature)
            // ek Map mein daalte hain — Razorpay ka Utils.verifyPaymentSignature()
            // helper isi format mein expect karta hai
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", request.getRazorpayOrderId());
            attributes.put("razorpay_payment_id", request.getRazorpayPaymentId());
            attributes.put("razorpay_signature", request.getRazorpaySignature());

            // Utils.verifyPaymentSignature() KHUD ANDAR ye karta hai:
            // 1. razorpay_order_id + "|" + razorpay_payment_id ko jodta hai
            // 2. Hamari "keySecret" (jo SIRF humare server ko pata hai,
            //    kabhi frontend ko nahi jaati) se ek HMAC-SHA256 signature
            //    banata hai
            // 3. Ye khud-generated signature, Razorpay se aayi
            //    "razorpay_signature" se MATCH karta hai
            //
            // Agar match ho gaya -> matlab payment GENUINE hai, Razorpay
            // se hi aayi hai (koi FAKE request nahi bhej sakta, kyunki
            // usko hamari secret-key pata nahi hai signature generate
            // karne ke liye)
            boolean isValid = Utils.verifyPaymentSignature(attributes, keySecret);

            if (!isValid) {
                // Signature match NAHI hua — ya to koi tamper karne ki
                // koshish kar raha hai, ya kuch data corrupt ho gaya
                order.setPaymentStatus("FAILED");
                orderRepository.save(order);
                throw ApiException.badRequest("Payment verification failed. Signature mismatch.");
            }

            // Signature genuine nikla — payment CONFIRM ho gaya
            order.setPaymentStatus("PAID");
            order.setRazorpayPaymentId(request.getRazorpayPaymentId());
            orderRepository.save(order);

        } catch (RazorpayException e) {
            throw new ApiException("Payment verification error: " + e.getMessage(),
                    org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }
}
