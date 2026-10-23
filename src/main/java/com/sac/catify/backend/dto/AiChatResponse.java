package com.sac.catify.backend.dto;

public class AiChatResponse {

    private String reply;
    private String action; // optional — "redirect:/help/ticket" wagera

    public AiChatResponse() {}

    public AiChatResponse(String reply) {
        this.reply = reply;
    }

    public AiChatResponse(String reply, String action) {
        this.reply = reply;
        this.action = action;
    }

    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
}
