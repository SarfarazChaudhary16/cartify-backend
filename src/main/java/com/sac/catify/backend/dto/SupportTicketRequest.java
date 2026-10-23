package com.sac.catify.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// "Raise a Ticket" form (frontend RaiseTicketPage.tsx) se aane wala data.
// Dhyan do: yahan "status" nahi hai — naya ticket HAMESHA "OPEN" status
// se shuru hota hai, isse Service layer khud decide karega, user ko
// choose karne ki zarurat/permission nahi.
public class SupportTicketRequest {

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Category is required")
    private String category;

    private String orderNumber; // optional — sabhi tickets kisi order se related nahi hote

    @NotBlank(message = "Description is required")
    @Size(min = 15, message = "Please describe your issue in at least 15 characters")
    private String description;

    private String attachmentName; // optional
    
    private String attachmentUrl;

    public SupportTicketRequest() {
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
}
