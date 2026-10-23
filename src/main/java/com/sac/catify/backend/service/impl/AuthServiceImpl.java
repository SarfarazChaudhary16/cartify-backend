package com.sac.catify.backend.service.impl;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sac.catify.backend.dto.*;
import com.sac.catify.backend.entity.OtpPurpose;
import com.sac.catify.backend.entity.OtpToken;
import com.sac.catify.backend.entity.Role;
import com.sac.catify.backend.entity.User;
import com.sac.catify.backend.exception.ApiException;
import com.sac.catify.backend.repository.OtpTokenRepository;
import com.sac.catify.backend.repository.UserRepository;
import com.sac.catify.backend.security.JwtUtil;
import com.sac.catify.backend.service.AuthService;
import com.sac.catify.backend.service.EmailService;

@Service
public class AuthServiceImpl implements AuthService {

	
	 // 5 dependencies chahiye is class ko — sabka apna kaam:
    private final UserRepository userRepository;         // User table ke saath baat karne ke liye
    private final OtpTokenRepository otpTokenRepository;  // OTP tokens save/dhoondhne ke liye
    private final PasswordEncoder passwordEncoder;        // BCrypt hashing ke liye (SecurityConfig se aaya Bean)
    private final JwtUtil jwtUtil;                        // JWT token generate karne ke liye
    private final EmailService emailService;              // OTP email bhejne ke liye

    
    // Random number generator OTP codes banane ke liye. SecureRandom
    // (Math.random() ke bajaye) isliye use kar rahe hain kyunki ye
    // "cryptographically strong" hota hai — matlab iske generate kiye
    // numbers predict karna bahut mushkil hai, security-sensitive
    // cheezon (jaise OTP) ke liye ye zaroori practice hai.
    private final SecureRandom secureRandom = new SecureRandom();
	
    public AuthServiceImpl(UserRepository userRepository,
                           OtpTokenRepository otpTokenRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil,
                           EmailService emailService) {
    	
    	this.userRepository = userRepository;
    	this.otpTokenRepository = otpTokenRepository;
    	this.passwordEncoder = passwordEncoder;
    	this.jwtUtil = jwtUtil;
    	this.emailService = emailService;
}
	
    // ===================================================================
    // REGISTER (Signup Step 1)
    // ===================================================================	
	@Override
	public MessageResponse register(RegisterRequest request) {
		
		
		// Step 1: Check karo email already registered to nahi hai.
        // Agar hai, to aage badhne ki zarurat nahi, error de dete hain.
        if (userRepository.existsByEmail(request.getEmail())) {
        	
        	throw ApiException.conflict("Email already registered: " + request.getEmail());
        }
        
        // Step 2: Naya User object banate hain aur request ka data bharte hain
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        
        // ===== YAHI HAI BCrypt WALA STEP =====
        // passwordEncoder.encode(rawPassword) -> plain password (jaise
        // "myPassword123") ko ek LAMBI, RANDOM-DIKHNE WALI hashed string
        // mein convert kar deta hai (jaise "$2a$10$N9qo8uLOickgx2ZMRZoMy...").
        // Isi hashed version ko hum database mein save karte hain — original
        // plain password KABHI bhi kahin store nahi hota, na hi log hota hai.
        // Agar database leak bhi ho jaye, hacker ko sirf ye hash milega,
        // jisse original password nikalna (practically) namumkin hai.
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Naya signup hamesha CUSTOMER role se shuru hota hai (frontend
        // useAuthStore.signup() jaisa hi hardcoded rule — sirf backend
        // database mein manually kisi ko ADMIN banaya ja sakta hai)
        user.setRole(Role.CUSTOMER);
        
        user.setAvatar("https://api.dicebear.com/9.x/notionists/svg?seed=" + request.getName());
        
        // IMPORTANT: enabled = false rakhte hain (Entity mein default bhi
        // false hai, lekin explicitly likh rahe hain clarity ke liye).
        // Jab tak OTP verify nahi hota, ye user LOGIN nahi kar payega.
        user.setEnabled(false);
        
        userRepository.save(user);
        
     // Step 3: OTP generate karke email bhejte hain
        // (generateAndSendOtp ek private helper hai, neeche define hai)
        generateAndSendOtp(request.getEmail(), OtpPurpose.SIGNUP_VERIFICATION);

        return new MessageResponse("Account created — check your email for the verification code.");

	}

//=============================================================================================================

	// ===================================================================
    // VERIFY SIGNUP OTP (Signup Step 2 — auto-login hone ke saath)
    // ===================================================================
	@Override
	public LoginResponse verifySignupOtp(VerifyOtpRequest request) {
		
		// Step 1: OTP verify karte hain (validateOtp ek private helper hai,
        // neeche define hai — email+code+purpose match karta hai,
        // expiry/used check karta hai)
        validateOtp(request.getEmail(), request.getCode(), OtpPurpose.SIGNUP_VERIFICATION);
        
        //Step 2: OTP sahi nikla, ab User ko dhoondh ke enable karte hain
        User user = userRepository.findByEmail(request.getEmail())
        		
        		.orElseThrow(() -> ApiException.notFound("User not found with email: " + request.getEmail()));
        
        user.setEnabled(true);
        userRepository.save(user);
        
        // Is OTP ka kaam poora ho gaya (email verify ho chuki), ab isse
        // database mein rakhne ki zarurat nahi — turant delete kar dete hain
        otpTokenRepository.deleteByEmailAndPurpose(request.getEmail(), OtpPurpose.SIGNUP_VERIFICATION);
        
        // Step 3: Turant JWT token generate karke "auto-login" karwa dete hain
        // (bilkul jaisa frontend ke verifyOtp() action mein tha)
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return new LoginResponse(token, mapToUserResponse(user));
		
	}

//=============================================================================================================
	
	// ===================================================================
    // LOGIN
    // ===================================================================
    @Override
	public LoginResponse login(LoginRequest request) {
		
    	
    	// Step 1: Email se user dhoondhte hain. Agar na mile, generic
        // error dete hain "Incorrect email or password" (specifically
        // "email exist nahi karta" nahi bolte — ye security best practice
        // hai, isse attacker ko pata nahi chalta ki kaunse emails registered
        // hain system mein, guessing/enumeration attack rukta hai)
        User user = userRepository.findByEmail(request.getEmail())
        		
        		.orElseThrow(() -> ApiException.unauthorized("Incorrect email or password."));

        // Step 2: Password match karte hain.
        // passwordEncoder.matches(rawPassword, hashedPasswordFromDB) ->
        // BCrypt KHUD andar-hi-andar rawPassword ko hash karke compare
        // karta hai hashedPassword se (special algorithm se, seedha
        // string == string nahi karta). True/false return karta hai.
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        	
        	throw ApiException.unauthorized("Incorrect email or password.");
        }
        
        // Step 3: Check karo account verified (enabled) hai ya nahi
        if (!user.isEnabled()) {
        	
        	throw ApiException.unauthorized("Please verify your email before logging in.");
        }
        
        
        // NAYA CHECK: Agar Admin ne is user ko "block" kar diya hai,
        // to login yahi ruk jayega — chahe password bilkul sahi ho.
        // Ye check "enabled" check ke TURANT BAAD hai (order zaroori
        // hai — pehle basic verification check, phir blocked-status check)
        if (user.isBlocked()) {
            throw ApiException.forbidden("Your account has been blocked. Please contact support.");
        }
        
        // Step 4: Sab sahi hai — JWT token banate hain
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return new LoginResponse(token, mapToUserResponse(user));
	}
	
//=============================================================================================================

