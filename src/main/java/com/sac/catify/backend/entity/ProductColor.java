package com.sac.catify.backend.entity;

import jakarta.persistence.Embeddable;

//Kyun Embeddable: ProductSpecification jaisa hi pattern — 
//chhota "name + hex" pair, apna table/ID nahi chahiye,
//Product ke andar hi list ki tarah store hota hai. Frontend ke colors: 
//{name, hex}[] ka exact match.

// Chhota data-block: color ka naam ("Cobalt") + hex code ("#3454FF")
// Product mein list ki tarah use hoga: List<ProductColor>
@Embeddable
public class ProductColor {

    private String name;
    private String hex;

    public ProductColor() {
    }

    public ProductColor(String name, String hex) {
        this.name = name;
        this.hex = hex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHex() {
        return hex;
    }

    public void setHex(String hex) {
        this.hex = hex;
    }
}
