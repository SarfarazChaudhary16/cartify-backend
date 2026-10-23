package com.sac.catify.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// @Entity batata hai JPA/Hibernate ko: "is class ko database table samjho aur
// automatically CREATE TABLE kar do (ya existing table se map kar do)".
@Entity
// @Table se hum table ka naam explicitly set kar sakte hain.
// Agar ye na likhein to Hibernate class ka naam hi table name bana deta (User -> user),
// lekin "user" MySQL mein ek reserved/special keyword jaisa treat ho sakta hai isliye
// explicitly "users" naam de rahe hain taaki koi conflict na ho.
@Table(name = "users")
public class User {

    // @Id batata hai ye field is table ki PRIMARY KEY hai (har row ko uniquely identify karta hai).
    @Id
    // @GeneratedValue batata hai ye value database khud generate karega (auto-increment),
    // hume manually ID assign nahi karni. IDENTITY strategy = MySQL ka AUTO_INCREMENT use hoga.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // nullable = false matlab ye column database mein NOT NULL hoga (khaali nahi ja sakta)
    @Column(nullable = false)
    private String name;

    // unique = true matlab do users same email se register nahi kar sakte
    @Column(nullable = false, unique = true)
    private String email;

    // Password hum yahan HASHED (encrypted) store karenge, plain text kabhi nahi.
    // Encryption ka code Security phase mein aayega (BCryptPasswordEncoder).
    @Column(nullable = false)
    private String password;

    private String phone;

    private String avatar; // profile picture ka URL

    // @Enumerated(EnumType.STRING) batata hai: database mein Role ko STRING ki tarah
    // store karo ("CUSTOMER" / "ADMIN"), na ki number (0/1) ki tarah.
    // STRING isliye behtar hai kyunki agar kabhi enum mein naya value add/reorder karein,
    // to number-based storage confuse ho sakta hai, lekin naam-based nahi.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Naya signup hone ke turant baad ye FALSE rehta hai — matlab account
    // ban gaya hai database mein, lekin email verify nahi hui abhi.
    // Jab user OTP verify kar dega, isse TRUE kar denge. Jab tak FALSE hai,
    // login karne nahi denge (frontend ka "verify your email pehle" jaisa flow).
    @Column(nullable = false)
    private boolean enabled = false;
    
    
    // Admin "Block User" button ke liye — agar TRUE hai, to ye user
    // LOGIN nahi kar payega (chahe uska password bilkul sahi ho).
    // "enabled" field se ye ALAG hai: "enabled=false" matlab "email
    // verify nahi hui abhi" (temporary state, khud user verify kar
    // sakta hai OTP se), lekin "blocked=true" matlab "Admin ne
    // JAAN-BOOJH KAR is user ko access se rok diya hai" (sirf Admin
    // hi wapas unblock kar sakta hai).
    @Column(nullable = false)
    private boolean blocked = false;

    // ===== Constructors =====

    // Khaali constructor — JPA/Hibernate ko internally isi ki zarurat padti hai
    // (jab wo database se data laake object banata hai, to pehle empty object banata hai,
    // phir fields fill karta hai). Isliye ye hamesha rakhna hai, chahe use na bhi karein.
    public User() {
    }

    // Ye constructor hum khud use karenge jab naya User banayenge (jaise signup ke time)
    public User(String name, String email, String password, String phone, Role role,boolean enabled) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = role;
        this.createdAt = LocalDateTime.now();
        this.enabled = enabled;
    }

    // ===== Getters and Setters =====
    // Lombok use nahi kar rahe, isliye manually likhne padenge.
    // Ye zaroori hain kyunki fields "private" hain — bahar se access karne ke liye
    // getter/setter chahiye (encapsulation ka basic OOP principle).

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
    
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
}
