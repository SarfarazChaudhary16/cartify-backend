package com.sac.catify.backend.service;

import java.util.List;

import com.sac.catify.backend.dto.NotificationResponse;
import com.sac.catify.backend.entity.NotificationCategory;

public interface NotificationService {

    // Notification Center page load hote hi — is user ki SAARI
    // notifications (naye-se-purane order mein)
    List<NotificationResponse> getNotifications(Long userId);

    // Header bell-icon ka "unread count" badge dikhane ke liye — sirf
    // ginti (number) chahiye, poori list nahi
    long getUnreadCount(Long userId);

    // Ek specific notification pe click karke "read" mark karna
    NotificationResponse markAsRead(Long userId, Long notificationId);

    // "Mark all read" button — is user ki SAARI unread notifications
    // ek saath "read" ho jayengi
    void markAllAsRead(Long userId);

    // Individual delete (trash icon)
    void deleteNotification(Long userId, Long notificationId);

    // "Clear all" button — is user ki SAARI notifications delete
    void clearAll(Long userId);

    // ===== Ye method hum BAAKI SERVICES se call karenge (Order, waghera) =====
    // Jaise jab Order place ho, ya status advance ho, hum chahenge ki
    // ek notification AUTOMATICALLY generate ho jaye ("Your order has
    // shipped!" jaisa). Ye method ISI kaam ke liye hai — koi Controller
    // isse seedha call nahi karega, sirf other Services (Order module)
    // isko internally use karenge.
    void createNotification(Long userId, NotificationCategory category, String title, String message);
}
