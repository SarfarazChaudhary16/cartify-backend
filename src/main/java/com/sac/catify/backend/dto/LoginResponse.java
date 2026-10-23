package com.sac.catify.backend.dto;

//Login (aur OTP-verify ke baad auto-login) successful hone pe ye response
//jata hai frontend ko. Do cheezein hoti hain isme:
//1. token -> JWT string, jise frontend har agli request ke saath
//   "Authorization: Bearer <token>" header mein bhejega
//2. user -> UserResponse (safe DTO, password nahi hai isme, Phase B4 mein
//   banaya tha) taaki frontend turant user.role check karke decide kar sake
//   "customer hai to /account bhejo, admin hai to /admin bhejo" — bilkul
//   jaisa frontend ke LoginPage.tsx mein already logic likha hua hai
public class LoginResponse {

 private String token;
 private UserResponse user;

 public LoginResponse() {
 }

 public LoginResponse(String token, UserResponse user) {
     this.token = token;
     this.user = user;
 }

 public String getToken() {
     return token;
 }

 public void setToken(String token) {
     this.token = token;
 }

 public UserResponse getUser() {
     return user;
 }

 public void setUser(UserResponse user) {
     this.user = user;
 }
}
