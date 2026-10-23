package com.sac.catify.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ye human-readable order number hai, jaise "CF-10293" — customer ko yehi dikhta hai,
    // internal database ID (Long id) kabhi frontend ko nahi dikhate
    @Column(name = "order_number", nullable = false, unique = true)
    private String orderNumber;

    // ===== Kis User ne ye order place kiya (Many Orders belong to One User) =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // @Embedded (Embeddable field ko Entity ke andar use karne ke liye ye annotation lagta hai)
    // Ye order-place-hote-waqt-ka address snapshot hai — upar explain kiya
    @Embedded
    private OrderAddress address;

    // Frontend mein payment method string tha ("upi", "card", "cod" waghera) —
    // hum ise simple String hi rakh rahe hain (enum nahi) kyunki Razorpay integration
    // (Phase B7) mein iske exact values Razorpay ke response format se aayenge,
    // to abhi flexible rakhna better hai.
    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;
    
    
    
//    Kyun ye fields chahiye: Jab Order banta hai (abhi humara flow "place order → turant confirm" hai), 
//    lekin real payment ke saath flow hoga: "Razorpay order create karo → PENDING order banao → "
//    + "user payment kare → verify karo → tabhi order CONFIRMED maano". Isliye Order ko Razorpay		
//    ke apne order_id aur payment_id yaad rakhne honge — future mein agar refund/dispute check karna 
//    ho to yehi IDs kaam aayengi.
    
    // Razorpay khud ka ek "order_id" banata hai (jaise "order_9A33XWu170gUtm").
    // Ye humare apne orderNumber (jaise "CF-48213") se ALAG hai — Razorpay
    // ka ID sirf PAYMENT-tracking ke liye hai, humara orderNumber
    // customer-facing display ke liye hai.
    @Column(name = "razorpay_order_id")
    private String razorpayOrderId;

    // Payment successful hone ke baad Razorpay jo "payment_id" deta hai
    // (jaise "pay_29QQoUBi66xm2f") — refund process karte waqt ye ID chahiye hoti hai
    @Column(name = "razorpay_payment_id")
    private String razorpayPaymentId;

    // Payment ka status track karne ke liye — "PENDING" (abhi payment
    // hui hi nahi/COD hai), "PAID" (successful), "FAILED" (fail ho gayi)
    @Column(name = "payment_status")
    private String paymentStatus;
    
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;
    
    // NAYA: Agar checkout ke waqt koi coupon apply hua tha, uska CODE
    // aur DISCOUNT-AMOUNT yahan bhi snapshot ki tarah save karte hain —
    // taaki Order Details page pe "Coupon WELCOME200 applied (-₹200)"
    // jaisa dikha sakein, chahe coupon baad mein delete/expire ho jaye
    @Column(name = "coupon_code")
    private String couponCode;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "placed_at", nullable = false)
    private LocalDateTime placedAt;

    // ===== One Order has Many Tracking Steps (Embeddable list) =====
    @ElementCollection
    @CollectionTable(name = "order_tracking_steps", joinColumns = @JoinColumn(name = "order_id"))
    private List<TrackingStep> trackingSteps = new ArrayList<>();

    // ===== One Order has Many Items =====
    // OrderItem entity agli batch mein banayenge, isliye ye reference abhi thoda "forward-looking" hai
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public Order() {
    }

    public Order(String orderNumber, User user, OrderAddress address, String paymentMethod,
                 OrderStatus status, BigDecimal total) {
        this.orderNumber = orderNumber;
        this.user = user;
        this.address = address;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.total = total;
        this.placedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public OrderAddress getAddress() {
        return address;
    }

    public void setAddress(OrderAddress address) {
        this.address = address;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public LocalDateTime getPlacedAt() {
        return placedAt;
    }

    public void setPlacedAt(LocalDateTime placedAt) {
        this.placedAt = placedAt;
    }

    public List<TrackingStep> getTrackingSteps() {
        return trackingSteps;
    }

    public void setTrackingSteps(List<TrackingStep> trackingSteps) {
        this.trackingSteps = trackingSteps;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
    
    public String getRazorpayOrderId() {
        return razorpayOrderId;
    }

    public void setRazorpayOrderId(String razorpayOrderId) {
        this.razorpayOrderId = razorpayOrderId;
    }

    public String getRazorpayPaymentId() {
        return razorpayPaymentId;
    }

    public void setRazorpayPaymentId(String razorpayPaymentId) {
        this.razorpayPaymentId = razorpayPaymentId;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
}
