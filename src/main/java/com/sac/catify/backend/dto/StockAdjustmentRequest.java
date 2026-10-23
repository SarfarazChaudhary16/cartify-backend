package com.sac.catify.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// "Adjust Stock" modal se aane wala data. "change" ek SIGNED integer hai —
// positive number (jaise +10) matlab stock ADD karna hai, negative
// (jaise -5) matlab stock REMOVE karna hai. Frontend apne UI mein
// "Add Stock" / "Remove Stock" buttons dikhata hai, lekin jo bhejta hai
// wo already sign ke saath calculate kiya hua number hota hai.
public class StockAdjustmentRequest {

    @NotNull(message = "Change amount is required")
    private Integer change;

    @NotBlank(message = "Reason is required")
    private String reason;

    public StockAdjustmentRequest() {
    }

    public Integer getChange() {
        return change;
    }

    public void setChange(Integer change) {
        this.change = change;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
