package com.sac.catify.backend.dto;


import java.math.BigDecimal;
/*
Ye class kya hai aur kyun bana: Jab order ka data frontend ko bhejenge, 
uske andar "items" ki ek list hogi (jo products order mein the). Har item 
ke liye humein OrderItem Entity (jo Phase B2 mein bana tha — jisme productId, 
title, image, price sab snapshot ki tarah store hote hain, kyunki order place 
hone ke baad wo details kabhi badalni nahi chahiye) ka ek "safe/clean" version 
chahiye jo JSON mein jaye. Isi kaam ke liye ye DTO hai.
*/

// OrderItem Entity ka response-shape version. Dhyan do: is class ke fields
// bilkul OrderItem Entity jaise hi lagte hain — lekin ye Entity NAHI hai,
// isi liye alag DTO banaya, taaki:
//   1. Entity ke internal JPA-relationship fields (jaise "order" field jo
//      wapas Order ko reference karta hai) frontend ko na jayein
//      (circular-reference se bachne ke liye — Order->items->order->items->... loop)
//   2. Agar kal Entity mein koi naya internal-only field add ho, DTO
//      automatically expose nahi karega jab tak hum khud na chahein
public class OrderItemResponse {

    private Long id;              // order_items table ki row-ID
    private Long productId;       // original product ka reference (agar product abhi bhi exist karta hai to link ke liye kaam aa sakta hai)
    private String title;         // order-time ka SNAPSHOT title (agar product baad mein rename ho jaye, ye WAISA hi rahega)
    private String image;         // order-time ka snapshot image
    private BigDecimal price;     // order-time ka snapshot price (per-unit)
    private Integer quantity;
    private String color;         // nullable — agar variant select kiya tha
    private String size;          // nullable
    private BigDecimal lineTotal; // price * quantity, hum khud calculate karke bhejte hain (frontend ko dobara maths nahi karni padti)

    public OrderItemResponse() {
    }

    public OrderItemResponse(Long id, Long productId, String title, String image, BigDecimal price,
                              Integer quantity, String color, String size, BigDecimal lineTotal) {
        this.id = id;
        this.productId = productId;
        this.title = title;
        this.image = image;
        this.price = price;
        this.quantity = quantity;
        this.color = color;
        this.size = size;
        this.lineTotal = lineTotal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }
}
