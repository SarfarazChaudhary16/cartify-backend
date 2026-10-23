package com.sac.catify.backend.controller;

import com.sac.catify.backend.dto.ContactRequest;
import com.sac.catify.backend.dto.MessageResponse;
import com.sac.catify.backend.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    // PUBLIC endpoint — login zaroori nahi, koi bhi visitor use kar sake
    @PostMapping
    public ResponseEntity<MessageResponse> submitMessage(@Valid @RequestBody ContactRequest request) {
        contactService.submitMessage(request);
        return ResponseEntity.ok(new MessageResponse("Thanks for reaching out! We'll get back to you soon."));
    }
}
