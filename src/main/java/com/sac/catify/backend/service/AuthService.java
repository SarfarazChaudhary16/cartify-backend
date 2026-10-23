package com.sac.catify.backend.service;

import com.sac.catify.backend.dto.*;

// Authentication se related SAARE operations ka contract — register,
// login, OTP verify (dono purposes ke liye), forgot/reset password.
// Frontend ke useAuthStore.ts mein jo bhi actions the (login, signup,
// verifyOtp, requestPasswordReset, verifyPasswordResetOtp, resetPassword,
// resendOtp) — un sabka backend-equivalent yahi interface hai.
public interface AuthService {

    // Signup form submit hone pe: naya (disabled) User banata hai,
    // OTP generate + email bhejta hai. LOGIN NAHI karwata abhi (enabled=false hai)
    MessageResponse register(RegisterRequest request);

    // Signup OTP verify karne pe: User ko enabled=true karta hai,
    // aur TURANT login karwa deta hai (JWT token ke saath) — jaisa
    // frontend ke verifyOtp() mein tha ("Verified! Welcome to Cartify")
    LoginResponse verifySignupOtp(VerifyOtpRequest request);

    // Normal login — email+password se JWT milta hai. Agar account
    // abhi tak verify (enabled=true) nahi hua, to error dega.
    LoginResponse login(LoginRequest request);

    // Forgot Password Step 1: email se OTP bhejta hai (purpose=PASSWORD_RESET)
    MessageResponse forgotPassword(ForgotPasswordRequest request);

    // Forgot Password Step 2: OTP verify karta hai, "used" mark karta hai.
    // Ye SIRF verify karta hai, password change nahi karta abhi — taaki
    // frontend flow match ho (OTP verify -> reset-password page pe redirect
    // -> tab jaake naya password submit hota hai, alag endpoint se)
    MessageResponse verifyResetOtp(VerifyOtpRequest request);

    // Forgot Password Step 3 (FINAL): naya password set karta hai.
    // Check karta hai ki is email ke liye RECENTLY ek verified/used
    // PASSWORD_RESET OTP exist karta hai (security ke liye — taaki koi
    // bina OTP verify kiye seedha password change na kar sake)
    MessageResponse resetPassword(ResetPasswordRequest request);

    // "Resend Code" button ke liye — naya OTP generate karke bhejta hai,
    // purana wala effectively "stale" ho jata hai (hum use ignore kar
    // denge kyunki hum hamesha LATEST OTP dhoondhte hain verify karte waqt)
    MessageResponse resendOtp(String email, String purpose);
}
