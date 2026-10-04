package com.rapidaid.controller;

import com.rapidaid.repository.NotificationLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/notifications")
public class NotificationLogController {

    private final NotificationLogRepository notificationLogRepository;

    @Autowired
    public NotificationLogController(NotificationLogRepository notificationLogRepository) {
        this.notificationLogRepository = notificationLogRepository;
    }

    @GetMapping
    public String viewNotificationLogs(Model model) {
        model.addAttribute("notificationLogs", notificationLogRepository.findAllByOrderByTimestampDesc());
        return "admin/notifications";
    }
}
