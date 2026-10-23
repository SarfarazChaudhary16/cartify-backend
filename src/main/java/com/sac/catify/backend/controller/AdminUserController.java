package com.sac.catify.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sac.catify.backend.dto.AdminUserResponse;
import com.sac.catify.backend.service.AdminUserService;

import java.util.List;

// @RequestMapping("/api/admin/users") — "/api/admin/**" prefix ki wajah
// se SecurityConfig ka existing rule (".hasRole("ADMIN")") in dono
// endpoints ko automatically protect kar deta hai
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    // ===================================================================
    // GET /api/admin/users
    // ===================================================================
    // Kab call hota hai: Admin Users page load hote hi
    @GetMapping
    public ResponseEntity<List<AdminUserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminUserService.getAllUsers());
    }

    // ===================================================================
    // PUT /api/admin/users/{userId}/toggle-block
    // ===================================================================
    // Kab call hota hai: "Block"/"Unblock" button click hone pe
    @PutMapping("/{userId}/toggle-block")
    public ResponseEntity<AdminUserResponse> toggleBlock(@PathVariable Long userId) {
        return ResponseEntity.ok(adminUserService.toggleBlock(userId));
    }
}
