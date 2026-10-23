package com.sac.catify.backend.service.impl;

import com.sac.catify.backend.dto.ContactRequest;
import com.sac.catify.backend.entity.ContactMessage;
import com.sac.catify.backend.repository.ContactMessageRepository;
import com.sac.catify.backend.service.ContactService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class ContactServiceImpl implements ContactService {

    private final ContactMessageRepository contactMessageRepository;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public ContactServiceImpl(ContactMessageRepository contactMessageRepository, JavaMailSender mailSender) {
        this.contactMessageRepository = contactMessageRepository;
        this.mailSender = mailSender;
    }

    @Override
    public void submitMessage(ContactRequest request) {
        // Step 1: Database mein save karte hain (future mein Admin ko
        // ye dekhne ka feature bhi ban sakta hai)
        ContactMessage message = new ContactMessage(
                request.getName(), request.getEmail(), request.getSubject(), request.getMessage());
        contactMessageRepository.save(message);

        // Step 2: User ko ek SIMPLE confirmation-email bhejte hain
        // (SimpleMailMessage use kar rahe hain — OTP jaisa fancy HTML
        // template nahi chahiye yahan, plain-text confirmation kaafi hai)
        try {
            SimpleMailMessage confirmationEmail = new SimpleMailMessage();
            confirmationEmail.setFrom(fromEmail);
            confirmationEmail.setTo(request.getEmail());
            confirmationEmail.setSubject("We've received your message — Cartify");
            confirmationEmail.setText(
                    "Hi " + request.getName() + ",\n\n" +
                    "Thanks for reaching out! We've received your message regarding \"" + request.getSubject() + "\" " +
                    "and our team will get back to you within 24-48 hours.\n\n" +
                    "— Team Cartify"
            );
            mailSender.send(confirmationEmail);
        } catch (Exception e) {
            // Agar email-bhejne mein koi issue aaye (jaise network-glitch),
            // hum ise SILENTLY ignore kar dete hain — message DATABASE mein
            // to save ho hi chuka hai (Step 1), confirmation-email fail
            // hone se poori request FAIL nahi honi chahiye, kyunki asal
            // "submission" already successful ho chuki hai
        }
    }
}
