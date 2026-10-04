package com.rapidaid.controller;

import com.rapidaid.dto.PublicRequestForm;
import com.rapidaid.model.EmergencyRequest;
import com.rapidaid.model.Patient;
import com.rapidaid.model.RequestStatus;
import com.rapidaid.service.EmergencyRequestService;
import com.rapidaid.service.PatientService;
import com.rapidaid.service.RateLimitingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/request")
public class PublicRequestController {

    private final EmergencyRequestService requestService;
    private final PatientService patientService;
    private final RateLimitingService rateLimitingService;

    @Autowired
    public PublicRequestController(EmergencyRequestService requestService,
                                  PatientService patientService,
                                  RateLimitingService rateLimitingService) {
        this.requestService = requestService;
        this.patientService = patientService;
        this.rateLimitingService = rateLimitingService;
    }

    @GetMapping
    public String showPublicRequestForm(Model model) {
        model.addAttribute("requestForm", new PublicRequestForm());
        return "requests/public_create";
    }

    @PostMapping
    public String processPublicRequest(@Valid @ModelAttribute("requestForm") PublicRequestForm form,
                                       BindingResult bindingResult,
                                       HttpServletRequest request,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {
        // Honeypot bot protection check
        if (form.getWebsite() != null && !form.getWebsite().isBlank()) {
            bindingResult.rejectValue("website", "bot.detected", "Invalid submission.");
        }

        // IP Rate limiting check
        String clientIp = getClientIp(request);
        if (!rateLimitingService.allowRequest(clientIp)) {
            bindingResult.reject("rate.limit", "Too many requests submitted from your IP address. Please wait a few minutes before trying again.");
        }

        if (bindingResult.hasErrors()) {
            return "requests/public_create";
        }

        EmergencyRequest emergencyRequest = new EmergencyRequest();
        emergencyRequest.setPatientName(form.getPatientName());
        emergencyRequest.setPatientPhone(form.getPatientPhone());
        emergencyRequest.setPatientEmail(form.getPatientEmail());
        emergencyRequest.setLocation(form.getLocation());
        emergencyRequest.setPickupLat(form.getPickupLat());
        emergencyRequest.setPickupLng(form.getPickupLng());
        emergencyRequest.setEmergencyType(form.getEmergencyType());
        emergencyRequest.setDescription(form.getDescription() != null && !form.getDescription().isBlank() 
                ? form.getDescription() 
                : "Emergency Type: " + form.getEmergencyType());
        emergencyRequest.setStatus(RequestStatus.PENDING);

        EmergencyRequest created = requestService.createRequest(emergencyRequest);
        redirectAttributes.addFlashAttribute("successMessage", "Emergency request submitted successfully. Tracking ID: #" + created.getId());
        return "redirect:/request/track/" + created.getId();
    }

    @GetMapping("/track/{id}")
    public String trackRequest(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<EmergencyRequest> reqOpt = requestService.getRequestById(id);
        if (reqOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Emergency Request not found with ID: " + id);
            return "redirect:/request";
        }
        EmergencyRequest req = reqOpt.get();
        model.addAttribute("request", req);
        model.addAttribute("statusText", getStatusTextInPlainLanguage(req));
        return "requests/public_track";
    }

    @GetMapping("/api/status/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getRequestStatusApi(@PathVariable("id") Long id) {
        Optional<EmergencyRequest> reqOpt = requestService.getRequestById(id);
        if (reqOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        EmergencyRequest req = reqOpt.get();
        Map<String, Object> resp = new HashMap<>();
        resp.put("id", req.getId());
        resp.put("status", req.getStatus().name());
        resp.put("statusText", getStatusTextInPlainLanguage(req));
        resp.put("location", req.getLocation());
        resp.put("pickupLat", req.getPickupLat());
        resp.put("pickupLng", req.getPickupLng());
        
        if (req.getAmbulance() != null) {
            Map<String, Object> amb = new HashMap<>();
            amb.put("id", req.getAmbulance().getId());
            amb.put("vehicleNumber", req.getAmbulance().getVehicleNumber());
            amb.put("driverName", req.getAmbulance().getDriverName());
            amb.put("driverPhone", req.getAmbulance().getDriverPhone());
            resp.put("ambulance", amb);
        }
        if (req.getHospital() != null) {
            resp.put("hospitalName", req.getHospital().getName());
        }

        return ResponseEntity.ok(resp);
    }

    private String getStatusTextInPlainLanguage(EmergencyRequest req) {
        if (req.getStatus() == RequestStatus.PENDING) {
            return "Request received";
        } else if (req.getStatus() == RequestStatus.ASSIGNED) {
            return "Ambulance assigned - On the way";
        } else if (req.getStatus() == RequestStatus.COMPLETED) {
            return "Completed";
        }
        return req.getStatus().name();
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
