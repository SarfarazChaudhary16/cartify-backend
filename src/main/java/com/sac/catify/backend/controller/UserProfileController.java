package com.sac.catify.backend.controller;

import com.sac.catify.backend.dto.ChangePasswordRequest;
import com.sac.catify.backend.dto.MessageResponse;
import com.sac.catify.backend.dto.UpdateProfileRequest;
import com.sac.catify.backend.dto.UserResponse;
import com.sac.catify.backend.security.CustomUserDetails;
import com.sac.catify.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

// "/api/users/me" — "me" convention batata hai ki ye endpoints HAMESHA
// "current logged-in user" ke liye hain, kisi bhi arbitrary user-ID ke
// liye nahi (jaise "/api/users/5" hota, jo galti se kisi AUR user ka
// data expose kar sakta tha agar authorization-check chhoot jata)
@RestController
@RequestMapping("/api/users/me")
public class UserProfileController {

    private final UserService userService;

    public UserProfileController(UserService userService) {
        this.userService = userService;
    }

    // GET /api/users/me
    // Profile page load hote hi — current user ka poora data
    @GetMapping
    public ResponseEntity<UserResponse> getMyProfile(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(userService.getUserById(currentUser.getId()));
    }

    // PUT /api/users/me
    // "Save Changes" button — naam/phone/avatar update
    @PutMapping
    public ResponseEntity<UserResponse> updateProfile(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(currentUser.getId(), request));
    }
    
//    name = v6ztuhbk
//    Api key = 385471544465862
//    Api secret key = PFQUz0jKush4DTh_LdRY47HITPw
//    API environment variable = CLOUDINARY_URL=cloudinary://385471544465862:PFQUz0jKush4DTh_LdRY47HITPw@v6ztuhbk

    // PUT /api/users/me/change-password
    @PutMapping("/change-password")
    public ResponseEntity<MessageResponse> changePassword(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(currentUser.getId(), request);
        return ResponseEntity.ok(new MessageResponse("Password updated successfully."));
    }
    
 // POST /api/users/me/avatar
    // "Change photo" camera-icon click karke file select karne pe
    @PostMapping("/avatar")
    public ResponseEntity<UserResponse> uploadAvatar(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(userService.uploadAvatar(currentUser.getId(), file));
    }
}
