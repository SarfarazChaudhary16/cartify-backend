package com.sac.catify.backend.dto;

//ProductVariant entity ka safe response version — same reason jaisa ProductImageResponse
public class ProductVariantResponse {

 private Long id;
 private String color;
 private String size;
 private String sku;
 private Integer stock;

 public ProductVariantResponse() {
 }

 public ProductVariantResponse(Long id, String color, String size, String sku, Integer stock) {
     this.id = id;
     this.color = color;
     this.size = size;
     this.sku = sku;
     this.stock = stock;
 }

 public Long getId() {
     return id;
 }

 public void setId(Long id) {
     this.id = id;
 }

 public String getColor() {
     return color;
 }

 public void setColor(String color) {
     this.color = color;
 }

 public String getSize() {
     return size;
 }

 public void setSize(String size) {
     this.size = size;
 }

 public String getSku() {
     return sku;
 }

 public void setSku(String sku) {
     this.sku = sku;
 }

 public Integer getStock() {
     return stock;
 }

 public void setStock(Integer stock) {
     this.stock = stock;
 }
}
