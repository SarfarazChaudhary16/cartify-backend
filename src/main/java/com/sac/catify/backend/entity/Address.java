package com.sac.catify.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Label jaise "Home", "Office", "Other" — frontend ke AddressesPage mein yehi options the
    private String label;

    private String fullName;

    private String line1;

    private String line2; // optional, isliye nullable allowed (koi @Column(nullable=false) nahi)

    private String city;

    private String state;

    private String pincode;

    private String phone;

    // Kya ye address user ka "default" address hai (checkout mein pehle se select hota hai)
    @Column(name = "is_default")
    private boolean isDefault;

    // ===== Relationship: Many Addresses belong to One User =====
    // @ManyToOne batata hai: "bahut saari Address rows ek hi User se related ho sakti hain"
    // fetch = LAZY matlab: jab tak hum explicitly address.getUser() na bulayein,
    // Hibernate user ka data database se load nahi karega (performance ke liye better,
    // warna har baar addresses fetch karte waqt unnecessary user data bhi load ho jata)
    @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn batata hai database mein iska foreign key column "user_id" naam se banega
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ===== Constructors =====

    public Address() {
    }

    public Address(String label, String fullName, String line1, String line2, String city,
                    String state, String pincode, String phone, boolean isDefault, User user) {
        this.label = label;
        this.fullName = fullName;
        this.line1 = line1;
        this.line2 = line2;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
        this.phone = phone;
        this.isDefault = isDefault;
        this.user = user;
    }

    // ===== Getters and Setters =====

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
