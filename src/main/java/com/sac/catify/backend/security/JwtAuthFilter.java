package com.sac.catify.backend.security;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
//Kyun bana: Ye "gatekeeper" hai — HAR SINGLE incoming HTTP request pehle isse guzarti hai. 
//Ye check karta hai "kya is request ke saath valid JWT token hai", aur agar hai to Spring 
//Security ko batata hai "ye user authenticated hai, aage jaane do".

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }
    
    
    // Ye method HAR request ke liye automatically call hota hai
    // (Spring khud isse invoke karta hai, hum kahin se manually nahi call krte hain.)
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain)
            throws ServletException, IOException {
    	
    	
    	// Step 1: Request ke header se "Authorization" nikalte hain.
        // Frontend ise "Authorization: Bearer eyJhbGc..." format mein bhejta hai
        final String authHeader = request.getHeader("Authorization");
        
        // Step 2: Agar header hi nahi hai, ya "Bearer " se shuru nahi hota,
        // to iska matlab ye request "unauthenticated" hai (guest/anonymous
        // user hai). Hum yahan koi error NAHI dete — bas filter chain ko
        // aage badhne dete hain (filterChain.doFilter). Kyun? Kyunki ho
        // sakta hai ye request kisi PUBLIC endpoint (jaise /api/products)
        // ke liye ho, jise login ki zarurat hi nahi. Agar endpoint
        // PROTECTED hai aur token nahi mila, to SecurityConfig ke rules
        // (jo hum agli file mein likhenge) khud aage jaake use reject kar denge.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Step 3: "Bearer " (7 characters) hata ke sirf actual TOKEN nikalte hain
        final String token = authHeader.substring(7);
        
        try {
        	
        	// Step 4: Token ke andar se email nikalte hain
            String email = jwtUtil.extractEmail(token);
            
            // Step 5: Agar email mil gaya, AUR abhi tak koi "authentication"
            // is request ke SecurityContext mein set nahi hui hai (double-check,
            // taaki agar galti se filter 2 baar chal jaye to dubara kaam na ho),
            // to hum User ko database se load karte hain aur verify karte hain
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            	
            	// CustomUserDetailsService use karke user ka poora UserDetails
                // object nikalte hain (jisme password-hash, role/authorities sab hai)
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                
                // Step 6: Token GENUINE hai aur EXPIRE nahi hua, ye verify karte hain
                if (jwtUtil.isTokenValid(token, userDetails.getUsername())) {
                	
                	// Step 7: Ek "Authentication" object banate hain — ye
                    // Spring Security ko batata hai "is request ke liye
                    // authentication successful hai, ye raha authenticated user"
                    //
                    // UsernamePasswordAuthenticationToken naam thoda confusing
                    // hai (JWT use kar rahe hain, password nahi), lekin ye
                    // Spring Security ka STANDARD "authenticated principal"
                    // wrapper class hai — JWT-based auth mein bhi yahi use hota hai
                	UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,               // principal (kaun hai)
                                    null,                       // credentials (password ki zarurat nahi, already verify ho chuka JWT se)
                                    userDetails.getAuthorities() // roles/permissions (ROLE_CUSTOMER ya ROLE_ADMIN)
                            );
                	
                	authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                	
                	// Step 8: Is authentication ko "SecurityContext" mein
                    // set kar dete hain — ab is poori request ke lifecycle
                    // mein (Controller tak), Spring Security jaanta hai
                    // "kaun logged in hai aur uska role kya hai"
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            
        } catch(Exception e) {
        	
        	// Agar token corrupt/expired/tampered hai, to upar ke steps
            // exception throw kar denge (JwtUtil.extractEmail() ke andar se).
            // Hum yahan use silently ignore kar dete hain — matlab
            // authentication set NAHI hoga, aur agar endpoint PROTECTED hai,
            // aage SecurityConfig khud "401 Unauthorized" de dega.
            // (Hum yahan khud koi response nahi bhejte, bas chup rehte hain
            // aur request ko aage jaane dete hain bina authentication ke)
        }
        
        // Step 9: Chahe authentication successful hui ho ya na hui ho,
        // request ko HAMESHA aage badhne dena hai (filter chain mein agla
        // step, jab tak koi previous step khud response na bhej de) —
        // final decision (allow/block) SecurityConfig ke rules lenge
        filterChain.doFilter(request, response);
    }
}
