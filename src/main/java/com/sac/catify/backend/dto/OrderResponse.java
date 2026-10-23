package com.sac.catify.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

//Ye "master" DTO hai is module ka — Order Detail page 
//(customer + admin dono ke) ko yehi poora shape chahiye: 
//order info + address + payment + items list + tracking timeline, 
//sab ek object mein.

// Ye poore Order ka "final assembled" response hai — Order Detail page
// (customer OrderDetailsPage.tsx aur admin AdminOrderDetailsPage.tsx dono)
// ko yehi shape milega. Ismein Order ki basic info, delivery address
// (SNAPSHOT wali — Address entity se link nahi, Phase B2 mein discuss
// kiya tha yaad hai?), items ki list, aur tracking timeline — sab combine
// hai, taaki frontend ko ek hi API call mein poora page render karne
// layak data mil jaye (bar-bar alag calls na karni padein).
public class OrderResponse {

    private Long id;
    private String orderNumber;
    private String status;          // OrderStatus enum ko String ki tarah bhejenge (jaise "PENDING")
    private String paymentMethod;
    private BigDecimal total;
    private LocalDateTime placedAt;
    
    private String couponCode;
    private BigDecimal discountAmount;

    // Address ke fields ko FLATTEN karke seedha yahan daal rahe hain
    // (OrderAddress Embeddable hai, alag DTO banane ki zarurat nahi —
    // seedha is class ke fields mein hi cover kar rahe hain, taaki
    // frontend ko "response.address.fullName" jaisa nested access mile,
    // uske liye neeche ek chhota inner-object bana rahe hain)
    private AddressSnapshot address;

    private List<OrderItemResponse> items;
    private List<TrackingStepResponse> trackingSteps;

    public OrderResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public LocalDateTime getPlacedAt() {
        return placedAt;
    }

    public void setPlacedAt(LocalDateTime placedAt) {
        this.placedAt = placedAt;
    }

    public AddressSnapshot getAddress() {
        return address;
    }

    public void setAddress(AddressSnapshot address) {
        this.address = address;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }

    public void setItems(List<OrderItemResponse> items) {
        this.items = items;
    }

    public List<TrackingStepResponse> getTrackingSteps() {
        return trackingSteps;
    }

    public void setTrackingSteps(List<TrackingStepResponse> trackingSteps) {
        this.trackingSteps = trackingSteps;
    }
    
    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public java.math.BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(java.math.BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    // ===================================================================
    // INNER STATIC CLASS — AddressSnapshot
    // ===================================================================
    // Ye ek chhota "nested" helper class hai, sirf OrderResponse ke andar
    // hi use hoti hai (BrandRequest ke andar ProductImageRequest jaisa
    // pattern tha, yaad hai?). Isse alag `.java` file banane ki zarurat
    // nahi thi kyunki ye sirf order-address dikhane ke kaam aati hai,
    // kahin aur reuse nahi hoti.
    public static class AddressSnapshot {
        private String fullName;
        private String line1;
        private String line2;
        private String city;
        private String state;
        private String pincode;
        private String phone;

        public AddressSnapshot() {
        }

        public AddressSnapshot(String fullName, String line1, String line2, String city,
                                String state, String pincode, String phone) {
            this.fullName = fullName;
            this.line1 = line1;
            this.line2 = line2;
            this.city = city;
            this.state = state;
            this.pincode = pincode;
            this.phone = phone;
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
}
