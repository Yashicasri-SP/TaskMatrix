package com.tcs.taskmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OtpService — generates, stores, sends, and validates 6-digit OTPs.
 * OTPs expire after 5 minutes and are stored in-memory.
 */
@Service
public class OtpService {

    // --- Inner class to hold OTP + expiry ---
    private static class OtpEntry {
        final String otp;
        final LocalDateTime expiresAt;

        OtpEntry(String otp) {
            this.otp = otp;
            this.expiresAt = LocalDateTime.now().plusMinutes(5);
        }

        boolean isExpired() {
            return LocalDateTime.now().isAfter(expiresAt);
        }
    }

    private final ConcurrentHashMap<String, OtpEntry> otpStore = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    @Autowired
    private JavaMailSender mailSender;

    // -------------------------------------------------------
    // Generate OTP, store it, and send to employee's email
    // -------------------------------------------------------
    public void generateAndSendOtp(String toEmail, String userName) {
        String otp = String.format("%06d", random.nextInt(1_000_000));
        otpStore.put(toEmail.toLowerCase(), new OtpEntry(otp));
        sendOtpEmail(toEmail, userName, otp);
    }

    // -------------------------------------------------------
    // Validate OTP — returns true on success and removes entry
    // -------------------------------------------------------
    public boolean validateOtp(String email, String enteredOtp) {
        String key = email.toLowerCase();
        OtpEntry entry = otpStore.get(key);

        if (entry == null) {
            return false; // No OTP was generated
        }
        if (entry.isExpired()) {
            otpStore.remove(key);
            return false; // OTP expired
        }
        if (!entry.otp.equals(enteredOtp.trim())) {
            return false; // Wrong OTP
        }

        otpStore.remove(key); // One-time use — invalidate after success
        return true;
    }

    // -------------------------------------------------------
    // Send styled HTML OTP email via Gmail SMTP
    // -------------------------------------------------------
    private void sendOtpEmail(String toEmail, String userName, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("yashicasp422@gmail.com", "TaskMatrix Security");
            helper.setTo(toEmail);
            helper.setSubject("🔐 Your TaskMatrix Login OTP — " + otp);

            String html = buildEmailHtml(userName, otp);
            helper.setText(html, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send OTP email: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Email sending error: " + e.getMessage(), e);
        }
    }

    // -------------------------------------------------------
    // Premium HTML Email Template
    // -------------------------------------------------------
    private String buildEmailHtml(String userName, String otp) {
        // Split OTP into individual characters for digit display
        String[] digits = otp.split("");
        StringBuilder digitBoxes = new StringBuilder();
        for (String d : digits) {
            digitBoxes.append(
                "<td style=\"padding:0 4px;\">" +
                "<div style=\"width:44px;height:54px;background:#1e1e2e;border:2px solid #7c3aed;" +
                "border-radius:10px;display:inline-flex;align-items:center;justify-content:center;" +
                "font-size:28px;font-weight:800;color:#a78bfa;font-family:monospace;\">" +
                d +
                "</div></td>"
            );
        }

        return "<!DOCTYPE html>" +
            "<html><head><meta charset='UTF-8'></head><body style='margin:0;padding:0;" +
            "background:#0f0f1a;font-family:\"Segoe UI\",Arial,sans-serif;'>" +
            "<table width='100%' cellpadding='0' cellspacing='0'><tr><td align='center' style='padding:40px 20px;'>" +
            "<table width='520' cellpadding='0' cellspacing='0' style='background:#1a1a2e;border-radius:20px;" +
            "overflow:hidden;box-shadow:0 20px 60px rgba(0,0,0,0.5);'>" +

            // Header
            "<tr><td style='background:linear-gradient(135deg,#7c3aed,#4f46e5);padding:32px 40px;text-align:center;'>" +
            "<div style='font-size:32px;margin-bottom:8px;'>🔐</div>" +
            "<h1 style='margin:0;color:#fff;font-size:22px;font-weight:700;letter-spacing:1px;'>TaskMatrix Security</h1>" +
            "<p style='margin:6px 0 0;color:rgba(255,255,255,0.75);font-size:13px;'>Login Verification Code</p>" +
            "</td></tr>" +

            // Body
            "<tr><td style='padding:36px 40px;'>" +
            "<p style='color:#a0a0c0;font-size:15px;margin:0 0 8px;'>Hello, <strong style='color:#e2e2f0;'>" + userName + "</strong></p>" +
            "<p style='color:#a0a0c0;font-size:14px;margin:0 0 28px;line-height:1.6;'>" +
            "Someone (hopefully you!) requested to log in to <strong style='color:#7c3aed;'>TaskMatrix</strong>. " +
            "Use the code below to complete your login. This code expires in <strong style='color:#f59e0b;'>5 minutes</strong>.</p>" +

            // OTP Digit Boxes
            "<div style='text-align:center;margin:0 0 28px;'>" +
            "<table cellpadding='0' cellspacing='0' style='display:inline-table;'><tr>" +
            digitBoxes +
            "</tr></table></div>" +

            // Warning
            "<div style='background:#2d2d4e;border-left:4px solid #f59e0b;border-radius:8px;padding:14px 18px;margin-bottom:24px;'>" +
            "<p style='margin:0;color:#fbbf24;font-size:13px;'>⚠️ <strong>Do not share this code</strong> with anyone. " +
            "TaskMatrix will never ask for your OTP via phone or chat.</p></div>" +

            "<p style='color:#6b6b8a;font-size:12px;margin:0;'>If you didn't request this, you can safely ignore this email. " +
            "Your account remains secure.</p>" +
            "</td></tr>" +

            // Footer
            "<tr><td style='background:#13131f;padding:20px 40px;text-align:center;'>" +
            "<p style='margin:0;color:#4a4a6a;font-size:12px;'>© 2026 TaskMatrix · TCS Smart Employee Task Management</p>" +
            "</td></tr>" +

            "</table></td></tr></table></body></html>";
    }
}
