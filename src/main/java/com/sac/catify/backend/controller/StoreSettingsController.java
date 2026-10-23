package com.sac.catify.backend.controller;

import com.sac.catify.backend.dto.StoreSettingsRequest;
import com.sac.catify.backend.dto.StoreSettingsResponse;
import com.sac.catify.backend.service.StoreSettingsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/store-settings")
public class StoreSettingsController {

    private final StoreSettingsService storeSettingsService;

    public StoreSettingsController(StoreSettingsService storeSettingsService) {
        this.storeSettingsService = storeSettingsService;
    }

    // GET /api/store-settings — PUBLIC (koi bhi check kar sake maintenance-mode)
    @GetMapping
    public ResponseEntity<StoreSettingsResponse> getSettings() {
        return ResponseEntity.ok(storeSettingsService.getSettings());
    }

    // PUT /api/admin/store-settings — sirf ADMIN
    @PutMapping("/admin")
    public ResponseEntity<StoreSettingsResponse> updateSettings(@Valid @RequestBody StoreSettingsRequest request) {
        return ResponseEntity.ok(storeSettingsService.updateSettings(request));
    }
}
