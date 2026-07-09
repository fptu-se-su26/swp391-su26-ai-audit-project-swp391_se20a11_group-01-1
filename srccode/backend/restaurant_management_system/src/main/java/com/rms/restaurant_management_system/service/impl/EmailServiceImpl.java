package com.rms.restaurant_management_system.service.impl;

import com.rms.restaurant_management_system.service.interfaces.EmailService;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Override
    public void sendOtpEmail(String toEmail, String otp) {
        // TODO: cấu hình mail thật trong application.properties khi deploy
        System.out.println("========================================");
        System.out.println("[MOCK EMAIL] Gửi OTP tới: " + toEmail);
        System.out.println("[MOCK EMAIL] Mã OTP: " + otp);
        System.out.println("========================================");
    }
}
