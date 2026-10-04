package com.rapidaid.service;

public interface NotificationService {
    void sendSms(String toPhone, String messageBody);
    void sendEmail(String toEmail, String subject, String body);
}
