package com.tcs.taskmanagement.dto;

public class OtpResponse {

    private String status;   // "OTP_SENT" or "ERROR"
    private String message;
    private String email;    // echo back email for frontend to use in verify step

    public OtpResponse() {}

    public OtpResponse(String status, String message, String email) {
        this.status = status;
        this.message = message;
        this.email = email;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
