package com.sac.catify.backend.config;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.sac.catify.backend.security.JwtAuthFilter;
// @Configuration batata hai Spring ko: "is class ke andar @Bean methods hain,
// inhe application startup ke waqt hi process kar lo aur unke return kiye
// gaye objects ko Spring ke container mein register kar do."
@Configuration
public class SecurityConfig {

	// JwtAuthFilter ko yahan inject kar rahe hain taaki hum ise
    // SecurityFilterChain ke andar "plug in" kar sakein
    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
 // Ye CORS ko SEEDHA Spring Security ke saath wire karta hai — jab
    // Security enabled hoti hai, sirf WebMvcConfigurer wala CORS config
    // (jo humne B1 mein banaya tha) kabhi-kabhi PURI tarah kaam nahi
    // karta, kyunki Security ka apna filter-chain requests ko pehle
    // intercept kar sakta hai. Isliye CORS rules ko yahan bhi define
    // karte hain, aur neeche securityFilterChain() mein ".cors(...)"
    // se explicitly enable karte hain — ye Spring Security 6 ke saath
    // CORS use karne ka OFFICIAL/recommended tareeka hai.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:3000",
                "https://cartify-frontend-eight.vercel.app"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // AuthenticationManager ek Spring Security "core" object hai jo
    // authentication process ko coordinate karta hai. Hum ise abhi
    // directly kahin use nahi kar rahe (kyunki humara login logic
    // AuthServiceImpl mein khud manually likha hai — findByEmail +
    // passwordEncoder.matches()), lekin ye Bean banana Spring Security
    // ke internal setup ke liye standard practice hai, future mein
    // agar zarurat pade (jaise @PreAuthorize annotations) to kaam aayega.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                
//                CSRF (Cross Site Request Forgery) ek attack hai jisme koi dusri website user ke browser se fake request bhejne ki koshish karti hai.
//
//                Example
//
//                Tum login ho
//
//                Amazon
//
//                Tab khula hua hai.
//
//                Ab tum
//
//                ek malicious website
//
//                open karte ho.
//
//                Wo website secretly
//
//                POST /changePassword
//
//                bhej deti hai.
//
//                Agar protection na ho
//
//                Password change.
//
//                Lekin JWT me
//
//                Hum
//
//                Authorization Header
//
//                use karte hain.
//
//                Na ki Session Cookie.
//
//                Isliye
//
//                CSRF ki zarurat nahi.
//
//                Isliye
                // ===================================================================
                // SESSION MANAGEMENT — YE BAHUT IMPORTANT LINE HAI
                // ===================================================================
                // STATELESS batata hai Spring Security ko: "koi HttpSession mat
                // banao ya use mat karo". Traditional web-apps mein server
                // login ke baad ek "session" bana ke rakhta hai memory mein
                // (session ID cookie ke through track hota hai). JWT-based
                // auth mein ye zarurat hi nahi — HAR request apna poora proof
                // (JWT token) khud carry karti hai, server ko kuch "yaad" rakhne
                // ki zarurat nahi. Isse app horizontally scale karna bhi aasan
                // hota hai (multiple servers ke beech session-sharing ka
                // jhanjhat nahi rehta).
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ===================================================================
                // URL-LEVEL ACCESS RULES (temporary permitAll() ki jagah FINAL rules)
                // ===================================================================
                .authorizeHttpRequests(auth -> auth

                        // ===== PUBLIC endpoints — bina login ke bhi accessible =====

                        // Authentication endpoints khud (login/signup/OTP) —
                        // agar ye protected hote, koi login hi nahi kar pata!
                        .requestMatchers("/api/auth/**").permitAll()  //permitAll() allows anyone to access the endpoint without authentication.
                        .requestMatchers("/api/contact").permitAll()
                        .requestMatchers("/api/ai/chat").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/store-settings").permitAll()
                        .requestMatchers("/api/store-settings/admin").hasRole("ADMIN")
                        // Admin-only order-management endpoints — ye rule
                        // baaki saare "/api/admin/**" rules (jaise Products
                        // ke ADMIN-only POST/PUT/DELETE) se PEHLE aani chahiye
                        // taaki specific rule pehle match ho
                        .requestMatchers("/api/support-tickets/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN") //hasRole("ADMIN") allows access only to users who have the ADMIN role.
                        .requestMatchers("/api/reviews/admin/**").hasRole("ADMIN")

                     // FIX: HttpMethod.GET (enum) use kiya hai, "GET" (String) nahi —
                        // is Spring Security version mein String overload sirf URL
                        // patterns ke liye hai, HTTP method ke liye alag HttpMethod
                        // enum wala overload use karna zaroori hai, warna Spring
                        // "GET"/"POST" ko khud ek URL pattern samajh ke crash karta hai
                        // (yahi wajah thi pichle error ki: "pattern must start with a /")
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/brands/**").permitAll()

                        // Admin ke Product/Category/Brand CREATE/UPDATE/DELETE
                        // endpoints — sirf ADMIN role wale hi access kar sakein.
                        // .hasRole("ADMIN") internally "ROLE_ADMIN" authority check
                        // karta hai (CustomUserDetailsService mein humne
                        // "ROLE_" prefix add kiya hai)
                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/brands/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/brands/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/brands/**").hasRole("ADMIN")
                        // Reviews dekhna PUBLIC hai (bina login ke bhi product
                        // detail page pe reviews dikhne chahiye)
                        .requestMatchers(HttpMethod.GET, "/api/reviews/product/**").permitAll()
                        
                        // Coupon-validate endpoint sabhi AUTHENTICATED users
                        // (customers) ke liye open hai — koi ADMIN-restriction
                        // nahi (ye already ".anyRequest().authenticated()" se
                        // cover ho jayega, isliye extra line ki zarurat nahi)
                        // Lekin coupon CREATE/UPDATE/DELETE/toggle sirf ADMIN
                        // kar sakta hai:
                        .requestMatchers(HttpMethod.POST, "/api/coupons").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/coupons/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/coupons/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/coupons").hasRole("ADMIN")

                        // ===== Baaki HAR endpoint (jo upar list nahi hua) =====
                        // .authenticated() -> matlab "sirf VALID JWT token ke
                        // saath hi access milega, chahe koi bhi role ho".
                        // Ye baaki saare future modules (Cart, Order, Reviews,
                        // waghera) ke liye default-safe rule hai — hum jaise-
                        // jaise naye modules banayenge, upar specific rules
                        // (jaise "/api/admin/**" -> hasRole("ADMIN")) add karte jayenge
                        .anyRequest().authenticated() //authenticated() means the user must be logged in with a valid JWT token.
                )

                // ===================================================================
                // JWT FILTER KO CHAIN MEIN "PLUG IN" KARNA
                // ===================================================================
                // addFilterBefore() batata hai: "hamara JwtAuthFilter,
                // UsernamePasswordAuthenticationFilter (Spring Security ka
                // built-in default filter) SE PEHLE chalna chahiye".
                // Kyun pehle? Kyunki humein pehle token check karke
                // "authenticated" set karna hai, TABHI jaake upar wale
                // authorizeHttpRequests() rules (hasRole, authenticated)
                // sahi se kaam kar payenge — warna Spring Security ko
                // pata hi nahi chalega ki current request "kaun" bhej raha hai.
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

/*Poora Flow — Ek Protected Request kaise process hoti hai (end-to-end)

Frontend → GET /api/orders  (koi future protected endpoint, example ke liye)
Header: "Authorization: Bearer eyJhbGc...XYZ"
│
▼
┌─────────────────────────────────────────────────────────┐
│  JwtAuthFilter.doFilterInternal()  (SABSE PEHLE chalta hai)│
│                                                             │
│  1. Header se token nikalta hai                            │
│  2. jwtUtil.extractEmail(token) → "user@email.com"          │
│  3. userDetailsService.loadUserByUsername(email)            │
│         └─► CustomUserDetailsService → DB se User lata hai,  │
│             UserDetails object banata hai (role ke saath)   │
│  4. jwtUtil.isTokenValid(token, email) → true                │
│  5. SecurityContextHolder mein "authenticated" set kar deta  │
│     hai: "ye user@email.com hai, role ROLE_CUSTOMER hai"     │
│  6. filterChain.doFilter() → request ko AAGE jaane deta hai   │
└─────────────────────────────────────────────────────────┘
│
▼
┌─────────────────────────────────────────────────────────┐
│  SecurityConfig ke authorizeHttpRequests() rules check hote │
│                                                             │
│  "/api/orders" -> koi specific rule nahi mili upar, isliye  │
│  ".anyRequest().authenticated()" wala rule apply hoga        │
│                                                             │
│  Check: "Kya is request pe SecurityContext mein             │
│  authentication SET hai?" → HAAN (JwtAuthFilter ne kiya)     │
│  → ALLOWED, aage badhne do                                   │
└─────────────────────────────────────────────────────────┘
│
▼
OrderController tak request pahunchti hai, normal kaam hota hai


═══════ AGAR TOKEN NA HOTA / GALAT HOTA: ═══════

JwtAuthFilter mein authentication SET nahi hoga (koi valid
email nahi mila) → SecurityContext khaali rahega
│
▼
authorizeHttpRequests() check karega: "authenticated() chahiye,
lekin SecurityContext mein koi authentication nahi hai"
│
▼
Spring Security KHUD "401 Unauthorized" response bhej deta hai,
Controller tak request pahunchti hi nahi
*/
