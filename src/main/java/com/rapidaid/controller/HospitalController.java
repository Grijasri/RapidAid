package com.rapidaid.controller;

import com.rapidaid.model.Hospital;
import com.rapidaid.service.HospitalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/hospitals")
public class HospitalController {

    private final HospitalService hospitalService;

    @Autowired
    public HospitalController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    @GetMapping
    public String listHospitals(Model model) {
        model.addAttribute("hospitals", hospitalService.getAllHospitals());
        model.addAttribute("totalBeds", hospitalService.sumTotalBeds());
        model.addAttribute("availableBeds", hospitalService.sumAvailableBeds());
        return "hospitals/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("hospital", new Hospital());
        return "hospitals/form";
    }

    @PostMapping
    public String saveHospital(@Valid @ModelAttribute("hospital") Hospital hospital,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (hospital.getAvailableBeds() != null && hospital.getTotalBeds() != null 
                && hospital.getAvailableBeds() > hospital.getTotalBeds()) {
            bindingResult.rejectValue("availableBeds", "error.hospital", "Available beds cannot exceed total beds (" + hospital.getTotalBeds() + ")");
        }

        if (bindingResult.hasErrors()) {
            return "hospitals/form";
        }

        try {
            boolean isNew = (hospital.getId() == null);
            hospitalService.saveHospital(hospital);
            redirectAttributes.addFlashAttribute("successMessage", 
                    isNew ? "Hospital '" + hospital.getName() + "' registered successfully!" : "Hospital '" + hospital.getName() + "' updated successfully!");
            return "redirect:/hospitals";
        } catch (IllegalArgumentException ex) {
            bindingResult.rejectValue("availableBeds", "error.hospital", ex.getMessage());
            return "hospitals/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        return hospitalService.getHospitalById(id)
                .map(hospital -> {
                    model.addAttribute("hospital", hospital);
                    return "hospitals/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Hospital not found with ID: " + id);
                    return "redirect:/hospitals";
                });
    }

    @PostMapping("/update-beds")
    public String updateAvailableBeds(@RequestParam("id") Long id,
                                      @RequestParam("availableBeds") Integer availableBeds,
                                      RedirectAttributes redirectAttributes) {
        try {
            hospitalService.updateAvailableBeds(id, availableBeds);
            redirectAttributes.addFlashAttribute("successMessage", "Available beds updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update bed count: " + e.getMessage());
        }
        return "redirect:/hospitals";
    }

    @PostMapping("/delete/{id}")
    public String deleteHospital(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            hospitalService.deleteHospital(id);
            redirectAttributes.addFlashAttribute("successMessage", "Hospital record deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not delete hospital: " + e.getMessage());
        }
        return "redirect:/hospitals";
    }
}
