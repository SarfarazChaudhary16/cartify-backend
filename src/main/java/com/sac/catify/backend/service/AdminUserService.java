package com.sac.catify.backend.service;

import java.util.List;

import com.sac.catify.backend.dto.AdminUserResponse;

public interface AdminUserService {

    // Admin Users page — SAARE customers (role=CUSTOMER wale users),
    // orders-count/total-spent ke saath
    List<AdminUserResponse> getAllUsers();

    // "Block"/"Unblock" button — current blocked-status ko ULTA kar deta hai
    AdminUserResponse toggleBlock(Long userId);
}
