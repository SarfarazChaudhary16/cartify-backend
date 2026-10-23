package com.sac.catify.backend.entity;

//Kyun Embeddable, alag Entity nahi: Review ke saath jo photos attach hoti hain 
//(WriteReviewModal mein yaad hai — image upload feature), unhe sirf URL ki tarah 
//store karna hai. Koi complex behavior nahi chahiye isse, isliye simple Embeddable 
//String-list jaisa treat kar rahe hain.

import jakarta.persistence.Embeddable;

// Review ke saath attach ki gayi image ka URL
@Embeddable
public class ReviewImage {

    private String url;

    public ReviewImage() {
    }

    public ReviewImage(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
