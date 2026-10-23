package com.sac.catify.backend.entity;


//Kyun enum: OTP do alag kaamon ke liye use hota hai frontend mein — 
//(1) Signup ke baad email verify karna, (2) Forgot Password flow. 
//Dono situations mein OTP ka process same hai (generate → email bhejo → 
//verify karo), lekin humein pata hona chahiye "ye OTP kis purpose ke liye bana tha" — 
//warna koi signup-verification wala OTP use karke password reset kar sakta tha 
//(security hole). Enum se ye clearly distinguish ho jata hai.


//OTP kis kaam ke liye generate hua hai — ye distinguish karna zaroori hai
//taaki ek context ka OTP dusre context mein "reuse" na ho sake (security ke liye).
//Frontend mein: VerifyOtpPage (signup ke baad) aur ForgotPasswordPage
//(password reset) — dono alag flows the, dono ko alag OTP purpose milega
public enum OtpPurpose {

	SIGNUP_VERIFICATION,
    PASSWORD_RESET
}
