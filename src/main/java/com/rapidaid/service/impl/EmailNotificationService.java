package com.rapidaid.service.impl;

import com.rapidaid.model.NotificationLog;
import com.rapidaid.repository.NotificationLogRepository;
import com.rapidaid.service.NotificationService;
import jakarta.annotation.PostConstruct;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
@Primary
public class EmailNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

    private final NotificationLogRepository notificationLogRepository;
    private final SmsNotificationService smsNotificationService;

    @Value("${spring.mail.host:${MAIL_HOST:}}")
    private String mailHost;

    @Value("${spring.mail.port:${MAIL_PORT:587}}")
    private int mailPort;

    @Value("${spring.mail.username:${MAIL_USERNAME:}}")
    private String mailUsername;

    @Value("${spring.mail.password:${MAIL_PASSWORD:}}")
    private String mailPassword;

    @Value("${spring.mail.from:${MAIL_FROM:noreply@rapidaid.com}}")
    private String mailFrom;

    private JavaMailSenderImpl mailSender;
    private boolean emailConfigured = false;

    @Autowired
    public EmailNotificationService(NotificationLogRepository notificationLogRepository,
                                    SmsNotificationService smsNotificationService) {
        this.notificationLogRepository = notificationLogRepository;
        this.smsNotificationService = smsNotificationService;
    }

    @PostConstruct
    public void init() {
        if (isConfigured(mailHost) && isConfigured(mailUsername)) {
            try {
                mailSender = new JavaMailSenderImpl();
                mailSender.setHost(mailHost);
                mailSender.setPort(mailPort);
                mailSender.setUsername(mailUsername);
                mailSender.setPassword(mailPassword);

                Properties props = mailSender.getJavaMailProperties();
                props.put("mail.transport.protocol", "smtp");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.debug", "false");

                emailConfigured = true;
                log.info("Email notification service (SMTP) initialized successfully for host: {}", mailHost);
            } catch (Exception e) {
                log.warn("Failed to initialize SMTP Mail Sender: {}. Email notifications disabled.", e.getMessage());
            }
        } else {
            log.warn("Email notifications disabled - no SMTP credentials configured.");
        }
    }

    @Override
    public void sendEmail(String toEmail, String subject, String body) {
        if (toEmail == null || toEmail.isBlank()) {
            log.warn("Cannot send Email: Recipient email address is empty.");
            return;
        }

        if (!emailConfigured || mailSender == null) {
            log.info("EMAIL [NO-OP / Unconfigured] -> To: {}, Subject: {}", toEmail, subject);
            notificationLogRepository.save(new NotificationLog(
                    "EMAIL",
                    toEmail,
                    subject,
                    body,
                    "DISABLED_NOOP",
                    "SMTP credentials not configured. Set MAIL_HOST, MAIL_PORT, MAIL_USERNAME, MAIL_PASSWORD env vars."
            ));
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(mailFrom);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(message);
            log.info("Email sent successfully to {}", toEmail);
            notificationLogRepository.save(new NotificationLog(
                    "EMAIL",
                    toEmail,
                    subject,
                    body,
                    "SUCCESS",
                    null
            ));
        } catch (Exception e) {
            log.error("Failed to send Email to {}: {}", toEmail, e.getMessage());
            notificationLogRepository.save(new NotificationLog(
                    "EMAIL",
                    toEmail,
                    subject,
                    body,
                    "FAILED",
                    e.getMessage()
            ));
        }
    }

    @Override
    public void sendSms(String toPhone, String messageBody) {
        // Delegate SMS to SmsNotificationService
        smsNotificationService.sendSms(toPhone, messageBody);
    }

    private boolean isConfigured(String val) {
        return val != null && !val.isBlank() && !val.startsWith("your_");
    }
}
