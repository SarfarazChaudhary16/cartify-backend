package com.sac.catify.backend.dto;

import java.time.LocalDateTime;

import com.sac.catify.backend.entity.NotificationCategory;

// Notification Entity ka safe response version — Notification Center
// page (frontend NotificationsPage.tsx) aur header ke bell-dropdown
// (NotificationsMenu.tsx), dono isi shape ko use karenge.
public class NotificationResponse {

    private Long id;

    // Category enum ko hum yahan SEEDHA "NotificationCategory" type mein
    // rakh rahe hain (String mein convert nahi kar rahe, jaisa OrderStatus
    // ke saath humne pehle kabhi kiya tha) — dono approach chalte hain,
    // Jackson (JSON library) enum ko automatically uske NAAM (jaise "ORDERS")
    // ki tarah JSON mein likh deta hai, isliye extra conversion ki zarurat
    // nahi padi is baar. (Chhota inconsistency hai codebase mein — kabhi
    // hum enum ko String bana ke bhejte hain, kabhi seedha enum — dono
    // hi tareeke kaam karte hain, bas ek design-choice hai)
    private NotificationCategory category;

    private String title;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;

    public NotificationResponse() {
    }

    public NotificationResponse(Long id, NotificationCategory category, String title,
                                 String message, boolean isRead, LocalDateTime createdAt) {
        this.id = id;
        this.category = category;
        this.title = title;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotificationCategory getCategory() {
        return category;
    }

    public void setCategory(NotificationCategory category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
