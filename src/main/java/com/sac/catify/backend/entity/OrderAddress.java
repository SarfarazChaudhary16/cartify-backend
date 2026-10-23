package com.sac.catify.backend.entity;


//Ye important hai, dhyan se padho: Ye Address entity jaisa hi lagta hai, lekin jaan-boojh kar 
//alag hai. Socho: agar order ka address seedha Address entity ko reference (Foreign Key) kare, 
//aur baad mein user apna address edit ya delete kar de — to purane order ka delivery address 
//bhi badal jayega ya crash ho jayega! Ye galat hai — ek baar order place ho gaya, uska address us 
//waqt ka snapshot hona chahiye, permanently frozen. Isliye hum Address ko reference nahi karte, 
//balki uski copy (Embeddable) Order ke andar hi store kar dete hain.


//Ye Address entity ki "snapshot copy" hai — order place hote waqt jo bhi address tha,
//uski details yahan permanently copy ho jati hain. Agar user baad mein apna saved
//Address edit/delete kare, to purane orders ka delivery address IMPACT nahi hoga
//(kyunki wo Address ko reference nahi kar raha, apni khud ki copy rakh raha hai).
//Isiliye ye Address entity se link (Foreign Key) nahi hai, ek independent Embeddable hai.


@jakarta.persistence.Embeddable
public class OrderAddress {

 private String fullName;
 private String line1;
 private String line2;
 private String city;
 private String state;
 private String pincode;
 private String phone;

 public OrderAddress() {
 }

 public OrderAddress(String fullName, String line1, String line2, String city,
                      String state, String pincode, String phone) {
     this.fullName = fullName;
     this.line1 = line1;
     this.line2 = line2;
     this.city = city;
     this.state = state;
     this.pincode = pincode;
     this.phone = phone;
 }

 public String getFullName() {
     return fullName;
 }

 public void setFullName(String fullName) {
     this.fullName = fullName;
 }

 public String getLine1() {
     return line1;
 }

 public void setLine1(String line1) {
     this.line1 = line1;
 }

 public String getLine2() {
     return line2;
 }

 public void setLine2(String line2) {
     this.line2 = line2;
 }

 public String getCity() {
     return city;
 }

 public void setCity(String city) {
     this.city = city;
 }

 public String getState() {
     return state;
 }

 public void setState(String state) {
     this.state = state;
 }

 public String getPincode() {
     return pincode;
 }

 public void setPincode(String pincode) {
     this.pincode = pincode;
 }

 public String getPhone() {
     return phone;
 }

 public void setPhone(String phone) {
     this.phone = phone;
 }
}
