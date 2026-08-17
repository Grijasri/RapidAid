package com.rapidaid.controller;

import com.rapidaid.model.*;
import com.rapidaid.service.AmbulanceService;
import com.rapidaid.service.EmergencyRequestService;
import com.rapidaid.service.HospitalService;
import com.rapidaid.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/requests")
public class EmergencyRequestController {

    private final EmergencyRequestService requestService;
    private final PatientService patientService;
    private final AmbulanceService ambulanceService;
    private final HospitalService hospitalService;

    @Autowired
    public EmergencyRequestController(EmergencyRequestService requestService,
                                     PatientService patientService,
                                     AmbulanceService ambulanceService,
                                     HospitalService hospitalService) {
        this.requestService = requestService;
        this.patientService = patientService;
        this.ambulanceService = ambulanceService;
        this.hospitalService = hospitalService;
    }

    @GetMapping
    public String listRequests(@RequestParam(value = "statusFilter", required = false) String statusFilter, Model model) {
        if (statusFilter != null && !statusFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(statusFilter)) {
            try {
                RequestStatus status = RequestStatus.valueOf(statusFilter.toUpperCase());
                model.addAttribute("requests", requestService.getRequestsByStatus(status));
                model.addAttribute("currentFilter", status.name());
            } catch (IllegalArgumentException e) {
                model.addAttribute("requests", requestService.getAllRequests());
                model.addAttribute("currentFilter", "ALL");
            }
        } else {
            model.addAttribute("requests", requestService.getAllRequests());
            model.addAttribute("currentFilter", "ALL");
        }
        model.addAttribute("statuses", RequestStatus.values());
        return "requests/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("emergencyRequest", new EmergencyRequest());
        model.addAttribute("patients", patientService.getAllPatients());
        return "requests/create";
    }

    @PostMapping
    public String createRequest(@Valid @ModelAttribute("emergencyRequest") EmergencyRequest emergencyRequest,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("patients", patientService.getAllPatients());
            return "requests/create";
        }
        requestService.createRequest(emergencyRequest);
        redirectAttributes.addFlashAttribute("successMessage", "Emergency Request registered successfully! Status: PENDING");
        return "redirect:/requests";
    }

    @GetMapping("/assign/{id}")
    public String showAssignForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        return requestService.getRequestById(id)
                .map(request -> {
                    if (request.getStatus() != RequestStatus.PENDING) {
                        redirectAttributes.addFlashAttribute("errorMessage", "Request #" + id + " is not in PENDING state.");
                        return "redirect:/requests";
                    }
                    model.addAttribute("request", request);
                    model.addAttribute("availableAmbulances", ambulanceService.getAvailableAmbulances());
                    model.addAttribute("availableHospitals", hospitalService.getHospitalsWithAvailableBeds());
                    return "requests/assign";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Emergency Request not found with ID: " + id);
                    return "redirect:/requests";
                });
    }

    @PostMapping("/assign")
    public String processAssign(@RequestParam("requestId") Long requestId,
                                @RequestParam("ambulanceId") Long ambulanceId,
                                @RequestParam("hospitalId") Long hospitalId,
                                RedirectAttributes redirectAttributes) {
        try {
            requestService.assignDispatch(requestId, ambulanceId, hospitalId);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Dispatch assigned! Ambulance and Hospital updated. Request #" + requestId + " is now ASSIGNED.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Assignment failed: " + e.getMessage());
        }
        return "redirect:/requests";
    }

    @PostMapping("/complete/{id}")
    public String completeRequest(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            requestService.completeRequest(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Emergency Request #" + id + " marked as COMPLETED. Ambulance has been returned to AVAILABLE status.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not complete request: " + e.getMessage());
        }
        return "redirect:/requests";
    }

    @PostMapping("/delete/{id}")
    public String deleteRequest(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            requestService.deleteRequest(id);
            redirectAttributes.addFlashAttribute("successMessage", "Emergency Request #" + id + " deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not delete request: " + e.getMessage());
        }
        return "redirect:/requests";
    }
}
