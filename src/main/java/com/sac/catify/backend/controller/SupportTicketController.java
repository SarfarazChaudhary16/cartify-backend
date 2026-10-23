package com.sac.catify.backend.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.sac.catify.backend.dto.MessageResponse;
import com.sac.catify.backend.dto.SupportTicketRequest;
import com.sac.catify.backend.dto.SupportTicketResponse;
import com.sac.catify.backend.security.CustomUserDetails;
import com.sac.catify.backend.service.SupportTicketService;

import java.util.List;

// @RestController -> is class ke saare methods ka return-value SEEDHA
// JSON response-body mein jayega (koi HTML page render nahi hoga)
//
// @RequestMapping("/api/support-tickets") -> is class ke ANDAR jitne bhi
// endpoints hain, unka URL isi prefix se shuru hoga. Jaise neeche
// "@PostMapping" (bina kisi extra path ke) likha hai, uska POORA URL
// hoga sirf "/api/support-tickets" (POST method ke saath)
@RestController
@RequestMapping("/api/support-tickets")
public class SupportTicketController {

    // Controller sirf SupportTicketService (interface) ko jaanta hai,
    // uski implementation (SupportTicketServiceImpl) ko nahi
    private final SupportTicketService supportTicketService;

    // Constructor Injection — Spring khud SupportTicketServiceImpl ka
    // object bana ke (@Service annotation ki wajah se) yahan de dega.
    // Single constructor hone ki wajah se @Autowired likhna zaroori nahi.
    public SupportTicketController(SupportTicketService supportTicketService) {
        this.supportTicketService = supportTicketService;
    }

    // ===================================================================
    // POST /api/support-tickets
    // ===================================================================
    // Kab call hota hai: Help Center ke "Raise a Ticket" page pe user
    // form bhar ke "Submit Ticket" button click karta hai
    @PostMapping
    public ResponseEntity<SupportTicketResponse> createTicket(
            // @AuthenticationPrincipal: current logged-in user ka poora
            // data (CustomUserDetails wrapper ke andar) seedha yahan aa
            // jata hai — Spring Security ye JwtAuthFilter ke verify-kiye-
            // hue authentication se nikal ke deta hai
            @AuthenticationPrincipal CustomUserDetails currentUser,

            // @Valid: SupportTicketRequest ke andar lage @NotBlank/@Size
            // annotations ko Spring khud check karega, submit karne se
            // PEHLE. Agar koi fail ho (jaise subject khaali), Spring
            // khud "400 Bad Request" bhej dega, method ke andar ka code
            // chalega hi nahi.
            //
            // @RequestBody: incoming HTTP request ka JSON, automatically
            // SupportTicketRequest object mein convert (deserialize) ho jayega
            @Valid @RequestBody SupportTicketRequest request) {

        // currentUser.getId() se numeric User-ID nikal ke Service ko bhejte hain
        SupportTicketResponse response = supportTicketService.createTicket(currentUser.getId(), request);

        // new ResponseEntity<>(response, HttpStatus.CREATED) -> HTTP 201
        // status ke saath response bhejta hai. 201 = "Created" — REST
        // convention hai jab koi NAYI cheez successfully database mein
        // ban jaye (sirf 200 bhi chal jata, lekin 201 zyada precise/correct hai)
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
   
    
    // POST /api/support-tickets/upload-attachment
    // Ticket-form ke "Attach a file" se — pehle image UPLOAD hoti hai
    // (Cloudinary URL milti hai), phir wahi URL createTicket() request
    // ke "attachmentUrl" field mein bheja jata hai
    @PostMapping("/upload-attachment")
    public ResponseEntity<MessageResponse> uploadAttachment(
            @RequestParam("file") MultipartFile file) {
        String url = supportTicketService.uploadAttachment(file);
        return ResponseEntity.ok(new MessageResponse(url));
    }

    // ===================================================================
    // GET /api/support-tickets/my-tickets
    // ===================================================================
    // Kab call hota hai: "Raise a Ticket" page ke neeche "My Tickets"
    // section load hote hi
    @GetMapping("/my-tickets")
    public ResponseEntity<List<SupportTicketResponse>> getMyTickets(
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        List<SupportTicketResponse> tickets = supportTicketService.getMyTickets(currentUser.getId());

        // ResponseEntity.ok(data) -> shortcut jo "200 OK status + ye
        // data body mein" wala response bana deta hai (new ResponseEntity
        // likhne ka chhota/convenient version, jab status HAMESHA 200 hi
        // hona ho, jaisa GET requests mein aam taur pe hota hai)
        return ResponseEntity.ok(tickets);
    }
    
   // GET /api/support-tickets/{ticketId}
    @GetMapping("/{ticketId}")
    public ResponseEntity<SupportTicketResponse> getTicketById(
            @AuthenticationPrincipal
            CustomUserDetails currentUser,
            @PathVariable Long ticketId) {
        return ResponseEntity.ok(supportTicketService.getTicketForUser(currentUser.getId(), ticketId));
    }
    
    // GET /api/support-tickets/admin/all
    @GetMapping("/admin/all")
    public ResponseEntity<List<SupportTicketResponse>> getAllTicketsForAdmin() {
        return ResponseEntity.ok(supportTicketService.getAllTicketsForAdmin());
    }

    // PUT /api/support-tickets/admin/{ticketId}/status?status=RESOLVED
    @PutMapping("/admin/{ticketId}/status")
    public ResponseEntity<SupportTicketResponse> updateTicketStatus(
            @PathVariable Long ticketId,
            @RequestParam String status) {
        return ResponseEntity.ok(supportTicketService.updateTicketStatus(ticketId, status));
    }
}
