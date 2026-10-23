package com.sac.catify.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sac.catify.backend.dto.ForgotPasswordRequest;
import com.sac.catify.backend.dto.LoginRequest;
import com.sac.catify.backend.dto.LoginResponse;
import com.sac.catify.backend.dto.MessageResponse;
import com.sac.catify.backend.dto.RegisterRequest;
import com.sac.catify.backend.dto.ResetPasswordRequest;
import com.sac.catify.backend.dto.VerifyOtpRequest;
import com.sac.catify.backend.service.AuthService;

import jakarta.validation.Valid;

//Saare authentication-related endpoints yahan hain — "/api/auth" base URL
//ke saath. Ye endpoints PUBLIC hone chahiye (bina login ke accessible),
//kyunki agar login/signup khud protected ho jaye, to koi login kar hi
//nahi payega! (Ye rule hum SecurityConfig mein Phase ke last mein set karenge)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    // POST /api/auth/register
    // Frontend SignupPage.tsx form submit hone pe
    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(
    		
    		@Valid @RequestBody RegisterRequest request) {
    	
    	MessageResponse response = authService.register(request);
    	
    	return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    // POST /api/auth/verify-signup-otp
    // Frontend VerifyOtpPage.tsx — signup ke baad email verify karna
    @PostMapping("/verify-signup-otp")
    public ResponseEntity<LoginResponse> verifySignupOtp(
    		
    		@Valid @RequestBody VerifyOtpRequest request) {
    	
        return ResponseEntity.ok(authService.verifySignupOtp(request));
    }
    
    // POST /api/auth/login
    // Frontend LoginPage.tsx — unified customer/admin login
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
    		
    		@Valid @RequestBody LoginRequest request) {
    	
        return ResponseEntity.ok(authService.login(request));
    }
    
    // POST /api/auth/forgot-password
    // Frontend ForgotPasswordPage.tsx Step 1 — email daal ke OTP mangna
    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(
    		
    		@Valid @RequestBody ForgotPasswordRequest request) {
    
        return ResponseEntity.ok(authService.forgotPassword(request));
    }
    
    // POST /api/auth/verify-reset-otp
    // Frontend ForgotPasswordPage.tsx Step 2 — OTP verify karna
    @PostMapping("/verify-reset-otp")
    public ResponseEntity<MessageResponse> verifyResetOtp(
    		
    		@Valid @RequestBody VerifyOtpRequest request) {
    	
        return ResponseEntity.ok(authService.verifyResetOtp(request));
    }
    
    // POST /api/auth/reset-password
    // Frontend ResetPasswordPage.tsx — naya password set karna (FINAL step)
    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(
    		
    		@Valid @RequestBody ResetPasswordRequest request) {
    
        return ResponseEntity.ok(authService.resetPassword(request));
    }
    
    // POST /api/auth/resend-otp?email=...&purpose=...
    // "Resend Code" button ke liye — dono OTP flows (signup + forgot-password)
    // ke liye reuse hota hai, "purpose" query param se decide hota hai kaunsa
    @PostMapping("/resend-otp")
    public ResponseEntity<MessageResponse> resendOtp(
            @RequestParam String email,
            @RequestParam String purpose) {
        return ResponseEntity.ok(authService.resendOtp(email, purpose));
    }
}

//FLOW
//════════════════════════════════════════════════════════════════
//PART 1: SIGNUP + EMAIL VERIFICATION
//════════════════════════════════════════════════════════════════
//
//Frontend (SignupPage)
// │ POST /api/auth/register {name, email, phone, password}
// ▼
//AuthController.register()
// ▼
//AuthServiceImpl.register()
// ├─► existsByEmail() check
// ├─► passwordEncoder.encode(password)  → HASH ban jata hai
// ├─► User save (enabled=false)
// └─► generateAndSendOtp()
//        ├─► 6-digit code generate (SecureRandom)
//        ├─► OtpToken save (expiresAt = now+10min)
//        └─► emailService.sendOtpEmail()
//               └─► Gmail SMTP se premium HTML email jati hai user ko
//
// ◄── Response: "Account created — check your email"
//
//
//User apna Gmail check karta hai, 6-digit code dekhta hai
// │
// ▼
//Frontend (VerifyOtpPage) — code enter karta hai
// │ POST /api/auth/verify-signup-otp {email, code}
// ▼
//AuthServiceImpl.verifySignupOtp()
// ├─► validateOtp() → match+expiry+used check, OtpToken.used=true mark
// ├─► User.enabled = true, save
// └─► jwtUtil.generateToken(email, role) → JWT ban gaya!
//
// ◄── Response: { token: "eyJhbGc...", user: {...} }
//
//Frontend token ko save karta hai → user AUTO-LOGGED-IN ho jata hai
//
//
//════════════════════════════════════════════════════════════════
//PART 2: NORMAL LOGIN (baad mein kabhi bhi)
//════════════════════════════════════════════════════════════════
//
//Frontend (LoginPage)
// │ POST /api/auth/login {email, password}
// ▼
//AuthServiceImpl.login()
// ├─► findByEmail() → User mila
// ├─► passwordEncoder.matches(rawPassword, user.getPassword())
// │        → BCrypt andar hi andar verify karta hai, true/false
// ├─► user.isEnabled() check (verified hai ya nahi)
// └─► jwtUtil.generateToken(email, role)
//
// ◄── Response: { token: "...", user: { role: "CUSTOMER" / "ADMIN" } }
//
//Frontend: result.role check karke /account ya /admin pe navigate karta hai
//(bilkul jaisa LoginPage.tsx mein already likha hua tha)
