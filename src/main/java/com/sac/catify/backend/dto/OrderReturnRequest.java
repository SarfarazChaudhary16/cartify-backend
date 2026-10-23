package com.sac.catify.backend.dto;

import jakarta.validation.constraints.NotBlank;

// "Return / Replace" button ke liye — frontend mein ReturnReplaceModal.tsx
// mein 2 cheezein poochi jati thi: type (return ya replace) aur reason
// (text describe karke, jaise "Wrong size delivered")
public class OrderReturnRequest {

    @NotBlank(message = "Please specify return or replace")
    private String type; // "return" ya "replace"

    @NotBlank(message = "Please describe the issue")
    private String reason;

    public OrderReturnRequest() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
