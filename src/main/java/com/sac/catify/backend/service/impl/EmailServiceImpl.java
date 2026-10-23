package com.sac.catify.backend.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.sac.catify.backend.exception.ApiException;
import com.sac.catify.backend.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService{

	// JavaMailSender Spring Boot khud provide karta hai jab humne
    // spring-boot-starter-mail dependency add ki aur application.properties
    // mein SMTP settings dein — Spring automatically ek JavaMailSender
    // Bean bana ke rakh deta hai, hume khud banana nahi padta
    private final JavaMailSender mailSender;
	
    // @Value annotation application.properties se ek value utha ke is field
    // mein daal deta hai. "spring.mail.username" wahi hai jo humne
    // properties file mein apna Gmail address likha tha — isse hum
    // "from" address ki tarah reuse kar rahe hain, hardcode nahi kar rahe
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
	
	@Override
	public void sendOtpEmail(String toEmail, String otpCode, String purpose) {


		try {
			
			// Simple email (SimpleMailMessage) sirf PLAIN TEXT bhej sakta hai.
            // Humein HTML (premium design) bhejna hai, isliye MimeMessage
            // use karte hain — ye zyada powerful hai, HTML content, images,
            // attachments sab support karta hai.
            MimeMessage message = mailSender.createMimeMessage();
            
            // MimeMessageHelper ek utility class hai jo MimeMessage banane
            // ka process aasan bana deti hai (subject/body/from set karna,
            // warna raw MimeMessage API bahut verbose/complicated hai)
            //
            // "true" second parameter batata hai: "multipart support chahiye"
            // (future mein attachments bhejne ho to kaam aayega, abhi zarurat
            // nahi lekin harmless hai rakhna)
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            
           // Subject line bhi purpose ke hisaab se badal rahe hain
            String subject = "SIGNUP_VERIFICATION".equals(purpose)
                    ? "Verify your Cartify account"
                    : "Reset your Cartify password";
            helper.setSubject(subject);
            
           // OtpEmailTemplate se poora HTML content generate karwa ke lete hain
            String htmlContent = OtpEmailTemplate.build(otpCode, purpose);
            
            // setText(content, true) -> "true" batata hai ki ye content HTML
            // hai (na ki plain text), taaki email client isse render kare
            // formatting ke saath, raw tags dikhane ke bajaye
            helper.setText(htmlContent, true);
            
            // Ab actual email bhejte hain — ye line SMTP server (Gmail) se
            // connect karke real email deliver karti hai
            mailSender.send(message);
			
		} catch(MessagingException e) {
			
			// TEMPORARY: error print karo debug ke liye
		    System.err.println("EMAIL SEND FAILED: " + e.getMessage());
		    e.printStackTrace();
			// Agar kisi wajah se email bhejne mein dikkat aaye (jaise galat
            // SMTP credentials, network issue), to hum ise ek RuntimeException
            // mein "wrap" karke throw kar dete hain. Java mein MessagingException
            // ek "checked exception" hai (matlab har jagah try-catch likhna
            // FORCE hota), lekin humare baaki code mein RuntimeException hi
            // consistently use ho raha hai, isliye yahan bhi convert kar rahe hain.
			throw new ApiException("Failed to send OTP email: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, e);
		}
		
	}
	
	@Override
    public void sendTicketStatusEmail(String toEmail, String userName, String ticketNumber, String subject, String status) {
        try {
            jakarta.mail.internet.MimeMessage message = mailSender.createMimeMessage();
            org.springframework.mail.javamail.MimeMessageHelper helper = new org.springframework.mail.javamail.MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("RESOLVED".equalsIgnoreCase(status)
                    ? "Your ticket #" + ticketNumber + " has been resolved"
                    : "Update on your ticket #" + ticketNumber);

            String htmlContent = TicketStatusEmailTemplate.build(userName, ticketNumber, subject, status);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (jakarta.mail.MessagingException e) {
            // Email-failure poori request ko FAIL nahi karni chahiye —
            // status-update to already database mein save ho chuka hai,
            // email sirf ek "nice-to-have" notification hai
        }
    }

}
