package com.sac.catify.backend.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.sac.catify.backend.dto.SupportTicketRequest;
import com.sac.catify.backend.dto.SupportTicketResponse;

public interface SupportTicketService {

    // "Submit Ticket" button — naya ticket banata hai, ek unique
    // ticket-number generate karta hai, status hamesha OPEN se shuru hota hai
    SupportTicketResponse createTicket(Long userId, SupportTicketRequest request);

    // "My Tickets" section — logged-in user ne jitne bhi tickets raise
    // kiye hain, sabki list (naye-se-purane)
    List<SupportTicketResponse> getMyTickets(Long userId);
    
    SupportTicketResponse getTicketForUser(Long userId, Long ticketId);
    
    // Admin > Support Tickets page — SAARE customers ke SAARE tickets
    List<SupportTicketResponse> getAllTicketsForAdmin();

    // Admin ka status-dropdown — Open/In-Progress/Resolved badalne ke liye
    SupportTicketResponse updateTicketStatus(Long ticketId, String status);
    
    String uploadAttachment(MultipartFile file);
    
    
}
