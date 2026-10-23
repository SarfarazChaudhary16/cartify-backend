package com.sac.catify.backend.entity;

//Kyun enum: Frontend mein OrderStatus type tha (pending | packed | shipped | ...) — 
//order ka status hamesha in 8 values mein se ek hi ho sakta hai, koi custom string nahi.
//Isliye enum, taaki galat status kabhi database mein na jaye, aur code mein switch statements
//bhi safely likh sakein.

//Order ka status — sirf yahi 8 valid values hain, exactly jaise frontend ka
//OrderStatus type tha. Admin panel ka "advance status" flow isi enum ke order pe based hoga.
public enum OrderStatus {
 PENDING,
 PACKED,
 SHIPPED,
 OUT_FOR_DELIVERY,
 DELIVERED,
 CANCELLED,
 RETURNED,
 REFUNDED
}
