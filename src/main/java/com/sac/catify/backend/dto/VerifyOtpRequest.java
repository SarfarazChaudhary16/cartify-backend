package com.sac.catify.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Ye DTO DO jagah reuse hoga: (1) Signup ke baad email-verify OTP submit
// karne ke liye, (2) Forgot-password flow mein reset-OTP submit karne ke liye.
// Dono jagah shape same hai (email + 6-digit code), isliye alag-alag DTO
// banane ki zarurat nahi padi — Controller/Service decide karta hai
// "purpose" kya hai apne internal logic se (alag endpoint URLs ke through)
public class VerifyOtpRequest {

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "OTP code is required")
    @Size(min = 6, max = 6, message = "OTP must be exactly 6 digits")
    private String code;

    public VerifyOtpRequest() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
