package com.sac.catify.backend.dto;

import java.math.BigDecimal;

//Ye class kya hai: Ye DTO Admin Dashboard ke top-4 stat-cards (Total Revenue, Total Orders, 
//Total Customers, Total Products) ka data carry karta hai — saath mein ek genuine calculated 
//"delta" (percentage change) bhi, jo humne is mahine ke number ko pichle mahine ke number se 
//compare karke nikala hai (frontend mein ye pehle sirf HARDCODED mock numbers the — dashboardTrends: 
//{ revenue: 12.4, ... } — ab hum ye asal mein calculate karenge database se, jo backend ka real value-add hai).

// Admin Dashboard ke 4 stat-cards (Total Revenue, Total Orders, Total
// Customers, Total Products) ka data. Har metric ke saath uska "delta"
// bhi hai — matlab "is mahine ka number, PICHLE mahine ke number se
// KITNA PERCENT badla/ghata" — ye HARDCODED nahi hai (jaisa frontend
// mock-data mein tha), balki hum ise Service layer mein REAL orders/users
// ke createdAt/placedAt dates dekh ke calculate karenge.
public class DashboardStatsResponse {

    private BigDecimal totalRevenue;
    private double revenueDelta;    // jaise 12.4 (matlab +12.4%), ya -5.2 (matlab -5.2%)

    private long totalOrders;
    private double ordersDelta;

    private long totalCustomers;
    private double customersDelta;

    private long totalProducts;
    private double productsDelta;

    public DashboardStatsResponse() {
    }

    public DashboardStatsResponse(BigDecimal totalRevenue, double revenueDelta, long totalOrders,
                                   double ordersDelta, long totalCustomers, double customersDelta,
                                   long totalProducts, double productsDelta) {
        this.totalRevenue = totalRevenue;
        this.revenueDelta = revenueDelta;
        this.totalOrders = totalOrders;
        this.ordersDelta = ordersDelta;
        this.totalCustomers = totalCustomers;
        this.customersDelta = customersDelta;
        this.totalProducts = totalProducts;
        this.productsDelta = productsDelta;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public double getRevenueDelta() {
        return revenueDelta;
    }

    public void setRevenueDelta(double revenueDelta) {
        this.revenueDelta = revenueDelta;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public double getOrdersDelta() {
        return ordersDelta;
    }

    public void setOrdersDelta(double ordersDelta) {
        this.ordersDelta = ordersDelta;
    }

    public long getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(long totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public double getCustomersDelta() {
        return customersDelta;
    }

    public void setCustomersDelta(double customersDelta) {
        this.customersDelta = customersDelta;
    }

    public long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public double getProductsDelta() {
        return productsDelta;
    }

    public void setProductsDelta(double productsDelta) {
        this.productsDelta = productsDelta;
    }
}
