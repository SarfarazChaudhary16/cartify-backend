package com.sac.catify.backend.service;

import com.sac.catify.backend.dto.StoreSettingsRequest;
import com.sac.catify.backend.dto.StoreSettingsResponse;

public interface StoreSettingsService {

    // PUBLIC — koi bhi visitor ye check kar sake ki maintenance-mode
    // ON hai ya nahi (frontend ise app-load hote hi check karega)
    StoreSettingsResponse getSettings();

    // Sirf ADMIN — settings update karne ke liye
    StoreSettingsResponse updateSettings(StoreSettingsRequest request);
}
