package com.sac.catify.backend.dto;

import java.util.List;

public class AiChatRequest {

    // Poora conversation history bhejte hain (user + assistant messages)
    // taaki AI ko context pata rahe ki pehle kya baat hui thi
    private List<ChatMessage> messages;
    private Long userId; // optional — agar logged in hai to uska data inject karenge

    public static class ChatMessage {
        private String role; // "user" ya "assistant"
        private String content;

        public ChatMessage() {}

        public ChatMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    public List<ChatMessage> getMessages() { return messages; }
    public void setMessages(List<ChatMessage> messages) { this.messages = messages; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
