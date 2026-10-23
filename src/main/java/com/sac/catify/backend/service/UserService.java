package com.sac.catify.backend.service;


import com.sac.catify.backend.dto.ChangePasswordRequest; 
import com.sac.catify.backend.dto.UpdateProfileRequest;
import com.sac.catify.backend.dto.UserResponse;
import org.springframework.web.multipart.MultipartFile;

//User se related read-only operations ka contract. NOTE: Signup/Register
//ka kaam ab AuthService.register() karta hai (jisme OTP + email verification
//bhi integrated hai) — isliye yahan se registerUser() method HATA diya gaya,
//warna do jagah duplicate/conflicting signup-logic ban jati (ek OTP ke
//saath, ek bina OTP ke) jo confusing aur bug-prone hota.
public interface UserService {

	// Email se user dhoondhta hai
    UserResponse getUserByEmail(String email);

 // ID se user dhoondhta hai — Profile page jaisi jagah use hoga
    UserResponse getUserById(Long id);

    // Signup form submit hone se pehle frontend check kar sake "kya email already liya gaya hai"
    boolean emailExists(String email);
    
 // Account > Profile page ke "Save Changes" ke liye — naam/phone/avatar update karta hai
    UserResponse updateProfile(Long userId, UpdateProfileRequest request);

    // Account > Security page ke "Change Password" ke liye — current
    // password verify karke naya password (hashed) save karta hai
    void changePassword(Long userId, ChangePasswordRequest request);
    
 // Profile photo upload karta hai (Cloudinary pe), aur User ka
    // avatar-URL update karta hai
    UserResponse uploadAvatar(Long userId, MultipartFile file);
}
