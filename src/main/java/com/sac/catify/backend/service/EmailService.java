package com.sac.catify.backend.service;

//Email bhejne ka contract. Interface isliye taaki agar kal hum SMTP se
//hatke koi third-party email service (jaise SendGrid, AWS SES) use karna
//chahein, to sirf EmailServiceImpl badalni padegi, jahan-jahan EmailService
//use ho raha hai (AuthServiceImpl mein) wahan kuch touch nahi karna padega.
public interface EmailService {

 // OTP code ko premium-designed HTML email banake, diye gaye email address
 // pe bhejta hai. purpose ke hisaab se email ka content thoda change hoga
 // (jaise "Verify your email" vs "Reset your password")
 void sendOtpEmail(String toEmail, String otpCode, String purpose);
 
 void sendTicketStatusEmail(String toEmail, String userName, String ticketNumber, String subject, String status);
}
