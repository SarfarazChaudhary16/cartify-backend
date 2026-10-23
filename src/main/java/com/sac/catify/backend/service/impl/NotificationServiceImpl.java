package com.sac.catify.backend.service.impl;

import org.springframework.stereotype.Service;

import com.sac.catify.backend.dto.NotificationResponse;
import com.sac.catify.backend.entity.Notification;
import com.sac.catify.backend.entity.NotificationCategory;
import com.sac.catify.backend.entity.User;
import com.sac.catify.backend.exception.ApiException;
import com.sac.catify.backend.repository.NotificationRepository;
import com.sac.catify.backend.repository.UserRepository;
import com.sac.catify.backend.service.NotificationService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    // 2 repositories chahiye: Notification (khud notifications ke liye)
    // aur User (jab naya notification banana ho, "User" object link karna
    // padta hai @ManyToOne relationship ki wajah se, sirf userId se kaam nahi chalega)
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                    UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<NotificationResponse> getNotifications(Long userId) {
        // findByUserIdOrderByCreatedAtDesc() Phase B2 mein bana tha —
        // "is user ki saari notifications, sabse NAYI pehle"
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public long getUnreadCount(Long userId) {
        // findByUserIdAndIsReadFalse() bhi Phase B2 mein bana tha —
        // "is user ki jitni notifications abhi tak PADHI nahi gayi"
        // .size() se List ki LENGTH (count) nikal lete hain
        return notificationRepository.findByUserIdAndIsReadFalse(userId).size();
    }

    @Override
    public NotificationResponse markAsRead(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> ApiException.notFound("Notification not found with id: " + notificationId));

        // ===== SECURITY CHECK — pehchana pattern hai ye =====
        // Verify karte hain ki ye notification ISI user ki hai jo request
        // kar raha hai — warna koi bhi kisi AUR ki notification "read"
        // mark kar sakta tha sirf ID guess karke (IDOR vulnerability,
        // jaisa Cart/Address/Order mein bhi discuss kiya tha)
        if (!notification.getUser().getId().equals(userId)) {
            throw ApiException.forbidden("This notification does not belong to you.");
        }

        notification.setRead(true);
        Notification updated = notificationRepository.save(notification);
        return mapToResponse(updated);
    }

    @Override
    public void markAllAsRead(Long userId) {
        // Is user ki SAARI unread notifications nikalte hain, ek-ek karke
        // sabko "read=true" karke save karte hain
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndIsReadFalse(userId);

        for (Notification notification : unreadNotifications) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
        // NOTE: Ye thoda "inefficient" hai kyunki har notification ke
        // liye ek alag UPDATE query chal rahi hai (loop ke andar save()).
        // Bade scale pe, ek "bulk update" query (jaise @Modifying @Query
        // se seedha "UPDATE notifications SET is_read=true WHERE user_id=?")
        // zyada efficient hoti. Abhi simplicity ke liye is straightforward
        // approach se kaam chala rahe hain — chaho to future mein optimize
        // kar sakte ho.
    }

    @Override
    public void deleteNotification(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> ApiException.notFound("Notification not found with id: " + notificationId));

        // Yahan bhi wahi ownership-security-check
        if (!notification.getUser().getId().equals(userId)) {
            throw ApiException.forbidden("This notification does not belong to you.");
        }

        notificationRepository.delete(notification);
    }

    @Override
    public void clearAll(Long userId) {
        // Saari notifications nikal ke, ek-ek karke delete karte hain
        List<Notification> allNotifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        for (Notification notification : allNotifications) {
            notificationRepository.delete(notification);
        }
        // (Yahan bhi, agar chaho to Repository mein ek "deleteByUserId()"
        // bulk-delete method bana sakte the, jaisa CartItemRepository mein
        // tha — abhi is simple approach se kaam chala rahe hain)
    }

    @Override
    public void createNotification(Long userId, NotificationCategory category, String title, String message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("User not found with id: " + userId));

        // Notification Entity ka constructor (Phase B2 mein banaya tha)
        // khud "isRead=false" aur "createdAt=now()" set kar deta hai
        Notification notification = new Notification(user, category, title, message);
        notificationRepository.save(notification);
    }

    // ===== Private Helper =====

    private NotificationResponse mapToResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getCategory(),
                notification.getTitle(),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}