    // ===================================================================
    // FORGOT PASSWORD (Step 1 — OTP bhejna)
    // ===================================================================
	@Override
	public MessageResponse forgotPassword(ForgotPasswordRequest request) {
		
		
		// Check karte hain ki ye email registered hai ya nahi
        if (!userRepository.existsByEmail(request.getEmail())) {
        	
        	throw ApiException.notFound("No account found with this email address.");
        }
        
        generateAndSendOtp(request.getEmail(), OtpPurpose.PASSWORD_RESET);

        return new MessageResponse("We sent a 6-digit code to " + request.getEmail());
        
	}
	
//=============================================================================================================


    // ===================================================================
    // VERIFY RESET OTP (Step 2 — sirf verify, password change nahi)
    // ===================================================================
	@Override
	public MessageResponse verifyResetOtp(VerifyOtpRequest request) {


		validateOtp(request.getEmail(), request.getCode(), OtpPurpose.PASSWORD_RESET);
		
        return new MessageResponse("Code verified. You can now set a new password.");
	}
	
//=============================================================================================================

	// ===================================================================
    // RESET PASSWORD (Step 3 — FINAL, naya password set karna)
    // ===================================================================
	@Override
	public MessageResponse resetPassword(ResetPasswordRequest request) {
		
		

        // Security check: Is email ke liye koi "used=true" (matlab abhi
        // recently verify hua) PASSWORD_RESET OTP hona chahiye, warna
        // koi bhi seedha ye endpoint hit karke, bina OTP verify kiye,
        // kisi aur ka password badal sakta tha!
        //
        // findFirstByEmailAndPurposeOrderByCreatedAtDesc -> sabse LATEST
        // OTP nikalte hain us email/purpose ke liye
        OtpToken latestOtp = otpTokenRepository
                .findFirstByEmailAndPurposeOrderByCreatedAtDesc(request.getEmail(), OtpPurpose.PASSWORD_RESET)
                .orElseThrow(() -> ApiException.badRequest("No password reset request found. Please start over."));
        
     // Agar latest OTP abhi tak "used" nahi hua, matlab verify-reset-otp
        // step skip karke seedha yahan aa gaya — ye allowed nahi hai
        if (!latestOtp.isUsed()) {
        	
        	throw ApiException.badRequest("Please verify the OTP code before resetting your password.");
        }
        
        User user = userRepository.findByEmail(request.getEmail())
        		
        		.orElseThrow(() -> ApiException.notFound("User not found with email: " + request.getEmail()));
        
        // Naya password bhi hash karke save karte hain (BCrypt yahan bhi use hota hai)
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
       // Password successfully reset ho gaya — is email ke saare
        // PASSWORD_RESET OTPs (jo verify-reset-otp step mein "used" mark
        // hue the, unhe hum yahan tak isliye rakhe hue the taaki verify
        // karke check kar sakein) ab delete kar dete hain, kaam khatam.
        otpTokenRepository.deleteByEmailAndPurpose(request.getEmail(), OtpPurpose.PASSWORD_RESET);
        
        return new MessageResponse("Password updated. You can now log in.");
	}
	
//=============================================================================================================

