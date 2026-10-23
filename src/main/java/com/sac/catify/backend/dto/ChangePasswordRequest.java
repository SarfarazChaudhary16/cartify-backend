package com.sac.catify.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Security page ke "Change Password" form se aane wala data.
// currentPassword maangna ZAROORI hai — chahe user already logged-in ho
// (valid JWT ke saath), password badalne se PEHLE uska EXISTING password
// re-verify karna security best-practice hai (warna koi khula-chhoड़ा
// hua session hijack kar sakta tha, bina current password jaane hi).
public class ChangePasswordRequest {

    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 6, message = "New password must be at least 6 characters")
    private String newPassword;

    public ChangePasswordRequest() {
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
