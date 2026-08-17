package com.rapidaid.service;

import com.rapidaid.model.Patient;
import com.rapidaid.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final ActivityLogService activityLogService;

    @Autowired
    public PatientService(PatientRepository patientRepository, ActivityLogService activityLogService) {
        this.patientRepository = patientRepository;
        this.activityLogService = activityLogService;
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public Page<Patient> getPatients(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        if (query != null && !query.trim().isEmpty()) {
            return patientRepository.searchPatients(query.trim(), pageable);
        }
        return patientRepository.findAll(pageable);
    }

    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    public Patient savePatient(Patient patient) {
        boolean isNew = (patient.getId() == null);
        Patient saved = patientRepository.save(patient);
        if (isNew) {
            activityLogService.logActivity("PATIENT_ADDED", "Added patient: " + saved.getName() + " (ID: #" + saved.getId() + ")");
        } else {
            activityLogService.logActivity("PATIENT_UPDATED", "Updated patient details for: " + saved.getName() + " (ID: #" + saved.getId() + ")");
        }
        return saved;
    }

    public void deletePatient(Long id) {
        Optional<Patient> patientOpt = patientRepository.findById(id);
        if (patientOpt.isPresent()) {
            String name = patientOpt.get().getName();
            patientRepository.deleteById(id);
            activityLogService.logActivity("PATIENT_DELETED", "Deleted patient record: " + name + " (ID: #" + id + ")");
        }
    }

    public long countTotalPatients() {
        return patientRepository.count();
    }
}
