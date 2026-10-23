package com.sac.catify.backend.exception;

import org.springframework.http.HttpStatus;

// Ye humari APNI custom exception class hai — Java ki built-in RuntimeException
// ko HI "extend" (inherit) kar rahe hain, taaki ye bhi ek "unchecked exception"
// rahe (matlab try-catch likhna FORCE nahi hota har jagah, jaise normal
// RuntimeException ke saath hota hai).
//
// Extra fayda: is class mein hum "HttpStatus" bhi saath mein store kar sakte
// hain — matlab jab Service layer koi error throw karega, wo KHUD decide
// karega "ye 404 hona chahiye ya 400 ya 409" — Controller ko alag se
// ye decide nahi karna padega, GlobalExceptionHandler (agli file) khud
// isi status ko use kar lega response banate waqt.
public class ApiException extends RuntimeException {

    // HttpStatus Spring ka built-in enum hai jisme saare standard HTTP
    // status codes hain (NOT_FOUND=404, BAD_REQUEST=400, CONFLICT=409, waghera)
    private final HttpStatus status;

    // Constructor — message (super() se RuntimeException ko de dete hain,
    // taaki getMessage() waise hi kaam kare jaise normal exception mein)
    // aur status dono ek saath lete hain
    public ApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    // Convenience constructor — agar status specify na karo, default
    // BAD_REQUEST (400) maan lete hain (sabse common validation-error case)
    public ApiException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }
    
    // Ye constructor tab use hota hai jab humare paas ek ORIGINAL/underlying
    // exception (jaise MessagingException) bhi hai jise hum "wrap" kar rahe
    // hain. "cause" pass karne se Java internally poori exception-chain
    // yaad rakhta hai — agar kabhi debugging karni pade, poora stack-trace
    // (humari ApiException + original MessagingException dono) milta hai,
    // sirf humara wala message nahi.
    public ApiException(String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    // ===================================================================
    // STATIC FACTORY METHODS — ye "convenience shortcuts" hain
    // ===================================================================
    // Inka fayda: Service layer mein likhna hoga sirf
    //     throw ApiException.notFound("Product not found");
    // bajaye is lambe wale ke:
    //     throw new ApiException("Product not found", HttpStatus.NOT_FOUND);
    // Dono same kaam karte hain, bas ye chhota/readable syntax deta hai.

    public static ApiException notFound(String message) {
        return new ApiException(message, HttpStatus.NOT_FOUND); // 404
    }

    public static ApiException badRequest(String message) {
        return new ApiException(message, HttpStatus.BAD_REQUEST); // 400
    }

    public static ApiException conflict(String message) {
        // 409 Conflict — jab koi cheez pehle se exist karti ho (jaise
        // "email already registered" jaisi situations ke liye use hoga)
        return new ApiException(message, HttpStatus.CONFLICT);
    }

    public static ApiException unauthorized(String message) {
        return new ApiException(message, HttpStatus.UNAUTHORIZED); // 401
    }

    public static ApiException forbidden(String message) {
        // 403 Forbidden — jaise "this cart item does not belong to you" jaisi
        // security-check failures ke liye (login to hai, lekin permission nahi)
        return new ApiException(message, HttpStatus.FORBIDDEN);
    }
}
