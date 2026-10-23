package com.sac.catify.backend.exception;

import java.time.LocalDateTime;

// Ye class define karti hai ki error-response ka JSON kaisa DIKHEGA —
// chahe error kahin bhi se aaya ho (Product na mile, ya validation fail
// ho, ya security check fail ho), response hamesha isi SHAPE mein jayega.
// Isse frontend developer (ya humara React app) ko pata hota hai
// "error aaye to hamesha ye 4 fields expect karo", consistent contract milta hai.
public class ApiErrorResponse {

    private int status;         // HTTP status code, jaise 404
    private String message;     // human-readable error message
    private LocalDateTime timestamp; // error kab aaya (debugging ke liye useful)
    private String path;        // kaunsa URL/endpoint pe error aaya (jaise "/api/products/999")

    public ApiErrorResponse() {
    }

    public ApiErrorResponse(int status, String message, LocalDateTime timestamp, String path) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
        this.path = path;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
