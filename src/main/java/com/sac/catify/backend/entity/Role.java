package com.sac.catify.backend.entity;


//Enum = ek fixed, predefined set of constant values.
//Frontend mein TypeScript ka `role: 'customer' | 'admin'` yaad hai? Java mein iska
//equivalent Enum hota hai. Isse database mein sirf ye 2 hi values store ho sakti hain,
//koi typo ya galat value nahi ja sakti.
public enum Role {

	 CUSTOMER,
	    ADMIN
}
