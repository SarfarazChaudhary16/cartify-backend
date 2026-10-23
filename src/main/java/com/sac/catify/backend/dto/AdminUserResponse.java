package com.sac.catify.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Admin Users page (frontend AdminUsersPage.tsx) ke liye — UserResponse
// se ALAG isliye hai kyunki isme 2 EXTRA "computed" fields hain
// (ordersCount, totalSpent) jo User Entity mein STORE nahi hote — ye
// Service layer mein Orders table se REAL-TIME calculate honge, har
// baar list load karte waqt (jaisa Category ke "productCount" ke saath
// bhi kiya tha, yaad hai?).
public class AdminUserResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private LocalDateTime joinedAt; // Entity ka "createdAt" hi hai, naam frontend-match ke liye "joinedAt" rakha
    private long ordersCount;
    private BigDecimal totalSpent;
    private boolean blocked;

    public AdminUserResponse() {
    }

    public AdminUserResponse(Long id, String name, String email, String phone, LocalDateTime joinedAt,
                              long ordersCount, BigDecimal totalSpent, boolean blocked) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.joinedAt = joinedAt;
        this.ordersCount = ordersCount;
        this.totalSpent = totalSpent;
        this.blocked = blocked;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    public long getOrdersCount() {
        return ordersCount;
    }

    public void setOrdersCount(long ordersCount) {
        this.ordersCount = ordersCount;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
}
