package com.sac.catify.backend.service;

import com.sac.catify.backend.dto.AiChatRequest;
import com.sac.catify.backend.dto.AiChatResponse;

public interface AiChatService {
	
    AiChatResponse chat(AiChatRequest request);
}
