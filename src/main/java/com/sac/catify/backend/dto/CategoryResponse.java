package com.sac.catify.backend.dto;


//Category entity ka safe response version. Category khud simple hai
//(Product jaisi nested images/variants nahi), isliye ye DTO bhi chhota hai.
public class CategoryResponse {

 private Long id;
 private String name;
 private String slug;
 private String image;

 // Frontend ke AdminCategoriesPage mein "productCount" dikhta tha (jaise
 // "24 products in this category"). Yaad hai Phase B2 mein humne discuss kiya tha
 // ki hum ise database mein STORE nahi karenge, balki REAL-TIME count nikalenge?
 // Yahi wo field hai — Service layer mein query se calculate hoga.
 private long productCount;

 public CategoryResponse() {
 }

 public CategoryResponse(Long id, String name, String slug, String image, long productCount) {
     this.id = id;
     this.name = name;
     this.slug = slug;
     this.image = image;
     this.productCount = productCount;
 }

 public Long getId() {
     return id;
 }

 public void setId(Long id) {
     this.id = id;
 }

 public String getName() {
     return name;
 }

 public void setName(String name) {
     this.name = name;
 }

 public String getSlug() {
     return slug;
 }

 public void setSlug(String slug) {
     this.slug = slug;
 }

 public String getImage() {
     return image;
 }

 public void setImage(String image) {
     this.image = image;
 }

 public long getProductCount() {
     return productCount;
 }

 public void setProductCount(long productCount) {
     this.productCount = productCount;
 }
}
