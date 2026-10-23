package com.sac.catify.backend.service.impl;

//Ye class sirf ek kaam karti hai: OTP email ka HTML content (string ki tarah)
//generate karna. Isse EmailServiceImpl se alag rakha hai taaki "design/HTML"
//aur "email bhejne ka logic" mixed na ho — clean separation of concerns.
public class OtpEmailTemplate {

 // Static method hai kyunki isko object banane ki zarurat nahi —
 // seedha OtpEmailTemplate.build(...) ki tarah call kar sakte hain
 public static String build(String otpCode, String purpose) {

     // purpose ke hisaab se heading aur subtext badalte hain
     // (Signup verification vs Password reset ke liye alag tone)
     String heading;
     String subtext;

     if ("SIGNUP_VERIFICATION".equals(purpose)) {
         heading = "Verify your email";
         subtext = "Welcome to Cartify! Use the code below to verify your email address and activate your account.";
     } else {
         heading = "Reset your password";
         subtext = "We received a request to reset your password. Use the code below to continue.";
     }

     // ===================================================================
     // HTML Email Template — Cartify brand colors ke saath
     // (ink-950 #0E0F13, porcelain-100 #FAF8F4, cobalt-500 #3454FF, gold-500 #C9A24B)
     //
     // IMPORTANT: Email clients (Gmail/Outlook) modern CSS (Flexbox, Grid)
     // support NAHI karte. Isliye poora layout <table> tags se banaya gaya
     // hai (jo purana lagta hai lekin email development mein ye hi
     // industry-standard/mandatory tareeka hai), aur saari styling
     // "inline" (style="...") likhi hai — kyunki bahut saare email clients
     // <style> block wali external/internal CSS ko bhi strip kar dete hain.
     // ===================================================================

     return "<!DOCTYPE html>" +
             "<html>" +
             "<head>" +
             "<meta charset='UTF-8'>" +
             "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
             "</head>" +
             // body ka background halka off-white (porcelain) rakha hai,
             // jaisa frontend ka overall theme hai
             "<body style='margin:0; padding:0; background-color:#FAF8F4; font-family:Arial, Helvetica, sans-serif;'>" +

             // Outer table — poori email ko center mein rakhta hai aur
             // background color deta hai (email clients mein <body> ka
             // background hamesha reliably nahi dikhta, isliye table
             // ke andar bhi background repeat kiya hai, ye common practice hai)
             "<table role='presentation' width='100%' cellpadding='0' cellspacing='0' style='background-color:#FAF8F4; padding:40px 0;'>" +
             "<tr><td align='center'>" +

             // Inner card — 480px chauda, white background, rounded corners,
             // halka shadow jaisa border — jaise frontend ke "surface-card" class jaisa
             "<table role='presentation' width='480' cellpadding='0' cellspacing='0' style='background-color:#FFFFFF; border-radius:16px; overflow:hidden; border:1px solid #E6E1D6;'>" +

             // ===== Header band (dark, jaise frontend ka ink-950 header) =====
             "<tr><td style='background-color:#0E0F13; padding:32px 40px; text-align:center;'>" +
             "<span style='font-size:22px; font-weight:700; color:#FFFFFF; letter-spacing:0.5px;'>Cartify</span>" +
             "</td></tr>" +

             // ===== Body content =====
             "<tr><td style='padding:40px;'>" +

             // Heading
             "<h1 style='margin:0 0 12px 0; font-size:22px; color:#0E0F13; font-family:Georgia, serif;'>" + heading + "</h1>" +

             // Subtext
             "<p style='margin:0 0 32px 0; font-size:14px; line-height:1.6; color:#5C6650;'>" + subtext + "</p>" +

             // ===== OTP Code Box — sabse important hissa, bada aur clearly visible =====
             "<table role='presentation' width='100%' cellpadding='0' cellspacing='0'>" +
             "<tr><td align='center' style='background-color:#FAF8F4; border:1px dashed #C9A24B; border-radius:12px; padding:24px;'>" +
             "<span style='font-size:36px; font-weight:700; letter-spacing:10px; color:#3454FF; font-family:\"Courier New\", monospace;'>" +
             otpCode +
             "</span>" +
             "</td></tr>" +
             "</table>" +

             // Expiry note
             "<p style='margin:24px 0 0 0; font-size:13px; color:#8B8F99; text-align:center;'>" +
             "This code will expire in 5 minutes." +
             "</p>" +

             // Divider line
             "<hr style='border:none; border-top:1px solid #E6E1D6; margin:32px 0;'>" +

             // Security note
             "<p style='margin:0; font-size:12px; line-height:1.6; color:#8B8F99;'>" +
             "If you didn't request this code, you can safely ignore this email — no changes will be made to your account." +
             "</p>" +

             "</td></tr>" +

             // ===== Footer =====
             "<tr><td style='background-color:#FAF8F4; padding:24px 40px; text-align:center; border-top:1px solid #E6E1D6;'>" +
             "<p style='margin:0; font-size:11px; color:#8B8F99;'>&copy; 2026 Cartify. All rights reserved.</p>" +
             "</td></tr>" +

             "</table>" + // inner card end
             "</td></tr>" +
             "</table>" + // outer table end

             "</body></html>";
 }
}
