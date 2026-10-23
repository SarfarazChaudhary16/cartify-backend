package com.sac.catify.backend.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDateTime;


//Kyun Embeddable: Frontend ke Order Tracking Timeline component mai — 
//har order ke andar tracking steps ki ek list hoti thi ({status, label, timestamp, completed}).
//Ye bhi chhota data-block hai jo hamesha Order ke andar hi rehta hai, isliye Embeddable 
//(ProductSpecification jaisa hi pattern, bas fields alag hain).

// Order tracking timeline ka ek single step, jaise:
// { status: SHIPPED, label: "Shipped from warehouse", timestamp: ..., completed: true }
@Embeddable
public class TrackingStep {

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String label;

    private LocalDateTime timestamp;

    private boolean completed;

    public TrackingStep() {
    }

    public TrackingStep(OrderStatus status, String label, LocalDateTime timestamp, boolean completed) {
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
