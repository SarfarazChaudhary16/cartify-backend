package com.sac.catify.backend.dto;

//Address entity ka safe response version — Account > Addresses page
//(frontend AddressesPage.tsx) aur Checkout ke Address-selection step,
//dono isi shape ko use karenge.
public class AddressResponse {

	private Long id;
	private String label; // "Home", "Office", "Other"
	private String fullName;
	private String line1;
	private String line2;
	private String city;
	private String state;
	private String pincode;
	private String phone;
	private boolean isDefault;
	
	public AddressResponse() {
		
	}
	public AddressResponse(Long id, String label, String fullName, String line1, String line2, String city,
			String state, String pincode, String phone, boolean isDefault) {
		
		this.id = id;
		this.label = label;
		this.fullName = fullName;
		this.line1 = line1;
		this.line2 = line2;
		this.city = city;
		this.state = state;
		this.pincode = pincode;
		this.phone = phone;
		this.isDefault = isDefault;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
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
	public boolean isDefault() {
		return isDefault;
	}
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	
	
	
}
