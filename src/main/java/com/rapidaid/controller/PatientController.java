package com.rapidaid.controller;

import com.rapidaid.model.BloodGroup;
import com.rapidaid.model.Gender;
import com.rapidaid.model.Patient;
import com.rapidaid.service.EmergencyRequestService;
import com.rapidaid.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;
    private final EmergencyRequestService requestService;

    @Autowired
    public PatientController(PatientService patientService, EmergencyRequestService requestService) {
        this.patientService = patientService;
        this.requestService = requestService;
    }

    @GetMapping
    public String listPatients(@RequestParam(value = "query", required = false) String query,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "10") int size,
                               Model model) {
        Page<Patient> patientPage = patientService.getPatients(query, page, size);
        model.addAttribute("patientPage", patientPage);
        model.addAttribute("patients", patientPage.getContent());
        model.addAttribute("query", query != null ? query : "");
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", patientPage.getTotalPages());
        return "patients/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("patient", new Patient());
        model.addAttribute("genders", Gender.values());
        model.addAttribute("bloodGroups", BloodGroup.values());
        return "patients/form";
    }

    @PostMapping
    public String savePatient(@Valid @ModelAttribute("patient") Patient patient,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("genders", Gender.values());
            model.addAttribute("bloodGroups", BloodGroup.values());
            return "patients/form";
        }
        boolean isNew = (patient.getId() == null);
        patientService.savePatient(patient);
        redirectAttributes.addFlashAttribute("successMessage", 
                isNew ? "Patient '" + patient.getName() + "' registered successfully!" : "Patient '" + patient.getName() + "' updated successfully!");
        return "redirect:/patients";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        return patientService.getPatientById(id)
                .map(patient -> {
                    model.addAttribute("patient", patient);
                    model.addAttribute("genders", Gender.values());
                    model.addAttribute("bloodGroups", BloodGroup.values());
                    return "patients/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Patient not found with ID: " + id);
                    return "redirect:/patients";
                });
    }

    @GetMapping("/view/{id}")
    public String viewPatient(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        return patientService.getPatientById(id)
                .map(patient -> {
                    model.addAttribute("patient", patient);
                    model.addAttribute("requests", requestService.getRequestsByPatientId(id));
                    return "patients/detail";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Patient not found with ID: " + id);
                    return "redirect:/patients";
                });
    }

    @PostMapping("/delete/{id}")
    public String deletePatient(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            patientService.deletePatient(id);
            redirectAttributes.addFlashAttribute("successMessage", "Patient record deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not delete patient: " + e.getMessage());
        }
        return "redirect:/patients";
    }
}
