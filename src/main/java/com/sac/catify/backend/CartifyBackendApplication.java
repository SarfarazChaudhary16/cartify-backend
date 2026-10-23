package com.sac.catify.backend;

import org.springframework.boot.SpringApplication; 
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

//Ye class poore Spring Boot app ka "entry point" hai.
//@SpringBootApplication ek shortcut annotation hai jo 3 kaam ek saath karta hai:
//1. Batata hai Spring ko: "ye configuration class hai"
//2. Auto-configuration on kar deta hai (jaise humari added dependencies ke hisaab se auto-setup)
//3. Component scanning shuru karta hai — is package aur niche ke saare
//   @Service, @Repository, @RestController classes khud dhoondh leta hai

//@EnableScheduling ZAROORI hai — bina isske @Scheduled annotation wale
//methods (jaise OtpCleanupScheduler) bilkul kaam NAHI karenge, Spring
//unhe silently ignore kar dega. Ye ek "master switch" hai jo poori
//application mein scheduling feature ko ON karta hai.

@SpringBootApplication
@EnableScheduling
public class CartifyBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CartifyBackendApplication.class, args);
		
	}

//	8ubI+ZOTLxNTln05Xn90liGLDAfOdmUNSk23cu2bvGI=

}
