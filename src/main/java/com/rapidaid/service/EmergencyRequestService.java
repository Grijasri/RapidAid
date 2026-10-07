package com.rapidaid.service;

import com.rapidaid.model.*;
import com.rapidaid.repository.AmbulanceRepository;
import com.rapidaid.repository.EmergencyRequestRepository;
import com.rapidaid.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EmergencyRequestService {

    private final EmergencyRequestRepository requestRepository;
    private final AmbulanceRepository ambulanceRepository;
    private final HospitalRepository hospitalRepository;
    private final ActivityLogService activityLogService;
    private final PriorityEngine priorityEngine;
    private final NotificationService notificationService;

    @Autowired
    public EmergencyRequestService(EmergencyRequestRepository requestRepository,
                                  AmbulanceRepository ambulanceRepository,
                                  HospitalRepository hospitalRepository,
                                  ActivityLogService activityLogService,
                                  PriorityEngine priorityEngine,
                                  NotificationService notificationService) {
        this.requestRepository = requestRepository;
        this.ambulanceRepository = ambulanceRepository;
        this.hospitalRepository = hospitalRepository;
        this.activityLogService = activityLogService;
        this.priorityEngine = priorityEngine;
        this.notificationService = notificationService;
    }

    public List<EmergencyRequest> getAllRequests() {
        return requestRepository.findAllByOrderByPriorityScoreDescRequestTimeAsc();
    }

    public List<EmergencyRequest> getPendingRequestsSortedByPriority() {
        return requestRepository.findByStatusOrderByPriorityScoreDescRequestTimeAsc(RequestStatus.PENDING);
    }

    public List<EmergencyRequest> getRequestsByStatus(RequestStatus status) {
        if (status == RequestStatus.PENDING) {
            return getPendingRequestsSortedByPriority();
        }
        return requestRepository.findByStatus(status);
    }

    public List<EmergencyRequest> getRequestsByPatientId(Long patientId) {
        return requestRepository.findByPatientIdOrderByRequestTimeDesc(patientId);
    }

    public Optional<EmergencyRequest> getRequestById(Long id) {
        return requestRepository.findById(id);
    }

    public EmergencyRequest createRequest(EmergencyRequest request) {
        if (request.getStatus() == null) {
            request.setStatus(RequestStatus.PENDING);
        }
        if (request.getRequestTime() == null) {
            request.setRequestTime(LocalDateTime.now());
        }

        // Priority engine scoring
        if (request.getPriorityScore() == null || request.getPriorityScore() == 50) {
            PriorityEngine.PriorityResult res = priorityEngine.evaluatePriority(request.getEmergencyType(), request.getDescription());
            request.setPriorityScore(res.getScore());
            request.setPriorityLabel(res.getLabel());
        }

        EmergencyRequest saved = requestRepository.save(request);
        String pName = saved.getPatientName() != null ? saved.getPatientName() : "Guest";
        activityLogService.logActivity("REQUEST_CREATED", 
                "Emergency request registered for Patient: " + pName + " at " + saved.getLocation() + " (Priority: " + saved.getPriorityLabel() + " - Score: " + saved.getPriorityScore() + ")");

        // Trigger Confirmation Email to requester
        try {
            if (saved.getPatientEmail() != null && !saved.getPatientEmail().isBlank()) {
                String subject = "RapidAid Emergency Request Confirmation #" + saved.getId();
                String htmlBody = "<h3>RapidAid Emergency Coordination</h3>" +
                        "<p>Dear " + saved.getPatientName() + ",</p>" +
                        "<p>Your emergency request <strong>#" + saved.getId() + "</strong> has been received by our dispatch center.</p>" +
                        "<p><strong>Emergency Type:</strong> " + (saved.getEmergencyType() != null ? saved.getEmergencyType() : "General") + "</p>" +
                        "<p><strong>Pickup Location:</strong> " + saved.getLocation() + "</p>" +
                        "<p>You can track your dispatch live here: <a href='http://localhost:8080/request/track/" + saved.getId() + "'>Track Request #" + saved.getId() + "</a></p>";
                notificationService.sendEmail(saved.getPatientEmail(), subject, htmlBody);
            }
        } catch (Exception e) {
            activityLogService.logActivity("EMAIL_WARNING", "Failed to send requester email confirmation: " + e.getMessage());
        }

        return saved;
    }

    @Transactional
    public EmergencyRequest assignDispatch(Long requestId, Long ambulanceId, Long hospitalId) {
        EmergencyRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Emergency Request not found with ID: " + requestId));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Only PENDING emergency requests can be assigned.");
        }

        Ambulance ambulance = ambulanceRepository.findById(ambulanceId)
                .orElseThrow(() -> new IllegalArgumentException("Ambulance not found with ID: " + ambulanceId));

        if (ambulance.getStatus() != AmbulanceStatus.AVAILABLE) {
            throw new IllegalStateException("Ambulance " + ambulance.getVehicleNumber() + " is currently unavailable (" + ambulance.getStatus() + ").");
        }

        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new IllegalArgumentException("Hospital not found with ID: " + hospitalId));

        if (hospital.getAvailableBeds() <= 0) {
            throw new IllegalStateException("Hospital " + hospital.getName() + " has no available beds remaining.");
        }

        // Cross-module state updates
        ambulance.setStatus(AmbulanceStatus.ON_DUTY);
        ambulanceRepository.save(ambulance);

        hospital.setAvailableBeds(hospital.getAvailableBeds() - 1);
        hospitalRepository.save(hospital);

        request.setAmbulance(ambulance);
        request.setHospital(hospital);
        request.setStatus(RequestStatus.ASSIGNED);
        EmergencyRequest updatedRequest = requestRepository.save(request);

        activityLogService.logActivity("DISPATCH_ASSIGNED", 
                "Request #" + requestId + " assigned: Ambulance [" + ambulance.getVehicleNumber() + 
                "] & Hospital [" + hospital.getName() + "]");

        // Trigger SMS Notifications
        try {
            if (ambulance.getDriverPhone() != null && !ambulance.getDriverPhone().isBlank()) {
                String driverMsg = "RapidAid DISPATCH ALERT: Request #" + requestId + " assigned to ambulance (" + ambulance.getVehicleNumber() + "). Pickup: " + request.getLocation() + ". Destination: " + hospital.getName() + ". Patient: " + request.getPatientName() + " (" + request.getPatientPhone() + ").";
                notificationService.sendSms(ambulance.getDriverPhone(), driverMsg);
            }

            if (request.getPatientPhone() != null && !request.getPatientPhone().isBlank()) {
                String patientMsg = "RapidAid DISPATCH UPDATE: Help is on the way! Ambulance " + ambulance.getVehicleNumber() + " (Driver: " + ambulance.getDriverName() + ", Ph: " + ambulance.getDriverPhone() + ") assigned to Request #" + requestId + ". Destination Hospital: " + hospital.getName() + ".";
                notificationService.sendSms(request.getPatientPhone(), patientMsg);
            }
        } catch (Exception e) {
            activityLogService.logActivity("SMS_WARNING", "Failed to dispatch SMS alerts: " + e.getMessage());
        }

        // Trigger Email Notification to Hospital & Patient
        try {
            if (hospital.getEmail() != null && !hospital.getEmail().isBlank()) {
                String subject = "INCOMING DISPATCH ALERT — Patient Transfer to " + hospital.getName();
                String htmlBody = "<h3>RapidAid Incoming Emergency Dispatch Alert</h3>" +
                        "<p><strong>Destination Hospital:</strong> " + hospital.getName() + "</p>" +
                        "<p><strong>Patient Name:</strong> " + request.getPatientName() + "</p>" +
                        "<p><strong>Emergency Type:</strong> " + (request.getEmergencyType() != null ? request.getEmergencyType() : "General") + "</p>" +
                        "<p><strong>Priority Level:</strong> " + request.getPriorityLabel() + " (Score: " + request.getPriorityScore() + ")</p>" +
                        "<p><strong>Pickup Location:</strong> " + request.getLocation() + "</p>" +
                        "<p><strong>Assigned Ambulance:</strong> " + ambulance.getVehicleNumber() + " (Driver: " + ambulance.getDriverName() + ", Phone: " + ambulance.getDriverPhone() + ")</p>";
                notificationService.sendEmail(hospital.getEmail(), subject, htmlBody);
            }

            if (request.getPatientEmail() != null && !request.getPatientEmail().isBlank()) {
                String patientSubject = "🚨 RapidAid ALERT: Emergency Dispatch Accepted (Request #" + requestId + ")";
                String patientHtml = "<h3>Your Emergency Request Has Been Accepted!</h3>" +
                        "<p>Dear <strong>" + request.getPatientName() + "</strong>,</p>" +
                        "<p>Emergency dispatch has accepted your request. An ambulance is currently en route to your location.</p>" +
                        "<ul>" +
                        "<li><strong>Assigned Ambulance:</strong> " + ambulance.getVehicleNumber() + "</li>" +
                        "<li><strong>Driver Name:</strong> " + ambulance.getDriverName() + "</li>" +
                        "<li><strong>Driver Phone:</strong> " + (ambulance.getDriverPhone() != null ? ambulance.getDriverPhone() : "N/A") + "</li>" +
                        "<li><strong>Receiving Hospital:</strong> " + hospital.getName() + "</li>" +
                        "</ul>" +
                        "<p><a href='http://localhost:8080/request/track/" + requestId + "' style='background: #2563eb; color: white; padding: 10px 20px; text-decoration: none; border-radius: 6px; font-weight: bold; display: inline-block;'>Track Ambulance Live Map</a></p>";
                notificationService.sendEmail(request.getPatientEmail(), patientSubject, patientHtml);
            }
        } catch (Exception e) {
            activityLogService.logActivity("EMAIL_WARNING", "Failed to send email notifications: " + e.getMessage());
        }

        return updatedRequest;
    }

    @Transactional
    public EmergencyRequest completeRequest(Long requestId) {
        EmergencyRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Emergency Request not found with ID: " + requestId));

        if (request.getStatus() != RequestStatus.ASSIGNED) {
            throw new IllegalStateException("Only ASSIGNED emergency requests can be marked as COMPLETED.");
        }

        Ambulance ambulance = request.getAmbulance();
        if (ambulance != null) {
            ambulance.setStatus(AmbulanceStatus.AVAILABLE);
            ambulanceRepository.save(ambulance);
        }

        request.setStatus(RequestStatus.COMPLETED);
        request.setCompletionTime(LocalDateTime.now());
        EmergencyRequest completed = requestRepository.save(request);

        String ambulanceInfo = ambulance != null ? "Ambulance " + ambulance.getVehicleNumber() + " reset to AVAILABLE." : "";
        activityLogService.logActivity("REQUEST_COMPLETED", 
                "Emergency Request #" + requestId + " completed successfully. " + ambulanceInfo);

        // Trigger SMS & Email Notification to requester
        try {
            if (request.getPatientPhone() != null && !request.getPatientPhone().isBlank()) {
                String patientMsg = "RapidAid Notice: Emergency Request #" + requestId + " has been marked COMPLETED. Thank you.";
                notificationService.sendSms(request.getPatientPhone(), patientMsg);
            }
        } catch (Exception e) {
            activityLogService.logActivity("SMS_WARNING", "Failed to send completion SMS: " + e.getMessage());
        }

        try {
            if (request.getPatientEmail() != null && !request.getPatientEmail().isBlank()) {
                String compSubject = "RapidAid Request #" + requestId + " Resolved";
                String compBody = "<h3>Emergency Service Completed</h3>" +
                        "<p>Dear " + request.getPatientName() + ",</p>" +
                        "<p>Your emergency request <strong>#" + requestId + "</strong> has been successfully completed. We wish you good health!</p>";
                notificationService.sendEmail(request.getPatientEmail(), compSubject, compBody);
            }
        } catch (Exception e) {
            activityLogService.logActivity("EMAIL_WARNING", "Failed to send completion email: " + e.getMessage());
        }

        return completed;
    }

    public void deleteRequest(Long id) {
        Optional<EmergencyRequest> reqOpt = requestRepository.findById(id);
        if (reqOpt.isPresent()) {
            requestRepository.deleteById(id);
            activityLogService.logActivity("REQUEST_DELETED", "Deleted Emergency Request #" + id);
        }
    }

    public long countTotalRequests() {
        return requestRepository.count();
    }

    public long countByStatus(RequestStatus status) {
        return requestRepository.countByStatus(status);
    }

    public String getCalculatedAvgResponseTime() {
        List<EmergencyRequest> completed = requestRepository.findByStatus(RequestStatus.COMPLETED);
        if (completed.isEmpty()) {
            return "Coming soon";
        }
        long totalSeconds = 0;
        int count = 0;
        for (EmergencyRequest req : completed) {
            if (req.getRequestTime() != null && req.getCompletionTime() != null) {
                long duration = java.time.Duration.between(req.getRequestTime(), req.getCompletionTime()).getSeconds();
                if (duration > 0) {
                    totalSeconds += duration;
                    count++;
                }
            }
        }
        if (count == 0) {
            return "Coming soon";
        }
        long avgMinutes = (totalSeconds / count) / 60;
        if (avgMinutes < 1) {
            return "< 1 min";
        }
        return avgMinutes + " mins";
    }
}