	// ===================================================================
    // RESEND OTP
    // ===================================================================
	@Override
	public MessageResponse resendOtp(String email, String purpose) {
		
		
		// String purpose ko OtpPurpose enum mein convert karte hain.
        // valueOf() ek built-in enum method hai — jaise
        // OtpPurpose.valueOf("SIGNUP_VERIFICATION") -> OtpPurpose.SIGNUP_VERIFICATION
        // Agar galat string aayi (jo enum mein exist hi nahi karta),
        // ye khud IllegalArgumentException throw kar dega
        OtpPurpose otpPurpose = OtpPurpose.valueOf(purpose);
        generateAndSendOtp(email, otpPurpose);
        return new MessageResponse("A new code was sent to " + email);
	}
	
//=============================================================================================================

	
	// ===================================================================
    // PRIVATE HELPER METHODS
    // ===================================================================

    // OTP generate karta hai, database mein save karta hai, aur email bhejta hai.
    // Ye 2 jagah (register, forgotPassword, resendOtp) se reuse hota hai
    // isliye ek common private method banaya — DRY principle.
    private void generateAndSendOtp(String email, OtpPurpose purpose) {

        // 6-digit random code banate hain: 100000 se 999999 tak ka number
        // (100000 isliye taaki hamesha EXACTLY 6 digits ka number bane,
        // "012345" jaisa leading-zero wala number nahi, jo "12345" 5-digit
        // dikh sakta tha display mein confusion create karta)
        int otpNumber = 100000 + secureRandom.nextInt(900000);
        String otpCode = String.valueOf(otpNumber);

        // OtpToken entity banate hain: abhi se 5 minute baad expire hoga
        // (application.properties mein "otp.expiry.minutes=5" set kiya tha —
        // abhi ke liye hardcoded 10 rakh raha hoon simplicity ke liye,
        // chaho to @Value se properties se bhi utha sakte ho jaisa JwtUtil mein kiya)
        OtpToken otpToken = new OtpToken(email, otpCode, purpose, LocalDateTime.now().plusMinutes(5));
        otpTokenRepository.save(otpToken);

        // Email bhejte hain — purpose.name() se enum ko String mein convert
        // karte hain (jaise OtpPurpose.SIGNUP_VERIFICATION -> "SIGNUP_VERIFICATION"),
        // kyunki EmailService.sendOtpEmail() String purpose expect karta hai
        emailService.sendOtpEmail(email, otpCode, purpose.name());
    
    }
    
//==================================================================================================================
    
    // OTP ko validate (verify) karta hai — match, expiry, aur used-check
    // teeno karta hai. Ye 2 jagah (verifySignupOtp, verifyResetOtp) se
    // reuse hota hai.
    private void validateOtp(String email, String code, OtpPurpose purpose) {

        OtpToken otpToken = otpTokenRepository
                .findByEmailAndCodeAndPurpose(email, code, purpose)
                .orElseThrow(() -> ApiException.badRequest("Incorrect code. Please try again."));

        // Check 1: Kya ye OTP pehle hi kisi aur request mein use ho chuka hai?
        // (replay-attack se bachne ke liye — ek OTP sirf EK BAAR use ho sakta hai)
        if (otpToken.isUsed()) {
        	
        	throw ApiException.badRequest("This code has already been used. Please request a new one.");
        }

        // Check 2: Kya ye OTP expire ho chuka hai? (2 minute se zyada purana)
        if (otpToken.getExpiresAt().isBefore(LocalDateTime.now())) {
        	
        	throw ApiException.badRequest("This code has expired. Please request a new one.");
        }

        // Sab sahi hai — OTP ko "used" mark kar dete hain taaki dobara use na ho sake
        otpToken.setUsed(true);
        otpTokenRepository.save(otpToken);
    }

//===================================================================================================================
    // User Entity ko safe UserResponse DTO mein convert karta hai
    // (Phase B4 mein UserServiceImpl mein bhi yahi pattern tha — password
    // field yahan "chhod" diya jata hai, kyunki UserResponse mein wo field hai hi nahi)
    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getAvatar(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
