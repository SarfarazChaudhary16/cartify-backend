package com.sac.catify.backend.service.impl;

//OtpEmailTemplate jaisa hi pattern — dark header, brand-colors, table-based
//layout (email-clients ke liye zaroori hai). Isme status ke hisaab se
//heading/content badalta hai (In Progress vs Resolved).
public class TicketStatusEmailTemplate {

 public static String build(String userName, String ticketNumber, String subject, String status) {

     String heading;
     String message;
     String accentColor;

     if ("RESOLVED".equalsIgnoreCase(status)) {
         heading = "Your ticket has been resolved";
         message = "Good news! Our support team has resolved your ticket. If you still need help, feel free to raise a new ticket anytime.";
         accentColor = "#1F9D63"; // success-green
     } else {
         heading = "Your ticket is now in progress";
         message = "Our support team has started working on your ticket. We'll update you again as soon as there's progress.";
         accentColor = "#3454FF"; // cobalt
     }

     return "<!DOCTYPE html>" +
             "<html>" +
             "<head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'></head>" +
             "<body style='margin:0; padding:0; background-color:#FAF8F4; font-family:Arial, Helvetica, sans-serif;'>" +
             "<table role='presentation' width='100%' cellpadding='0' cellspacing='0' style='background-color:#FAF8F4; padding:40px 0;'>" +
             "<tr><td align='center'>" +
             "<table role='presentation' width='480' cellpadding='0' cellspacing='0' style='background-color:#FFFFFF; border-radius:16px; overflow:hidden; border:1px solid #E6E1D6;'>" +

             "<tr><td style='background-color:#0E0F13; padding:32px 40px; text-align:center;'>" +
             "<span style='font-size:22px; font-weight:700; color:#FFFFFF; letter-spacing:0.5px;'>Cartify</span>" +
             "</td></tr>" +

             "<tr><td style='padding:40px;'>" +
             "<p style='margin:0 0 8px 0; font-size:13px; color:#8B8F99;'>Hi " + userName + ",</p>" +
             "<h1 style='margin:0 0 12px 0; font-size:22px; color:#0E0F13; font-family:Georgia, serif;'>" + heading + "</h1>" +
             "<p style='margin:0 0 28px 0; font-size:14px; line-height:1.6; color:#5C6650;'>" + message + "</p>" +

             "<table role='presentation' width='100%' cellpadding='0' cellspacing='0'>" +
             "<tr><td style='background-color:#FAF8F4; border-left:4px solid " + accentColor + "; border-radius:8px; padding:16px 20px;'>" +
             "<p style='margin:0; font-size:12px; color:#8B8F99;'>TICKET</p>" +
             "<p style='margin:2px 0 0 0; font-size:14px; font-weight:600; color:#0E0F13;'>#" + ticketNumber + "</p>" +
             "<p style='margin:6px 0 0 0; font-size:13px; color:#5C6650;'>" + subject + "</p>" +
             "</td></tr>" +
             "</table>" +

             "<hr style='border:none; border-top:1px solid #E6E1D6; margin:28px 0;'>" +
             "<p style='margin:0; font-size:12px; line-height:1.6; color:#8B8F99;'>" +
             "You can view the full progress of this ticket anytime from your Cartify account under Help Center." +
             "</p>" +
             "</td></tr>" +

             "<tr><td style='background-color:#FAF8F4; padding:24px 40px; text-align:center; border-top:1px solid #E6E1D6;'>" +
             "<p style='margin:0; font-size:11px; color:#8B8F99;'>&copy; 2026 Cartify. All rights reserved.</p>" +
             "</td></tr>" +

             "</table></td></tr></table>" +
             "</body></html>";
 }
}
