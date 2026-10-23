package com.sac.catify.backend.dto;

//Brand entity ka safe response version — Category jaisa hi simple structure,
//bas "description" extra field hai
public class BrandResponse {

 private Long id;
 private String name;
 private String slug;
 private String logo;
 private String description;

 public BrandResponse() {
 }

 public BrandResponse(Long id, String name, String slug, String logo, String description) {
     this.id = id;
     this.name = name;
     this.slug = slug;
     this.logo = logo;
     this.description = description;
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

 public String getLogo() {
     return logo;
 }

 public void setLogo(String logo) {
     this.logo = logo;
 }

 public String getDescription() {
     return description;
 }

 public void setDescription(String description) {
     this.description = description;
 }
}
