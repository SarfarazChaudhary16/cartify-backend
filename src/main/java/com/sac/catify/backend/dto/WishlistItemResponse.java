package com.sac.catify.backend.dto;

import java.math.BigDecimal;

//Wishlist page (frontend WishlistPage.tsx) ko chahiye: product ka live
//data (title, image, price, rating) — jaise Cart mein tha, wahi pattern.
//Wishlist mein quantity/color/size jaisi cheezein NAHI hoti (Cart se
//isi liye simpler hai) — bas "kaunsa product save kiya hai" itna kaafi hai.
public class WishlistItemResponse {

	
	private Long id;  // wishlist_items table ki row ki ID
	private Long productid;
	private String title;
	private String image;
	
	private BigDecimal price;
	private Double rating;
	private Integer stock;
	
	public WishlistItemResponse() {
		
	}

	public WishlistItemResponse(Long id, Long productid, String title, String image, BigDecimal price, Double rating,
			Integer stock) {
		
		this.id = id;
		this.productid = productid;
		this.title = title;
		this.image = image;
		this.price = price;
		this.rating = rating;
		this.stock = stock;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getProductid() {
		return productid;
	}

	public void setProductid(Long productid) {
		this.productid = productid;
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

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}
	
	
}
