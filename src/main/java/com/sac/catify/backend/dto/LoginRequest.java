package com.sac.catify.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// Login form (frontend LoginPage.tsx) se aane wala data — sirf email + password.
// frontend mein "unified login" banaya tha (customer aur admin dono
// isi form se login karte hain)? Backend mein bhi yahi concept hai — koi
// alag "AdminLoginRequest" nahi hai, role check hum LOGIC mein karte hain
// (database mein us email ka role kya hai), URL/DTO level pe nahi.
public class LoginRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    
    public LoginRequest() {
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
}
