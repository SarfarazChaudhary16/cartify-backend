package com.sac.catify.backend.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.sac.catify.backend.entity.OtpPurpose;
import com.sac.catify.backend.entity.OtpToken;

public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {

	
	// Jab user OTP submit karega, hume us email + code + purpose ka
    // EXACT match dhoondhna hai. Teeno conditions "And" se judi hain method
    // naam mein — Spring khud samajh jata hai: "WHERE email=? AND code=? AND purpose=?"
    //
    // Method naam thoda lamba hai lekin bilkul clear hai ki kya query banegi:
    // findBy + Email + And + Code + And + Purpose
    Optional<OtpToken> findByEmailAndCodeAndPurpose(String email, String code, OtpPurpose purpose);
    
    
    // "Resend Code" feature ke liye — agar user dobara OTP maange, hum sabse
    // NAYA (latest) wala OTP dhoondhna chahenge us email+purpose ke liye
    // "OrderByCreatedAtDesc" + "First" -> sabse pehla result do jab
    // createdAt ke hisaab se descending (naye-se-purane) sort kiya jaye,
    // matlab EFFECTIVELY "sabse latest wala ek record do"
    Optional<OtpToken> findFirstByEmailAndPurposeOrderByCreatedAtDesc(String email, OtpPurpose purpose);
    
 // Jab OTP ka kaam poora ho jaye (signup verify ho gaya, ya password
    // reset ho gaya), us email+purpose ke SAARE OTP rows ek saath delete
    // kar dete hain — sirf "abhi jo use hua" wahi nahi, balki agar user ne
    // "Resend Code" bhi kiya tha (matlab multiple OTP rows ban gayi thi
    // usi email/purpose ke liye), sabko साफ kar dete hain, kyunki ab
    // sabki zarurat khatam ho chuki hai.
    @Transactional
    void deleteByEmailAndPurpose(String email, OtpPurpose purpose);

    // Scheduled cleanup job ke liye — "jitni bhi OTP rows expire ho chuki
    // hain (expiresAt us diye gaye time se pehle ka hai), sabko delete karo".
    // Ye un OTPs ko pakड़ega jo generate hui thi lekin user ne kabhi verify
    // hi nahi kiya (jaise beech mein tab band kar diya) — aisi "abandoned"
    // OTPs hamesha ke liye database mein padi na rahein.
    @Transactional
    void deleteByExpiresAtBefore(LocalDateTime time);
}
