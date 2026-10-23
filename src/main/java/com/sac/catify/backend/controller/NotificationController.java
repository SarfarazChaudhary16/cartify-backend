package com.sac.catify.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.sac.catify.backend.dto.NotificationResponse;
import com.sac.catify.backend.security.CustomUserDetails;
import com.sac.catify.backend.service.NotificationService;

import java.util.List;
import java.util.Map;

// @RestController: batata hai Spring ko "is class ke saare methods JSON
// response denge, koi HTML page nahi". Internally ye @Controller +
// @ResponseBody ka combo hai.
//
// @RequestMapping("/api/notifications"): is class ke ANDAR jitne bhi
// endpoints hain, sabka URL "/api/notifications" se shuru hoga. Jaise
// neeche "@GetMapping("/unread-count")" likha hai, to uska POORA URL
// banega "/api/notifications/unread-count".
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    // Controller SIRF interface (NotificationService) ko jaanta hai,
    // implementation (NotificationServiceImpl) ko nahi — Dependency
    // Inversion principle, jo humne shuru mein discuss kiya tha
    private final NotificationService notificationService;

    // Constructor Injection — Spring khud NotificationServiceImpl ka
    // object bana ke (kyunki wo @Service se annotate hai) yahan "inject"
    // kar dega. Single constructor hai isliye @Autowired ki zarurat nahi.
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // ===================================================================
    // GET /api/notifications
    // ===================================================================
    // Kab call hota hai: Notification Center page (frontend
    // NotificationsPage.tsx) load hote hi ye endpoint hit hota hai
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(
            // @AuthenticationPrincipal: Spring Security se "current
            // logged-in user ka poora data" seedha yahan is parameter
            // mein aa jata hai (JwtAuthFilter ne pehle hi verify karke
            // SecurityContext mein daal diya tha — CustomUserDetails
            // hamara khud ka wrapper class hai jo poore User Entity
            // (id samet) ko carry karta hai)
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        // currentUser.getId() se hum User ki numeric database-ID nikalte
        // hain, aur Service ko de dete hain — Service ye ID use karke
        // database se sahi user ki notifications nikalega
        List<NotificationResponse> notifications = notificationService.getNotifications(currentUser.getId());

        // ResponseEntity.ok(data) ek shortcut hai jo "200 OK status +
        // ye data body mein" wala response bana deta hai
        return ResponseEntity.ok(notifications);
    }

    // ===================================================================
    // GET /api/notifications/unread-count
    // ===================================================================
    // Kab call hota hai: Header ka bell-icon load hote hi (aur shayad
    // periodically bhi, jaise har 30 second mein) — sirf "kitni unread
    // hain" jaanne ke liye, poori list nahi maangte (halka/fast request)
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        long count = notificationService.getUnreadCount(currentUser.getId());

        // Map.of("count", count) se hum ek chhota JSON object bana rahe
        // hain: { "count": 5 } — poori NotificationResponse DTO banane
        // ki zarurat nahi thi itni chhoti si cheez (sirf ek number) ke
        // liye, isliye seedha Map use kar liya (jaisa WishlistController
        // mein bhi "isInWishlist" ke liye kiya tha)
        return ResponseEntity.ok(Map.of("count", count));
    }

    // ===================================================================
    // PUT /api/notifications/{notificationId}/read
    // ===================================================================
    // Kab call hota hai: User kisi ek notification pe CLICK karta hai
    // (jaise dropdown mein ya list mein) — usse "read" mark karna hai
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            // @PathVariable: URL ke andar jo "{notificationId}" likha hai
            // (jaise koi "/api/notifications/7/read" call kare), uska
            // "7" value yahan automatically aa jayega
            @PathVariable Long notificationId) {

        NotificationResponse response = notificationService.markAsRead(currentUser.getId(), notificationId);
        return ResponseEntity.ok(response);
    }

    // ===================================================================
    // PUT /api/notifications/mark-all-read
    // ===================================================================
    // Kab call hota hai: "Mark all read" button click hone pe
    @PutMapping("/mark-all-read")
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal CustomUserDetails currentUser) {
        notificationService.markAllAsRead(currentUser.getId());

        // ResponseEntity<Void> ka matlab: is response ke BODY mein kuch
        // nahi jayega (sirf status-code). .build() se hum ResponseEntity
        // ka object "assemble" kar rahe hain bina koi body diye.
        // noContent() -> HTTP 204, jo batata hai "operation successful
        // hui, lekin wapas dikhane-layak koi naya data nahi hai"
        return ResponseEntity.noContent().build();
    }

    // ===================================================================
    // DELETE /api/notifications/{notificationId}
    // ===================================================================
    // Kab call hota hai: Individual notification ke "trash" icon pe click
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long notificationId) {

        notificationService.deleteNotification(currentUser.getId(), notificationId);
        return ResponseEntity.noContent().build();
    }

    // ===================================================================
    // DELETE /api/notifications
    // ===================================================================
    // Kab call hota hai: "Clear all" button click hone pe
    @DeleteMapping
    public ResponseEntity<Void> clearAll(@AuthenticationPrincipal CustomUserDetails currentUser) {
        notificationService.clearAll(currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}