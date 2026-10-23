package com.sac.catify.backend.security;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

//JwtUtil is a utility/helper class responsible for creating, reading, verifying, 
//and validating JWT tokens.

//@Component ek generic annotation hai (jaise @Service, @Repository) jo
//Spring ko batata hai "is class ka ek object bana ke apne container mein
//rakh lo". Hum @Service isliye nahi likhte kyunki ye class "business logic"
//nahi hai (jaise Product/User ka logic), ye ek "utility/helper" class hai
//jo security-related low-level kaam karti hai — @Component zyada accurate naam hai.
@Component
public class JwtUtil {

	
	// application.properties se "jwt.secret" value le rahe hain (jo humne
    // abhi define ki). @Value ye kaam karta hai — property file ki value
    // ko field mein automatically inject kar deta hai
    @Value("${jwt.secret}")
    private String secretKey;
    
//  @Value injects values from the application.properties file into Java variables.
    @Value("${jwt.expiration.ms}")
    private long expirationMs;
    
 // ===================================================================
    // TOKEN GENERATE KARNA
    // ===================================================================

//    Login successful hone ke baad ye method ek secure token banata hai jo 
//    user ki identity ko represent karta hai.
    
    // Ye method ek naya JWT token banata hai, jisme email aur role "embed"
    // (chhupa hua/encoded) hote hain. Ye token hi user ke agle har API call
    // ke saath jayega (jaise browser cookie ki tarah, lekin header mein).
    public String generateToken(String email, String role) {
        // getSigningKey() se hamari secret key ko crypto-ready format mein lete hain
        SecretKey key = getSigningKey();

        // Jwts.builder() — JWT library ka fluent/chain-style API hai token
        // banane ke liye. Har ".xyz()" call ek property set karta hai:
        return Jwts.builder()
                // subject = token kis ke baare mein hai — hum yahan EMAIL
                // daal rahe hain, kyunki har user ka email unique hota hai,
                // ye humara "primary identifier" hai token ke andar
                .subject(email)

                // claim() se koi bhi EXTRA custom data token ke andar daal
                // sakte hain. Hum "role" daal rahe hain (CUSTOMER ya ADMIN)
                // taaki jab hum token verify karein, hume database dobara
                // query kiye bina hi pata chal jaye ki ye user admin hai ya nahi —
                // ye ek BADA PERFORMANCE FAYDA hai (baar-baar DB call nahi karni padti)
                .claim("role", role)

                // issuedAt = token kab bana (current time)
                .issuedAt(new Date())

                // expiration = token kab expire hoga. new Date() (abhi ka time)
                // + expirationMs (24 ghante ka milliseconds) = 24 ghante baad ki date
                .expiration(new Date(System.currentTimeMillis() + expirationMs))

                // signWith() — YE SABSE IMPORTANT STEP HAI:
                // token ko hamari secret key se "sign" (cryptographically lock)
                // kar deta hai. Isse ek "signature" ban jati hai jo token ke
                // end mein attach hoti hai. Agar koi is token ke andar ka data
                // (jaise role ko CUSTOMER se ADMIN) tamper/badal de, to
                // signature match nahi karegi aur verify karte waqt ye
                // pakda jayega — ye JWT ki security ka core concept hai.
                .signWith(key)

                // compact() — sab kuch combine karke final token STRING
                // banata hai, jo dikhta hai kuch aisa:
                // "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGVtYWlsLmNvbSJ9.4f8h2..."
                // (3 parts, dot se separated: Header.Payload.Signature)
                .compact();
    } 

//    Login ke baad JWT ban gaya.
//    Suppose Token
//
//    eyJhbGc.....
//    eyJzdWIiOiJzYW1AZ21haWwuY29tIiwicm9sZSI6IlVTRVIifQ
//    abcxyz....
//
//    Ab question
//    Server ko email kaise milega?
//    Role kaise milega?
//    Expiry kaise milegi?
//    Iske liye hi ye methods bane hain.
    
    // ===================================================================
    // TOKEN SE DATA NIKALNA (Extract karna)
    // ===================================================================
	
    
//    JWT ke andar stored email ko return karta hai.
    // Token ke andar se email (subject) nikalta hai
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    
//    JWT ke andar stored role return karta hai.
   // Token ke andar se "role" custom claim nikalta hai
    public String extractRole(String token) {
        // get("role", String.class) -> claims map se "role" key ki value
        // nikalta hai aur use String type mein cast karta hai
        return extractClaim(token, claims -> claims.get("role", String.class));
    }
    
    // Token expire ho chuka hai ya nahi, ye check karta hai
    public boolean isTokenExpired(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        // Agar expiration date "abhi" se PEHLE hai, matlab token expire ho chuka hai
        return expiration.before(new Date());
    }
    
 // Ye ek GENERIC helper method hai — isse hum "kuch bhi" nikal sakte hain
    // token ke andar se, bina baar-baar wahi parsing-code likhe.
    //
    // Function<Claims, T> ek Java "functional interface" hai — matlab hum
    // ismein ek chhota function pass kar sakte hain (jaise upar
    // "Claims::getSubject" ya "claims -> claims.get(...)"), aur ye method
    // us function ko Claims object pe apply kar dega.
    //
    // <T> generic type hai — matlab ye method KISI BHI type ka result
    // return kar sakta hai (String ho, Date ho, waghera) depend karta hai
    // hum kya function pass karte hain
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
//    Ye method JWT Token ko verify karta hai aur 
//    uske andar ka sara data (Claims) nikalta hai.  
    
    // Token ko "parse" (decode + verify signature) karke uske andar ke
    // saare claims (email, role, expiry, waghera) ek Claims object mein nikalta hai
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                // verifyWith() — bata rahe hain "isi secret key se signature
                // verify karo". Agar koi FAKE token bheje (jo hamari key se
                // sign nahi hua), ye line yahi EXCEPTION throw kar degi —
                // isliye humein alag se manually check karne ki zarurat
                // nahi padti "kya ye genuine token hai"
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
 // ===================================================================
    // TOKEN VALIDATE KARNA (poora check — genuine hai + expire nahi hua)
    // ===================================================================

    public boolean isTokenValid(String token, String email) {
        try {
            String extractedEmail = extractEmail(token);
            // Do conditions check kar rahe hain:
            // 1. Token ke andar ka email, jo email humein diya gaya hai
            //    (jaise current logged-in user ka email) usse match karta hai?
            // 2. Token abhi bhi valid hai (expire nahi hua)?
            // Dono true hon, tabhi token "valid" mana jayega
            return extractedEmail.equals(email) && !isTokenExpired(token);
        } catch (Exception e) {
            // Agar token corrupt/tampered/malformed hai, to extractEmail()
            // ya extractAllClaims() khud hi exception throw kar denge
            // (JWT library ke andar se). Hum yahan use pakड़ ke seedha
            // "false" return kar dete hain — matlab "ye token invalid hai",
            // bina poori application ko crash kiye
            return false;
        }
    }
    
    // ===================================================================
    // PRIVATE HELPER
    // ===================================================================

//    Ye method application.properties me stored String secret key ko 
//    JWT library ke required SecretKey format me convert karta hai.
    
    // Hamari secret key (jo properties file mein plain String hai) ko
    // JWT library ke required format (SecretKey object) mein convert karta hai
    private SecretKey getSigningKey() {
        // Keys.hmacShaKeyFor() String ko bytes mein convert karke ek
        // proper cryptographic key object banata hai jo signing/verifying
        // ke liye use ho sakta hai
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
