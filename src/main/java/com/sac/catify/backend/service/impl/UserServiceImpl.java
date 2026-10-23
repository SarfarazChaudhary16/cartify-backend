package com.sac.catify.backend.service.impl;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.sac.catify.backend.dto.ChangePasswordRequest;
import com.sac.catify.backend.dto.UpdateProfileRequest;
import com.sac.catify.backend.dto.UserResponse;
import com.sac.catify.backend.entity.User;
import com.sac.catify.backend.exception.ApiException;
import com.sac.catify.backend.repository.UserRepository;
import com.sac.catify.backend.service.UserService;

import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;

    // PasswordEncoder chahiye — Change Password ke liye (current-password
    // verify karna, aur naya password hash karna) — SecurityConfig mein
    // pehle se @Bean bana hua hai (Phase B3 mein), Spring khud yahan
    // inject kar dega
    private final PasswordEncoder passwordEncoder;

    private final Cloudinary cloudinary;
    
    public UserServiceImpl(UserRepository userRepository, 
    		               PasswordEncoder passwordEncoder,
    		               Cloudinary cloudinary) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.cloudinary = cloudinary;
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> ApiException.notFound("User not found with email: " + email));
        return mapToResponse(user);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("User not found with id: " + id));
        return mapToResponse(user);
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("User not found with id: " + userId));

        user.setName(request.getName());
        user.setPhone(request.getPhone());

        // Avatar sirf tab update karte hain jab naya diya gaya ho (khaali
        // na ho) — warna user ka existing avatar accidentally null ho jata
        if (request.getAvatar() != null && !request.getAvatar().isBlank()) {
            user.setAvatar(request.getAvatar());
        }

        User updated = userRepository.save(user);
        return mapToResponse(updated);
    }

    @Override
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("User not found with id: " + userId));

        // ===== Step 1: Current password verify karo =====
        // passwordEncoder.matches(rawPassword, hashedPasswordFromDB) —
        // bilkul wahi pattern jo Login ke waqt use hota hai (AuthServiceImpl)
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw ApiException.badRequest("Current password is incorrect.");
        }

        // ===== Step 2: Naya password hash karke save karo =====
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
    
    @Override
    public UserResponse uploadAvatar(Long userId, org.springframework.web.multipart.MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("User not found with id: " + userId));

        try {
            // Cloudinary ko file ke bytes bhejte hain — ye upload karke
            // ek "secure_url" (HTTPS CDN link) return karta hai
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "cartify/avatars", // Cloudinary account ke andar organized rakhne ke liye
                            "resource_type", "image"
                    )
            );

            String imageUrl = (String) uploadResult.get("secure_url");

            user.setAvatar(imageUrl);
            User updated = userRepository.save(user);

            return mapToResponse(updated);

        } catch (java.io.IOException e) {
            throw new ApiException("Failed to upload image: " + e.getMessage(),
                    org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getAvatar(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}