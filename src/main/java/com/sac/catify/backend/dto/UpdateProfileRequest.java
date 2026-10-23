package com.sac.catify.backend.dto;

import jakarta.validation.constraints.NotBlank;

// Account > Profile page ke "Save Changes" button se aane wala data.
// Email yahan JAAN-BOOJH KAR nahi hai — email UPDATE karna is app mein
// support nahi karte (kyunki email = login-identity hai, use badalne se
// JWT-token/security-flow complicated ho jate); avatar bhi optional
// isliye hai kyunki DiceBear-generated default avatar rakh sakte hain.
public class UpdateProfileRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String phone;

    private String avatar;

    public UpdateProfileRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
