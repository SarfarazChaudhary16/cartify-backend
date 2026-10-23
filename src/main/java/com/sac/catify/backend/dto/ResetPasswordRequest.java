package com.sac.catify.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Forgot Password flow ka FINAL step — email (query se aata hai frontend
// mein, ResetPasswordPage.tsx yaad karo: "?email=..." URL param se) + naya
// password. Dhyan do: is DTO mein OTP CODE nahi hai — kyunki humari design
// mein "verify-reset-otp" endpoint PEHLE hi call ho chuka hota hai (jaha
// OTP verify hoke "used=true" mark ho jata hai), is step mein sirf naya
// password set karna hai. Service layer check karega ki "kya isi email ke
// liye recently ek verified/used PASSWORD_RESET OTP exist karta hai" —
// taaki koi bina OTP verify kiye seedha ye endpoint na hit kar sake.
public class ResetPasswordRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    private String email;

    @NotBlank(message = "New password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String newPassword;

    public ResetPasswordRequest() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
