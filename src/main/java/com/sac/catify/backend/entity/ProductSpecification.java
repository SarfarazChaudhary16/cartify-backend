package com.sac.catify.backend.entity;

import jakarta.persistence.Embeddable;


//@Embeddable kyun bana rahe hain (na ki poori Entity): Ye ek chhota 
//"label + value" pair hai (jaise "Material: Leather", "Weight: 250g"). 
//Ye apne aap mein independent nahi hai — hamesha kisi Product ke andar hi 
//rehta hai, aur isse alag se access karne ki koi zarurat nahi (na koi controller, na koi ID). 
//Isliye ise poori @Entity (apni ID/table ke saath) banana overkill hoga. 
//@Embeddable ek "chhota reusable data block" hota hai jo kisi doosri entity ke andar embed
//(fit) ho jata hai — apna table nahi banata, Product ke saath hi ek combined table (product_specifications)
//mein store hota hai.


// @Embeddable batata hai: "ye ek chhota data-holder class hai, isse apni alag
// Primary Key/table nahi chahiye — ye hamesha kisi badi Entity ke andar use hogi."
// Frontend mein yaad hai Product ka specifications field tha: {label, value}[]
// (jaise "Material: Leather", "Weight: 250g") — yahi uska Java version hai.
@Embeddable
public class ProductSpecification {

    private String label; // jaise "Material"
    private String value; // jaise "Leather"

    public ProductSpecification() {
    }

    public ProductSpecification(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
