package com.sac.catify.backend.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.sac.catify.backend.dto.SupportTicketRequest;
import com.sac.catify.backend.dto.SupportTicketResponse;
import com.sac.catify.backend.dto.TicketTrackingStepResponse;
import com.sac.catify.backend.entity.SupportTicket;
import com.sac.catify.backend.entity.TicketStatus;
import com.sac.catify.backend.entity.TicketTrackingStep;
import com.sac.catify.backend.entity.User;
import com.sac.catify.backend.exception.ApiException;
import com.sac.catify.backend.repository.SupportTicketRepository;
import com.sac.catify.backend.repository.UserRepository;
import com.sac.catify.backend.service.EmailService;
import com.sac.catify.backend.service.NotificationService;
import com.sac.catify.backend.service.SupportTicketService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class SupportTicketServiceImpl implements SupportTicketService {

    private final SupportTicketRepository supportTicketRepository;
    private final Cloudinary cloudinary;
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final UserRepository userRepository;

    // Random object ticket-number generate karne ke liye — Order module
    // mein bhi "CF-48213" jaisa number banane ke liye Random use kiya tha,
    // yahan same pattern hai bas prefix "CF-T" hai (Ticket ke liye)
    private final Random random = new Random();

    public SupportTicketServiceImpl(SupportTicketRepository supportTicketRepository,
                                     UserRepository userRepository,
                                     Cloudinary cloudinary,
                                     NotificationService notificationService,
                                     EmailService emailService) {
    	
        this.supportTicketRepository = supportTicketRepository;
        this.userRepository = userRepository;
        this.cloudinary = cloudinary;
        this.notificationService = notificationService;
        this.emailService = emailService;
    }

    @Override
    public SupportTicketResponse createTicket(Long userId, SupportTicketRequest request) {

        // Step 1: User dhoondhte hain — SupportTicket Entity banane ke
        // liye humein POORA User object chahiye (@ManyToOne relationship
        // ki wajah se), sirf userId (Long) se kaam nahi chalega
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("User not found with id: " + userId));

        // Step 2: Unique ticket-number generate karte hain
        String ticketNumber = generateTicketNumber();

        // Step 3: SupportTicket Entity ka constructor use karke naya
        // ticket banate hain. Yaad hai Phase B2 mein iska constructor
        // dekha tha? Wahi khud "status = OPEN" aur "createdAt = now()"
        // set kar deta hai andar-hi-andar — humein alag se likhna nahi padta.
        SupportTicket ticket = new SupportTicket(
                ticketNumber,
                user,
                request.getSubject(),
                request.getCategory(),
                request.getOrderNumber(),
                request.getDescription(),
                request.getAttachmentName()
        );
        ticket.setAttachmentUrl(request.getAttachmentUrl());

        // NAYA: Initial tracking-timeline banate hain — Order jaisa hi
        // pattern (placeOrder() mein trackingSteps banaya tha, yaad hai?)
        List<TicketTrackingStep> trackingSteps = new ArrayList<>();
        trackingSteps.add(new TicketTrackingStep(TicketStatus.OPEN, "Ticket raised", LocalDateTime.now(), true));
        trackingSteps.add(new TicketTrackingStep(TicketStatus.IN_PROGRESS, "In progress", null, false));
        trackingSteps.add(new TicketTrackingStep(TicketStatus.RESOLVED, "Resolved", null, false));
        ticket.setTrackingSteps(trackingSteps);

        SupportTicket saved = supportTicketRepository.save(ticket);
        return mapToResponse(saved);
    }
    
    // Ticket-attachment image upload — bilkul ProfilePage ke avatar-
    // upload jaisa pattern, Cloudinary use karke
    public String uploadAttachment(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("folder", "cartify/support-tickets", "resource_type", "auto")
            );
            return (String) uploadResult.get("secure_url");
        } catch (java.io.IOException e) {
            throw new ApiException(
                    "Failed to upload attachment: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    @Override
    public List<SupportTicketResponse> getMyTickets(Long userId) {
        return supportTicketRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<SupportTicketResponse> getAllTicketsForAdmin() {
        return supportTicketRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SupportTicketResponse updateTicketStatus(Long ticketId, String status) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> com.sac.catify.backend.exception.ApiException.notFound("Ticket not found with id: " + ticketId));

        // String status ko TicketStatus enum mein convert karte hain
        // (jaisa CouponServiceImpl mein DiscountType ke saath kiya tha)
        com.sac.catify.backend.entity.TicketStatus newStatus =
                com.sac.catify.backend.entity.TicketStatus.valueOf(status.toUpperCase());

        ticket.setStatus(newStatus);

        // NAYA: Tracking-timeline mein bhi corresponding step "complete" mark
        // karte hain — Order ke advanceStatus() jaisa hi pattern
        for (TicketTrackingStep step : ticket.getTrackingSteps()) {
            if (step.getStatus() == newStatus) {
                step.setCompleted(true);
                step.setTimestamp(LocalDateTime.now());
            }
        }

        SupportTicket updated = supportTicketRepository.save(ticket);
        

        // NAYA: Status badalte hi user ko notification milti hai — taaki
        // use pata chale uska ticket "In Progress" ya "Resolved" ho gaya
        notificationService.createNotification(
                ticket.getUser().getId(),
                com.sac.catify.backend.entity.NotificationCategory.ACCOUNT,
                "Ticket " + formatStatusLabel(newStatus),
                "Your support ticket #" + ticket.getTicketNumber() + " is now " + formatStatusLabel(newStatus) + "."
        );
        
        // Real email bhejte hain (premium HTML template ke saath)
        // — sirf IN_PROGRESS aur RESOLVED ke liye bhejte hain (OPEN
        // wapas set hone ka koi practical use-case nahi hai, isliye skip)
        if (newStatus == TicketStatus.IN_PROGRESS || newStatus == TicketStatus.RESOLVED) {
            emailService.sendTicketStatusEmail(
                    ticket.getUser().getEmail(),
                    ticket.getUser().getName(),
                    ticket.getTicketNumber(),
                    ticket.getSubject(),
                    newStatus.name()
            );
        }
        return mapToResponse(updated);
    }
    
    private String formatStatusLabel(com.sac.catify.backend.entity.TicketStatus status) {
        return status.name().replace("_", " ").toLowerCase();
    }
    
    @Override
    public SupportTicketResponse getTicketForUser(Long userId, Long ticketId) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> com.sac.catify.backend.exception.ApiException.notFound("Ticket not found with id: " + ticketId));

        if (!ticket.getUser().getId().equals(userId)) {
            throw com.sac.catify.backend.exception.ApiException.forbidden("This ticket does not belong to you.");
        }

        return mapToResponse(ticket);
    }

    // ===================================================================
    // PRIVATE HELPERS
    // ===================================================================

    // Ticket-number banata hai jaise "CF-T48213" (Order ke "CF-48213"
    // pattern se milta-julta, bas beech mein "T" hai — Ticket ke liye,
    // taaki dono types (Order-number aur Ticket-number) visually alag
    // pehchane ja sakein)
    private String generateTicketNumber() {
        int number = 10000 + random.nextInt(90000); // 5-digit random number
        return "CF-T" + number;
    }

    // SupportTicket Entity ko SupportTicketResponse DTO mein convert
    // karta hai — Entity ke "status" field (jo TicketStatus ENUM hai)
    // ko .name() se String mein badalte hain (jaise TicketStatus.OPEN
    // -> "OPEN"), taaki DTO ka "status" field (jo String type ka hai) match kare
    private SupportTicketResponse mapToResponse(SupportTicket ticket) {
        return new SupportTicketResponse(
                ticket.getId(),
                ticket.getTicketNumber(),
                ticket.getSubject(),
                ticket.getCategory(),
                ticket.getOrderNumber(),
                ticket.getDescription(),
                ticket.getAttachmentName(),
                ticket.getAttachmentUrl(),
                ticket.getUser().getName(),
                ticket.getUser().getEmail(),
                ticket.getStatus().name(),
                ticket.getCreatedAt(),
                ticket.getTrackingSteps().stream()
                        .map(step -> new TicketTrackingStepResponse(step.getStatus(), step.getLabel(), step.getTimestamp(), step.isCompleted()))
                        .collect(Collectors.toList())
        );
    }
}
