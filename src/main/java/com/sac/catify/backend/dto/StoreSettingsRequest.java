package com.sac.catify.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class StoreSettingsRequest {

    @NotBlank(message = "Store name is required")
    private String storeName;

    @NotBlank(message = "Support email is required")
    private String supportEmail;

    private boolean maintenanceMode;

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getSupportEmail() {
        return supportEmail;
    }

    public void setSupportEmail(String supportEmail) {
        this.supportEmail = supportEmail;
    }

    public boolean isMaintenanceMode() {
        return maintenanceMode;
    }

    public void setMaintenanceMode(boolean maintenanceMode) {
        this.maintenanceMode = maintenanceMode;
    }
}
