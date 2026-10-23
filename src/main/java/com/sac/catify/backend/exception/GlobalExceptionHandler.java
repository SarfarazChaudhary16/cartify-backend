package com.sac.catify.backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// @RestControllerAdvice ek combo hai (@ControllerAdvice + @ResponseBody):
// "ye class SAARE Controllers ke exceptions ko GLOBALLY handle karegi,
// aur response JSON format mein jayega". Ye poori application mein ek
// hi jagah likha jata hai — hume har Controller mein alag-alag try-catch
// likhne ki zarurat NAHI padti.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ===================================================================
    // 1. Hamari khud ki ApiException handle karna
    // ===================================================================
    // @ExceptionHandler batata hai: "jab bhi kahin ApiException throw ho
    // (kisi bhi Controller/Service se), is method ko call kar do"
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(ApiException ex, HttpServletRequest request) {

        ApiErrorResponse error = new ApiErrorResponse(
                ex.getStatus().value(),      // jaise 404
                ex.getMessage(),               // jaise "Product not found with id: 999"
                LocalDateTime.now(),
                request.getRequestURI()        // jaise "/api/products/999"
        );

        // ex.getStatus() wahi status hai jo humne ApiException banate
        // waqt diya tha (jaise ApiException.notFound() se 404) — isliye
        // response ka HTTP status code bhi WAHI hoga, sirf body mein
        // JSON nahi, balki actual response-status bhi sahi jayega
        return new ResponseEntity<>(error, ex.getStatus());
    }

    // ===================================================================
    // 2. @Valid validation failures handle karna
    // ===================================================================
    // humne DTOs mein @NotBlank, @Email, @Positive waghera
    // lagaye the, aur Controllers mein @Valid use kiya tha? Jab koi
    // validation FAIL hoti hai, Spring khud "MethodArgumentNotValidException"
    // throw karta hai. Agar hum ise handle NA karte, to iska response
    // bhi ek ugly/default Spring error page/JSON hota. Ab hum ise bhi
    // apne clean format mein convert kar rahe hain.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        // Ek validation request mein MULTIPLE fields fail ho sakte hain
        // (jaise email bhi khaali, password bhi chhota) — isliye hum
        // saare field-errors ko ek Map mein jama karte hain:
        // { "email": "Enter a valid email address", "password": "..." }
        Map<String, String> fieldErrors = new HashMap<>();

        // getBindingResult().getFieldErrors() -> saare fail hue fields
        // ki list deta hai. Har field ke liye uska naam aur uska
        // @NotBlank(message="...") wala custom message nikalte hain.
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        // Final response mein status/timestamp/path ke saath, ye
        // fieldErrors ka poora Map bhi bhejte hain — taaki frontend form
        // ke HAR field ke neeche uska apna specific error dikha sake
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", "Validation failed");
        response.put("errors", fieldErrors);
        response.put("timestamp", LocalDateTime.now());
        response.put("path", request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // ===================================================================
    // 3. Spring Security ka "bad credentials" (galat password) handle karna
    // ===================================================================
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest request) {

        ApiErrorResponse error = new ApiErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Incorrect email or password.",
                LocalDateTime.now(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }
    
    
 // ===================================================================
    // 3.5. IllegalArgumentException handle karna (enum conversion fail)
    // ===================================================================
    // Ye tab throw hoti hai jab koi String, enum.valueOf() ko di jaye
    // jo us enum mein EXIST hi NAHI karta — jaise CouponServiceImpl mein
    // "DiscountType.valueOf(request.getDiscountType())" agar koi
    // "discountType": "xyz" bheje (jo na FLAT hai na PERCENT), ye
    // exception throw hogi. Isko specifically pakड़ke, user ko ek CLEAR
    // message dete hain (na ki generic "Something went wrong").
    //
    // IMPORTANT: Ye handler @ExceptionHandler(Exception.class) (jo
    // sabसे neeche/generic hai) SE PEHLE likha jana chahiye is FILE mein
    // — Spring "sabसे SPECIFIC match" ko priority deta hai automatically
    // (chahe file mein order kuch bhi ho), lekin READABILITY ke liye hum
    // specific-se-generic order mein hi likh rahe hain.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {

        ApiErrorResponse error = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid value provided: " + ex.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // ===================================================================
    // 4. CATCH-ALL — koi bhi aur/anjaani exception (safety net)
    // ===================================================================
    // Ye SABSE AAKHRI/generic handler hai — agar koi aisi exception aaye
    // jo upar ke 3 handlers se match nahi hoti (jaise koi unexpected
    // NullPointerException, ya database connection issue), ye ise pakड़
    // ke kam se kam ek CLEAN 500 response degi, poora Java stack-trace
    // frontend/Postman ko expose NAHI hoga (security ke liye achha hai —
    // internal error-details hackers ko clues de sakte hain)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {

        ApiErrorResponse error = new ApiErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Something went wrong. Please try again later.",
                LocalDateTime.now(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
