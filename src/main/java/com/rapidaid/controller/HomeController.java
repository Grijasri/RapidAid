package com.rapidaid.controller;

import com.rapidaid.model.AmbulanceStatus;
import com.rapidaid.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final PatientService patientService;
    private final AmbulanceService ambulanceService;
    private final HospitalService hospitalService;
    private final EmergencyRequestService requestService;

    @Autowired
    public HomeController(PatientService patientService,
                          AmbulanceService ambulanceService,
                          HospitalService hospitalService,
                          EmergencyRequestService requestService) {
        this.patientService = patientService;
        this.ambulanceService = ambulanceService;
        this.hospitalService = hospitalService;
        this.requestService = requestService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("totalPatients", patientService.countTotalPatients());
        model.addAttribute("totalAmbulances", ambulanceService.countTotalAmbulances());
        model.addAttribute("availableAmbulances", ambulanceService.countByStatus(AmbulanceStatus.AVAILABLE));
        model.addAttribute("totalHospitals", hospitalService.countTotalHospitals());
        model.addAttribute("availableBeds", hospitalService.sumAvailableBeds());
        model.addAttribute("totalBeds", hospitalService.sumTotalBeds());
        model.addAttribute("totalRequests", requestService.countTotalRequests());
        return "home";
    }
}
