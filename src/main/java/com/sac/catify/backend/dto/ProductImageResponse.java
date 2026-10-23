package com.sac.catify.backend.dto;

//Kyun bana: ProductImage Entity hai (apni ID hoti hai), 
//lekin frontend ko response mein poori Entity (jisme Product 
//ka wapas reference bhi hota — circular reference ban sakta hai) 
//nahi bhejni. Isliye simple flat DTO.

//ProductImage entity ka "safe" response version — sirf wahi fields jo
//frontend ko chahiye (id, url, alt text). Entity ka "product" field (jo
//wapas Product ko reference karta hai) yahan include nahi kiya — warna
//JSON banate waqt "circular reference" problem aa sakti thi
//(Product -> images -> product -> images -> ... infinite loop).
public class ProductImageResponse {

 private Long id;
 private String url;
 private String alt;

 public ProductImageResponse() {
 }

 public ProductImageResponse(Long id, String url, String alt) {
     this.id = id;
     this.url = url;
     this.alt = alt;
 }

 public Long getId() {
     return id;
 }

 public void setId(Long id) {
     this.id = id;
 }

 public String getUrl() {
     return url;
 }

 public void setUrl(String url) {
     this.url = url;
 }

 public String getAlt() {
     return alt;
 }

 public void setAlt(String alt) {
     this.alt = alt;
 }
}
