package com.sac.catify.backend.dto;

import java.math.BigDecimal;

// Revenue Chart ka EK point — "is din ka total revenue kitna tha".
// Service layer mein hum aisi 14 (ya jitne din chahiye) DTOs ki ek
// LIST banayenge, jo Chart component ko poori tarah render karne layak
// data degi.
public class RevenuePointResponse {

    private String date;         // jaise "10 Jul" (display-ready format)
    private BigDecimal revenue;

    public RevenuePointResponse() {
    }

    public RevenuePointResponse(String date, BigDecimal revenue) {
        this.date = date;
        this.revenue = revenue;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }
}
