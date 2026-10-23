package com.sac.catify.backend.dto;

//Ek generic "success message" response — jaise "OTP sent to your email",
//"Password reset successful" waghera. Jahan bhi hume sirf ek confirmation
//message bhejna hai (poora User/Order object nahi), wahan ye reuse hoga.
public class MessageResponse {

 private String message;

 public MessageResponse() {
 }

 public MessageResponse(String message) {
     this.message = message;
 }

 public String getMessage() {
     return message;
 }

 public void setMessage(String message) {
     this.message = message;
 }
}
