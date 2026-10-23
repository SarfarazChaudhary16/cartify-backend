package com.sac.catify.backend.security;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sac.catify.backend.repository.OtpTokenRepository;

import java.time.LocalDateTime;

// Ye class ek "background job" hai — koi Controller/Service ise seedha
// call nahi karta, Spring khud ise apne-aap, khud-ba-khud, ek fix
// time-interval pe chalata rehta hai (jab tak application chalu hai).
//
// @Component isliye (na ki @Service) kyunki ye "business logic" nahi hai
// (jaise Product/Order ka logic), ye ek "housekeeping/maintenance" task hai.
@Component
public class OtpCleanupScheduler {

    private final OtpTokenRepository otpTokenRepository;

    public OtpCleanupScheduler(OtpTokenRepository otpTokenRepository) {
        this.otpTokenRepository = otpTokenRepository;
    }

    // @Scheduled Spring ko batata hai "is method ko baar-baar, khud-ba-khud
    // chalao, bina kisi HTTP request ke".
    //
    // fixedRate = 3600000 -> matlab 3600000 MILLISECONDS = 1 GHANTA.
    // Iska matlab: ye method application start hone ke turant baad EK BAAR
    // chalega, aur uske baad HAR 1 GHANTE mein dobara chalta rahega, jab
    // tak application band nahi ho jati.
    //
    // (Agar chaho to "cron" expression bhi use kar sakte the, jaise
    // "@Scheduled(cron = "0 0 * * * *")" jo bhi exact clock-hour pe chalata,
    // lekin fixedRate simpler hai aur humare use-case ke liye kaafi hai)
    @Scheduled(fixedRate = 3600000)
    public void deleteExpiredOtps() {

        // Abhi ka time nikalte hain
        LocalDateTime now = LocalDateTime.now();

        // Saari OTP rows delete kar do jinki expiresAt, ABHI ke time se
        // PEHLE ki hai (matlab wo expire ho chuki hain) — chahe wo "used"
        // hui ho ya nahi. Agar koi OTP generate hui thi lekin user ne
        // kabhi verify nahi kiya aur 10 minute se zyada ho gaye, ye method
        // use turant nahi, lekin agle scheduled-run (1 ghante ke andar)
        // mein saaf kar dega.
        otpTokenRepository.deleteByExpiresAtBefore(now);

        // Note: Hum yahan koi return value ya log print nahi kar rahe
        // simplicity ke liye. Chaho to yahan System.out.println() ya
        // proper Logger (jaise SLF4J) add kar sakte ho taaki console mein
        // dikhe "X expired OTPs cleaned up" — abhi ke liye silent rakha hai.
    }
}
