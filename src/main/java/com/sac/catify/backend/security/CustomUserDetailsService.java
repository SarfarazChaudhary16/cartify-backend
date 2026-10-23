package com.sac.catify.backend.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sac.catify.backend.entity.User;
import com.sac.catify.backend.repository.UserRepository;

//Ye class Spring Security ko batati hai ki database se user kaise 
//lana hai aur usko Spring Security ke format me kaise convert karna hai.

//Kyun bana: Spring Security ka apna ek internal concept hai UserDetails — 
//"current logged-in user kaun hai" ye represent karta hai. Humara User entity 
//Spring Security ke is format mein nahi hai, isliye humein ek "translator" class 
//banani padti hai jo hamare User ko Spring Security ke samajhne layak object mein convert kare.

//UserDetailsService ek Spring Security ka STANDARD interface hai — hume
//isse implement karna zaroori hai taaki Spring Security ko pata chale
//"database mein user kaise dhoondhna hai". Ye interface sirf EK method
//maangta hai: loadUserByUsername(). Hamare case mein "username" asal
//mein EMAIL hai (kyunki humne email se login banaya hai, alag username
//field nahi rakha).

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // CHANGE: Ab Spring Security ke built-in "User.withUsername(...)"
        // ki jagah HAMARA khud ka CustomUserDetails return kar rahe hain —
        // isme poora User entity (id samet) wrapped hai, isliye Controllers
        // mein @AuthenticationPrincipal se seedha userId mil jayega, bina
        // extra database call ke.
        return new CustomUserDetails(user);
    }
}

