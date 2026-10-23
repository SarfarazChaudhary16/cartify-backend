package com.sac.catify.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

// SupportTicket Entity ka safe response version — Help Center page
// (frontend RaiseTicketPage.tsx "My Tickets" section) isi shape ko use
// karega. Dhyan do: humein "kis user ne banaya" batane ki zarurat nahi
// (frontend hamesha "apne khud ke" tickets maangta hai), isliye User
// ka koi bhi field is DTO mein include NAHI kiya — Entity ke "user"
// field ko yahan bilkul chhor diya hai.
public class SupportTicketResponse {

    private Long id;
    private String ticketNumber;   // jaise "CF-T48213" — customer ko yahi dikhta hai
    private String subject;
    private String category;       // "orders", "payments", "shipping", waghera (String hai, enum nahi — Entity mein bhi String tha)
    private String orderNumber;    // optional, agar ticket kisi specific order se related ho
    private String description;
    private String attachmentName; // optional file attachment ka naam
    
    private String attachmentUrl;
    private String userName;
    private String userEmail;
    private String status;
    private LocalDateTime createdAt;
    private List<TicketTrackingStepResponse> trackingSteps;
             // TicketStatus enum ko String ki tarah bhejenge (jaise "OPEN")
    

    public SupportTicketResponse() {
    }
    
    
    public SupportTicketResponse(Long id, String ticketNumber, String subject, String category,
            String orderNumber, String description, String attachmentName,
            String attachmentUrl, String userName, String userEmail,
            String status, LocalDateTime createdAt,
            List<TicketTrackingStepResponse> trackingSteps) {
    	
this.id = id;
this.ticketNumber = ticketNumber;
this.subject = subject;
this.category = category;
this.orderNumber = orderNumber;
this.description = description;
this.attachmentName = attachmentName;
this.attachmentUrl = attachmentUrl;
this.userName = userName;
this.userEmail = userEmail;
this.status = status;
this.createdAt = createdAt;
this.trackingSteps = trackingSteps;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    
    public List<TicketTrackingStepResponse> getTrackingSteps() {
        return trackingSteps;
    }

    public void setTrackingSteps(List<TicketTrackingStepResponse> trackingSteps) {
        this.trackingSteps = trackingSteps;
    }
}
