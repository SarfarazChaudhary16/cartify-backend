package com.sac.catify.backend.service.impl;

import com.sac.catify.backend.dto.StoreSettingsRequest;
import com.sac.catify.backend.dto.StoreSettingsResponse;
import com.sac.catify.backend.entity.StoreSettings;
import com.sac.catify.backend.repository.StoreSettingsRepository;
import com.sac.catify.backend.service.StoreSettingsService;
import org.springframework.stereotype.Service;

@Service
public class StoreSettingsServiceImpl implements StoreSettingsService {

    private final StoreSettingsRepository storeSettingsRepository;

    public StoreSettingsServiceImpl(StoreSettingsRepository storeSettingsRepository) {
        this.storeSettingsRepository = storeSettingsRepository;
    }

    @Override
    public StoreSettingsResponse getSettings() {
        StoreSettings settings = getOrCreateSettings();
        return mapToResponse(settings);
    }

    @Override
    public StoreSettingsResponse updateSettings(StoreSettingsRequest request) {
        StoreSettings settings = getOrCreateSettings();
        settings.setStoreName(request.getStoreName());
        settings.setSupportEmail(request.getSupportEmail());
        settings.setMaintenanceMode(request.isMaintenanceMode());
        StoreSettings saved = storeSettingsRepository.save(settings);
        return mapToResponse(saved);
    }

    // Kyunki ye ek "singleton" row hai (id=1 hamesha), pehli baar jab
    // koi bhi is settings ko access kare, agar row exist NAHI karti,
    // to hum ise DEFAULT values ke saath bana dete hain
    private StoreSettings getOrCreateSettings() {
        return storeSettingsRepository.findById(1L)
                .orElseGet(() -> storeSettingsRepository.save(new StoreSettings()));
    }

    private StoreSettingsResponse mapToResponse(StoreSettings settings) {
        return new StoreSettingsResponse(settings.getStoreName(), settings.getSupportEmail(), settings.isMaintenanceMode());
    }
}
