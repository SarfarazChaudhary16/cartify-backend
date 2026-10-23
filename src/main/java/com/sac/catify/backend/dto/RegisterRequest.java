package com.sac.catify.backend.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

//Kyun bana: Jab frontend /api/auth/register pe POST request bhejega (signup form se), 
//uska JSON body is shape mein aayega. Ye class us incoming data ko represent karti hai.

// Ye class signup form se aane wale data ko represent karti hai.
// Frontend ke SignupPage.tsx form fields (name, email, phone, password) ka exact match.
// Validation annotations (@NotBlank, @Email, @Size) Controller mein @Valid ke saath
// use honge — agar galat data aaya (jaise khaali naam, ya invalid email), Spring
// khud automatic 400 Bad Request error de dega, hume manually if-else check nahi likhna padega.
public class RegisterRequest {

    // @NotBlank -> field khaali ya sirf spaces wala nahi hona chahiye
    @NotBlank(message = "Name is required")
    private String name;

    // @Email -> Spring khud check karega ki valid email format hai ya nahi
    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phone;

    // @Size -> password kam se kam 6 characters ka hona chahiye
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    // Khaali constructor zaroori hai — Spring jab JSON ko is class mein convert
    // karta hai (deserialize), to pehle empty object banata hai, phir setters se fields bharta hai.
    public RegisterRequest() {
    }

    public RegisterRequest(String name, String email, String phone, String password) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
