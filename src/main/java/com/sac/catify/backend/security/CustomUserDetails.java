package com.sac.catify.backend.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.sac.catify.backend.entity.User;

import java.util.Collection;
import java.util.List;

// Ye Spring Security ke built-in "User" class ki jagah use hoga. Fayda ye
// hai ki isme humara POORA User entity (id samet) wrapped hai — isliye
// @AuthenticationPrincipal se seedha userId, poora User object, sab kuch
// mil jayega, Controllers mein dobara "email se User dhoondho" wali
// extra database query nahi karni padegi (jo pehle getCurrentUserId()
// helper mein har baar hoti thi).
public class CustomUserDetails implements UserDetails {

    // Poora User entity yahan store kar rahe hain — isse getId(), getEmail(),
    // getName() waghera SEEDHA is class se access ho sakenge
    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    // Convenience method — Controllers mein isi ko use karenge
    public Long getId() {
        return user.getId();
    }

    // Agar kabhi poora User object hi chahiye ho (bas ID nahi)
    public User getUser() {
        return user;
    }

    // ===== UserDetails interface ke MANDATORY methods (implement karne padte hain) =====

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Role ko "ROLE_" prefix ke saath dete hain (Spring Security convention,
        // CustomUserDetailsService mein pehle jo logic tha, wahi yahan hai)
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        // "Username" ki jagah hum email use kar rahe hain (jaisa pehle bhi tha)
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // abhi account-expiry ka feature nahi hai, hamesha true
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // abhi account-locking ka feature nahi hai, hamesha true
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // abhi password-expiry ka feature nahi hai, hamesha true
    }

    @Override
    public boolean isEnabled() {
        // Yehi wo jagah hai jahan humara "enabled" field (OTP-verify wala)
        // asal mein kaam karta hai — agar false hai, Spring Security khud
        // is user ko authenticate hone se rok dega
        return user.isEnabled();
    }
}
