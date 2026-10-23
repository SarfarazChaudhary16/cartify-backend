package com.sac.catify.backend.entity;

import jakarta.persistence.*;

// Ye ek "singleton-jaisi" entity hai — poore system mein SIRF EK row
// hogi (id=1 hamesha), kyunki store-settings GLOBAL hain (kisi specific
// user ke nahi, poore app ke liye common). Koi bhi Admin ise update
// kare, SABHI Admins ko wahi latest values dikhengi.
@Entity
@Table(name = "store_settings")
public class StoreSettings {

    @Id
    private Long id = 1L; // hamesha fixed — sirf ek hi row exist karegi

    @Column(name = "store_name", nullable = false)
    private String storeName = "Cartify";

    @Column(name = "support_email", nullable = false)
    private String supportEmail = "support@cartify.example";

    @Column(name = "maintenance_mode", nullable = false)
    private boolean maintenanceMode = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
