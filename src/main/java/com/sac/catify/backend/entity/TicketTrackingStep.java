package com.sac.catify.backend.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDateTime;

@Embeddable
public class TicketTrackingStep {

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    private String label;

    private LocalDateTime timestamp;

    private boolean completed;

    public TicketTrackingStep() {
    }

    public TicketTrackingStep(TicketStatus status, String label, LocalDateTime timestamp, boolean completed) {
        this.status = status;
        this.label = label;
        this.timestamp = timestamp;
        this.completed = completed;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
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