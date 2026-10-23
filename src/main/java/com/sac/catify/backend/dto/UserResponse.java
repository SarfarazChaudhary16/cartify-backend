package com.sac.catify.backend.dto;

//Kyun bana: Ye wo shape hai jo hum User se related har response mein bhejenge — password kabhi nahi. 
//Frontend ke User TypeScript interface se match karta hai.

import java.time.LocalDateTime;

import com.sac.catify.backend.entity.Role;

// Ye "safe" version hai User entity ka — jab bhi hume frontend ko user ka data
// bhejna ho (login response, profile page, waghera), hum User entity seedha nahi bhejenge,
// balki ise is DTO mein convert karke bhejenge. Isse password field kabhi frontend
// tak nahi pahunchega, chahe wo hashed hi kyun na ho.
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String avatar;
    private Role role;
    private LocalDateTime createdAt;

    public UserResponse() {
    }

    public UserResponse(Long id, String name, String email, String phone, String avatar,
                         Role role, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.avatar = avatar;
        this.role = role;
        this.createdAt = createdAt;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
