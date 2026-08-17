package com.rapidaid.controller;

import com.rapidaid.model.AmbulanceStatus;
import com.rapidaid.model.RequestStatus;
import com.rapidaid.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final PatientService patientService;
    private final AmbulanceService ambulanceService;
    private final HospitalService hospitalService;
    private final EmergencyRequestService requestService;

    @Autowired
    public DashboardController(PatientService patientService,
                               AmbulanceService ambulanceService,
                               HospitalService hospitalService,
                               EmergencyRequestService requestService) {
        this.patientService = patientService;
        this.ambulanceService = ambulanceService;
        this.hospitalService = hospitalService;
        this.requestService = requestService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Patient Stats
        model.addAttribute("totalPatients", patientService.countTotalPatients());

        // Ambulance Stats
        model.addAttribute("totalAmbulances", ambulanceService.countTotalAmbulances());
        model.addAttribute("availableAmbulances", ambulanceService.countByStatus(AmbulanceStatus.AVAILABLE));
        model.addAttribute("onDutyAmbulances", ambulanceService.countByStatus(AmbulanceStatus.ON_DUTY));
        model.addAttribute("maintenanceAmbulances", ambulanceService.countByStatus(AmbulanceStatus.MAINTENANCE));

        // Hospital Stats
        model.addAttribute("totalHospitals", hospitalService.countTotalHospitals());
        model.addAttribute("totalBeds", hospitalService.sumTotalBeds());
        model.addAttribute("availableBeds", hospitalService.sumAvailableBeds());

        // Request Stats
        model.addAttribute("totalRequests", requestService.countTotalRequests());
        model.addAttribute("pendingRequests", requestService.countByStatus(RequestStatus.PENDING));
        model.addAttribute("assignedRequests", requestService.countByStatus(RequestStatus.ASSIGNED));
        model.addAttribute("completedRequests", requestService.countByStatus(RequestStatus.COMPLETED));

        // Recent Requests table
        model.addAttribute("recentRequests", requestService.getAllRequests());

        return "dashboard";
    }
}
