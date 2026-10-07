package com.rapidaid.service.impl;

import com.rapidaid.model.NotificationLog;
import com.rapidaid.repository.NotificationLogRepository;
import com.rapidaid.service.NotificationService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(SmsNotificationService.class);

    private final NotificationLogRepository notificationLogRepository;

    @Value("${twilio.account.sid:${TWILIO_ACCOUNT_SID:}}")
    private String accountSid;

    @Value("${twilio.auth.token:${TWILIO_AUTH_TOKEN:}}")
    private String authToken;

    @Value("${twilio.phone.number:${TWILIO_PHONE_NUMBER:${TWILIO_FROM_NUMBER:}}}")
    private String fromNumber;

    private boolean twilioConfigured = false;

    @Autowired
    public SmsNotificationService(NotificationLogRepository notificationLogRepository) {
        this.notificationLogRepository = notificationLogRepository;
    }

    @PostConstruct
    public void init() {
        if (isConfigured(accountSid) && isConfigured(authToken) && isConfigured(fromNumber)) {
            try {
                Twilio.init(accountSid, authToken);
                twilioConfigured = true;
                log.info("Twilio SMS notification service initialized successfully with From number: {}", fromNumber);
            } catch (Exception e) {
                log.warn("Failed to initialize Twilio SMS service: {}. SMS notifications disabled.", e.getMessage());
            }
        } else {
            log.warn("SMS notifications unconfigured - set TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN, TWILIO_PHONE_NUMBER env vars.");
        }
    }

    @Override
    public void sendSms(String toPhone, String messageBody) {
        if (toPhone == null || toPhone.isBlank()) {
            log.warn("Cannot send SMS: Recipient phone number is empty.");
            return;
        }

        if (!twilioConfigured) {
            log.info("SMS [UNCONFIGURED] -> To: {}, Message: {}", toPhone, messageBody);
            try {
                notificationLogRepository.save(new NotificationLog(
                        "SMS",
                        toPhone,
                        "SMS Dispatch Notification",
                        messageBody,
                        "FAILED",
                        "Twilio credentials not set. Required env vars: TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN, TWILIO_PHONE_NUMBER."
                ));
            } catch (Exception ex) {
                log.error("Error writing notification log: {}", ex.getMessage());
            }
            return;
        }

        try {
            Message twilioMessage = Message.creator(
                    new PhoneNumber(toPhone),
                    new PhoneNumber(fromNumber),
                    messageBody
            ).create();

            String sidInfo = "SID: " + twilioMessage.getSid();
            log.info("SMS Sent Successfully! {} to {}", sidInfo, toPhone);
            notificationLogRepository.save(new NotificationLog(
                    "SMS",
                    toPhone,
                    "SMS Dispatch Notification",
                    messageBody + " (" + sidInfo + ")",
                    "SUCCESS",
                    null
            ));
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", toPhone, e.getMessage());
            try {
                notificationLogRepository.save(new NotificationLog(
                        "SMS",
                        toPhone,
                        "SMS Dispatch Notification",
                        messageBody,
                        "FAILED",
                        "Twilio API Error: " + e.getMessage()
                ));
            } catch (Exception ex) {
                log.error("Error writing failure notification log: {}", ex.getMessage());
            }
        }
    }

    @Override
    public void sendEmail(String toEmail, String subject, String body) {
        // Email handled by EmailNotificationService
    }

    private boolean isConfigured(String val) {
        return val != null && !val.isBlank() && !val.startsWith("your_") && !val.equalsIgnoreCase("placeholder");
    }
}
