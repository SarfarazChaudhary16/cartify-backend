package com.sac.catify.backend.dto;

import java.math.BigDecimal;

// Reports > Sales tab ke "Sales by Category" chart ke liye — har
// category ka total revenue aur units-sold
public class CategorySalesResponse {

    private String categoryName;
    private BigDecimal revenue;
    private long unitsSold;

    public CategorySalesResponse() {
    }

    public CategorySalesResponse(String categoryName, BigDecimal revenue, long unitsSold) {
        this.categoryName = categoryName;
        this.revenue = revenue;
        this.unitsSold = unitsSold;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public long getUnitsSold() {
        return unitsSold;
    }

    public void setUnitsSold(long unitsSold) {
        this.unitsSold = unitsSold;
    }
}
