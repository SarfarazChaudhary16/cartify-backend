package com.sac.catify.backend.controller;

import com.sac.catify.backend.dto.AiChatRequest;
import com.sac.catify.backend.dto.AiChatResponse;
import com.sac.catify.backend.security.CustomUserDetails;
import com.sac.catify.backend.service.AiChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiChatController {

    private final AiChatService aiChatService;

    public AiChatController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    // PUBLIC endpoint — logged in aur guest dono use kar sakte hain
    // Agar logged in hai to userId inject karte hain (real data ke liye)
    @PostMapping("/chat")
    public ResponseEntity<AiChatResponse> chat(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestBody AiChatRequest request) {

        // Agar logged in hai to userId set karo
        if (currentUser != null) {
            request.setUserId(currentUser.getId());
        }

        return ResponseEntity.ok(aiChatService.chat(request));
    }
}
