package com.sac.catify.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "support_tickets")
public class SupportTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_number", nullable = false, unique = true)
    private String ticketNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String subject;

    private String category;

    @Column(name = "order_number")
    private String orderNumber;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "attachment_name")
    private String attachmentName;

    // NAYA: Real Cloudinary-uploaded image ka URL
    @Column(name = "attachment_url")
    private String attachmentUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // NAYA: Order jaisa hi tracking-timeline
    @ElementCollection
    @CollectionTable(name = "ticket_tracking_steps", joinColumns = @JoinColumn(name = "ticket_id"))
    private List<TicketTrackingStep> trackingSteps = new ArrayList<>();

    public SupportTicket() {
    }

    public SupportTicket(String ticketNumber, User user, String subject, String category,
                          String orderNumber, String description, String attachmentName) {
        this.ticketNumber = ticketNumber;
        this.user = user;
        this.subject = subject;
        this.category = category;
        this.orderNumber = orderNumber;
        this.description = description;
        this.attachmentName = attachmentName;
        this.status = TicketStatus.OPEN;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<TicketTrackingStep> getTrackingSteps() {
        return trackingSteps;
    }

    public void setTrackingSteps(List<TicketTrackingStep> trackingSteps) {
        this.trackingSteps = trackingSteps;
    }
}