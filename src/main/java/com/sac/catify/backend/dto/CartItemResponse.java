package com.sac.catify.backend.dto;

import java.math.BigDecimal;

//Cart page (frontend CartPage.tsx) ko jo response chahiye — har cart item
//ke saath uska product ka LIVE data (current price, image, title, stock)
//bhi bhejte hain, kyunki jab tak order place nahi hota, price/stock
//change ho sakta hai aur cart mein hamesha LATEST value dikhni chahiye
//(isi wajah se Phase B2 mein humne CartItem entity mein Product ko
//SEEDHA reference kiya tha, OrderItem jaisa "snapshot" nahi banaya tha —
public class CartItemResponse {

	
	// Ye cart_items table ki row ki ID hai (is specific cart-entry ki ID) —
    // update/delete karte waqt isi ID se identify karenge
    private Long id;

    // Product ki info — "live" data hai, cart_items table mein store nahi
    // hai, har baar Product entity se fresh nikala jata hai
    private Long productId;
    private String title;
    private String image;
    private BigDecimal price;
    private Integer availableStock; // taaki frontend "only X left" jaisa dikha sake

    private Integer quantity;
    private String color;
    private String size;
    
    // Convenience field — price * quantity, taaki frontend ko khud
    // calculate na karna pade (chhota sa "server does the math" fayda)
    private BigDecimal lineTotal;

	public CartItemResponse() {
	
	}

	public CartItemResponse(Long id, Long productId, String title, String image, BigDecimal price,
			Integer availableStock, Integer quantity, String color, String size, BigDecimal lineTotal) {
		
		this.id = id;
		this.productId = productId;
		this.title = title;
		this.image = image;
		this.price = price;
		this.availableStock = availableStock;
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

	public Integer getAvailableStock() {
		return availableStock;
	}

	public void setAvailableStock(Integer availableStock) {
		this.availableStock = availableStock;
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
