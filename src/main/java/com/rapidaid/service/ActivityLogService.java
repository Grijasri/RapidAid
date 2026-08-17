package com.rapidaid.service;

import com.rapidaid.model.ActivityLog;
import com.rapidaid.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    @Autowired
    public ActivityLogService(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    public void logActivity(String action, String details) {
        String username = getCurrentUsername();
        ActivityLog log = new ActivityLog(action, details, username);
        activityLogRepository.save(log);
    }

    public void logActivity(String action, String details, String performedBy) {
        ActivityLog log = new ActivityLog(action, details, performedBy);
        activityLogRepository.save(log);
    }

    public List<ActivityLog> getAllLogs() {
        return activityLogRepository.findAllByOrderByTimestampDesc();
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
        }
        return "SYSTEM";
    }
}
