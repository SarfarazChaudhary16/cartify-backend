package com.sac.catify.backend.dto;


import java.time.LocalDateTime;

import com.sac.catify.backend.entity.OrderStatus;

//Kyun bana: TrackingStep humne Phase B2 mein @Embeddable banaya tha 
//(Order ke andar list ki tarah rehta hai — "koi apni ID/table nahi"). 
//Response ke liye humein iska ek chhota "safe" version chahiye. 
//Ye technically TrackingStep Embeddable jaisa hi dikhta hai, lekin 
//naam mein "Response" likh ke hum clearly bata rahe hain ki ye 
//"outgoing JSON shape" hai, na ki Entity/Embeddable khud.

// Order Tracking Timeline (frontend OrderTrackingTimeline component) ke
// liye ek single "step" ka response — jaise: "Shipped — 24 July, 3:30 PM"
public class TrackingStepResponse {

    private OrderStatus status;
    private String label;
    private LocalDateTime timestamp;
    private boolean completed;

    public TrackingStepResponse() {
    }

    public TrackingStepResponse(OrderStatus status, String label, LocalDateTime timestamp, boolean completed) {
        this.status = status;
        this.label = label;
        this.timestamp = timestamp;
        this.completed = completed;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}