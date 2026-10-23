package com.sac.catify.backend.dto;

import jakarta.validation.constraints.NotBlank;

// "Cancel Order" button click hone pe frontend jo reason bhejta hai
// (CancelOrderModal.tsx mein dropdown-jaisi options thi: "Changed my mind",
// "Ordered by mistake", waghera) — us reason ko carry karne ke liye
// ye chhoti-si DTO hai.
public class OrderCancelRequest {

    @NotBlank(message = "Please provide a reason for cancellation")
    private String reason;

    public OrderCancelRequest() {
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
