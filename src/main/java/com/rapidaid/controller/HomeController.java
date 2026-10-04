package com.rapidaid.controller;

import com.rapidaid.model.AmbulanceStatus;
import com.rapidaid.model.PartnerInquiry;
import com.rapidaid.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    private final PatientService patientService;
    private final AmbulanceService ambulanceService;
    private final HospitalService hospitalService;
    private final EmergencyRequestService requestService;
    private final PartnerInquiryService partnerInquiryService;

    @Autowired
    public HomeController(PatientService patientService,
                          AmbulanceService ambulanceService,
                          HospitalService hospitalService,
                          EmergencyRequestService requestService,
                          PartnerInquiryService partnerInquiryService) {
        this.patientService = patientService;
        this.ambulanceService = ambulanceService;
        this.hospitalService = hospitalService;
        this.requestService = requestService;
        this.partnerInquiryService = partnerInquiryService;
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
        model.addAttribute("avgResponseTime", requestService.getCalculatedAvgResponseTime());
        
        if (!model.containsAttribute("partnerInquiry")) {
            model.addAttribute("partnerInquiry", new PartnerInquiry());
        }

        return "home";
    }

    @PostMapping("/partner-inquiry")
    public String processPartnerInquiry(@Valid @ModelAttribute("partnerInquiry") PartnerInquiry inquiry,
                                        BindingResult bindingResult,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("totalPatients", patientService.countTotalPatients());
            model.addAttribute("totalAmbulances", ambulanceService.countTotalAmbulances());
            model.addAttribute("availableAmbulances", ambulanceService.countByStatus(AmbulanceStatus.AVAILABLE));
            model.addAttribute("totalHospitals", hospitalService.countTotalHospitals());
            model.addAttribute("availableBeds", hospitalService.sumAvailableBeds());
            model.addAttribute("totalBeds", hospitalService.sumTotalBeds());
            model.addAttribute("totalRequests", requestService.countTotalRequests());
            model.addAttribute("avgResponseTime", requestService.getCalculatedAvgResponseTime());
            return "home";
        }

        partnerInquiryService.saveInquiry(inquiry);
        redirectAttributes.addFlashAttribute("successMessage", "Thank you for your interest! Our hospital network integration team will contact you shortly.");
        return "redirect:/#partner-form";
    }
}
