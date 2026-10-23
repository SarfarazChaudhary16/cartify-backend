package com.sac.catify.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;


//Ye bhi important concept hai (Address jaisa hi): Order ke andar jo products hote hain, unka 
//snapshot store karte hain — seedha Product entity reference nahi karte. Kyun? Kyunki agar 
//order place hone ke baad admin us product ka price/title change kar de, ya product 		hi delete kar de, to purane order ka record kharab nahi hona chahiye 
//("customer ne kya order kiya tha" hamesha fix rehna chahiye, jo bhi us waqt hua tha). 
//Isliye title, image, price sab yahan copy karke store karte hain, bas productId reference
//rakhte hain taaki chahein to original product ka link bhi mil sake.


// Order ke andar ek single item/product ka snapshot.
// Product entity ko seedha reference nahi karte (sirf productId store karte hain)
// kyunki agar product ka price/title baad mein badal jaye, order history
// wahi purani details dikhani chahiye jo order place hote waqt thi.
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Sirf ID reference — original product ka link, lekin display data yahan copy hai
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private String title; // order-time snapshot ka title

    private String image; // order-time snapshot ki image

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price; // order-time snapshot ka price

    @Column(nullable = false)
    private Integer quantity;

    private String color; // nullable, agar variant select kiya tha

    private String size; // nullable

    // ===== Many Items belong to One Order =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    public OrderItem() {
    }

    public OrderItem(Long productId, String title, String image, BigDecimal price,
                      Integer quantity, String color, String size, Order order) {
        this.productId = productId;
        this.title = title;
        this.image = image;
        this.price = price;
        this.quantity = quantity;
        this.color = color;
        this.size = size;
        this.order = order;
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

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
