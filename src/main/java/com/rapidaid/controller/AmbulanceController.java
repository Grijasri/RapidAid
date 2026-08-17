package com.rapidaid.controller;

import com.rapidaid.model.Ambulance;
import com.rapidaid.model.AmbulanceStatus;
import com.rapidaid.model.AmbulanceType;
import com.rapidaid.service.AmbulanceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ambulances")
public class AmbulanceController {

    private final AmbulanceService ambulanceService;

    @Autowired
    public AmbulanceController(AmbulanceService ambulanceService) {
        this.ambulanceService = ambulanceService;
    }

    @GetMapping
    public String listAmbulances(@RequestParam(value = "statusFilter", required = false) String statusFilter, Model model) {
        if ("AVAILABLE".equalsIgnoreCase(statusFilter)) {
            model.addAttribute("ambulances", ambulanceService.getAvailableAmbulances());
            model.addAttribute("statusFilter", "AVAILABLE");
        } else {
            model.addAttribute("ambulances", ambulanceService.getAllAmbulances());
            model.addAttribute("statusFilter", "ALL");
        }
        model.addAttribute("statuses", AmbulanceStatus.values());
        return "ambulances/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("ambulance", new Ambulance());
        model.addAttribute("statuses", AmbulanceStatus.values());
        model.addAttribute("types", AmbulanceType.values());
        return "ambulances/form";
    }

    @PostMapping
    public String saveAmbulance(@Valid @ModelAttribute("ambulance") Ambulance ambulance,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("statuses", AmbulanceStatus.values());
            model.addAttribute("types", AmbulanceType.values());
            return "ambulances/form";
        }
        try {
            boolean isNew = (ambulance.getId() == null);
            ambulanceService.saveAmbulance(ambulance);
            redirectAttributes.addFlashAttribute("successMessage", 
                    isNew ? "Ambulance " + ambulance.getVehicleNumber() + " added successfully!" : "Ambulance " + ambulance.getVehicleNumber() + " updated successfully!");
            return "redirect:/ambulances";
        } catch (IllegalArgumentException ex) {
            bindingResult.rejectValue("vehicleNumber", "error.ambulance", ex.getMessage());
            model.addAttribute("statuses", AmbulanceStatus.values());
            model.addAttribute("types", AmbulanceType.values());
            return "ambulances/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        return ambulanceService.getAmbulanceById(id)
                .map(ambulance -> {
                    model.addAttribute("ambulance", ambulance);
                    model.addAttribute("statuses", AmbulanceStatus.values());
                    model.addAttribute("types", AmbulanceType.values());
                    return "ambulances/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Ambulance not found with ID: " + id);
                    return "redirect:/ambulances";
                });
    }

    @PostMapping("/status-update")
    public String updateStatus(@RequestParam("id") Long id,
                               @RequestParam("status") AmbulanceStatus status,
                               RedirectAttributes redirectAttributes) {
        try {
            ambulanceService.updateStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", "Ambulance status updated to " + status.getDisplayName());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Status update failed: " + e.getMessage());
        }
        return "redirect:/ambulances";
    }

    @PostMapping("/delete/{id}")
    public String deleteAmbulance(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            ambulanceService.deleteAmbulance(id);
            redirectAttributes.addFlashAttribute("successMessage", "Ambulance record deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not delete ambulance: " + e.getMessage());
        }
        return "redirect:/ambulances";
    }
}
