package com.sac.catify.backend.service.impl;

import com.sac.catify.backend.dto.AiChatRequest;
import com.sac.catify.backend.dto.AiChatResponse;
import com.sac.catify.backend.entity.Order;
import com.sac.catify.backend.entity.OrderStatus;
import com.sac.catify.backend.repository.OrderRepository;
import com.sac.catify.backend.repository.SupportTicketRepository;
import com.sac.catify.backend.service.AiChatService;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class AiChatServiceImpl implements AiChatService {

    @Value("${groq.api.key}")
    private String groqApiKey;

    private final OrderRepository orderRepository;
    private final SupportTicketRepository supportTicketRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public AiChatServiceImpl(OrderRepository orderRepository,
                              SupportTicketRepository supportTicketRepository) {
        this.orderRepository = orderRepository;
        this.supportTicketRepository = supportTicketRepository;
    }

    @Override
    public AiChatResponse chat(AiChatRequest request) {
        try {
            // ===== SYSTEM PROMPT banate hain =====
            // Agar userId hai to user ka actual data (orders, tickets)
            // system prompt mein inject karte hain — taaki AI real
            // information de sake, fake/hallucinated nahi
            String systemPrompt = buildSystemPrompt(request.getUserId());

            // ===== Groq ke liye messages array banate hain =====
            List<java.util.Map<String, String>> groqMessages = new ArrayList<>();

            // System message pehle
            groqMessages.add(java.util.Map.of("role", "system", "content", systemPrompt));

            // Conversation history add karte hain
            for (AiChatRequest.ChatMessage msg : request.getMessages()) {
                groqMessages.add(java.util.Map.of("role", msg.getRole(), "content", msg.getContent()));
            }

            // ===== Groq API request body =====
            java.util.Map<String, Object> requestBody = new java.util.HashMap<>();
            requestBody.put("model", "llama-3.3-70b-versatile");
            requestBody.put("messages", groqMessages);
            requestBody.put("max_tokens", 500);
            requestBody.put("temperature", 0.7);

            String requestBodyJson = objectMapper.writeValueAsString(requestBody);

            // ===== HTTP Request bhejte hain =====
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.groq.com/openai/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + groqApiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            // ===== Response parse karte hain =====
            JsonNode jsonResponse = objectMapper.readTree(response.body());
            String reply = jsonResponse
                    .path("choices")
                    .path(0)
                    .path("message")
                    .path("content")
                    .asText("Sorry, I couldn't process your request.");

            // ===== Action detect karte hain =====
            // Agar AI ne kaha "raise ticket" ya "file a complaint" to
            // frontend ko redirect action bhejte hain
            String action = null;
            String lowerReply = reply.toLowerCase();
            if (lowerReply.contains("raise a ticket") ||
                lowerReply.contains("file a ticket") ||
                lowerReply.contains("create a ticket") ||
                lowerReply.contains("submit a ticket")) {
                action = "redirect:/help/ticket";
            }

            return new AiChatResponse(reply, action);

        } catch (Exception e) {
            return new AiChatResponse("I'm having trouble connecting right now. Please try again in a moment.");
        }
    }

    private String buildSystemPrompt(Long userId) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("You are Cartify's friendly AI support assistant. ");
        prompt.append("Cartify is an e-commerce store selling sneakers, apparel, bags, watches, audio equipment, and eyewear. ");
        prompt.append("Keep your responses concise (2-3 sentences max) and helpful. ");
        prompt.append("You can help with: order status, tracking, returns, refunds, product questions, and general support. ");
        prompt.append("If the user needs to raise a support ticket, say 'You can raise a ticket' and I'll redirect them. ");
        prompt.append("Always be polite and professional. ");

        // Agar user logged in hai to uska real data inject karo
        if (userId != null) {
            try {
                // User ke recent orders
                List<Order> orders = orderRepository.findByUserIdOrderByPlacedAtDesc(userId);
                if (!orders.isEmpty()) {
                    prompt.append("\n\nUSER'S RECENT ORDERS (use this for order-related questions):\n");
                    orders.stream().limit(5).forEach(order -> {
                        prompt.append(String.format("- Order #%s: Status=%s, Total=₹%s, Date=%s\n",
                                order.getOrderNumber(),
                                order.getStatus().name(),
                                order.getTotal().toPlainString(),
                                order.getPlacedAt().toLocalDate()));
                    });
                }

                // User ke support tickets
                var tickets = supportTicketRepository.findByUserIdOrderByCreatedAtDesc(userId);
                if (!tickets.isEmpty()) {
                    prompt.append("\nUSER'S SUPPORT TICKETS:\n");
                    tickets.stream().limit(3).forEach(ticket -> {
                        prompt.append(String.format("- Ticket #%s: Subject=%s, Status=%s\n",
                                ticket.getTicketNumber(),
                                ticket.getSubject(),
                                ticket.getStatus().name()));
                    });
                }
            } catch (Exception e) {
                // Ignore — user data optional hai
            }
        }

        return prompt.toString();
    }
}