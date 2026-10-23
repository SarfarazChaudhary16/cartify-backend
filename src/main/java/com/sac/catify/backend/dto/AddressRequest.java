package com.sac.catify.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

// Frontend AddressesPage.tsx ke Add/Edit Address modal se aane wala data
public class AddressRequest {

    @NotBlank(message = "Label is required")
    private String label;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Address line 1 is required")
    private String line1;

    private String line2; // optional

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    // @Pattern se hum regex check kar sakte hain — yahan "exactly 6 digits"
    // enforce kar rahe hain, jaise frontend mein bhi /^\d{6}$/ check tha
    @NotBlank(message = "Pincode is required")
    @Pattern(regexp = "^\\d{6}$", message = "Enter a valid 6-digit pincode")
    private String pincode;

    @NotBlank(message = "Phone number is required")
    private String phone;

    public AddressRequest() {
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
}
