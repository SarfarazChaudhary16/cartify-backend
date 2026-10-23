package com.sac.catify.backend.service;

import com.sac.catify.backend.dto.ContactRequest;

public interface ContactService {
	
    void submitMessage(ContactRequest request);
}
