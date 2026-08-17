package com.rapidaid.service;

import com.rapidaid.model.Hospital;
import com.rapidaid.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HospitalService {

    private final HospitalRepository hospitalRepository;
    private final ActivityLogService activityLogService;

    @Autowired
    public HospitalService(HospitalRepository hospitalRepository, ActivityLogService activityLogService) {
        this.hospitalRepository = hospitalRepository;
        this.activityLogService = activityLogService;
    }

    public List<Hospital> getAllHospitals() {
        return hospitalRepository.findAll();
    }

    public List<Hospital> getHospitalsWithAvailableBeds() {
        return hospitalRepository.findByAvailableBedsGreaterThan(0);
    }

    public Optional<Hospital> getHospitalById(Long id) {
        return hospitalRepository.findById(id);
    }

    public Hospital saveHospital(Hospital hospital) {
        if (hospital.getAvailableBeds() > hospital.getTotalBeds()) {
            throw new IllegalArgumentException("Available beds (" + hospital.getAvailableBeds() + ") cannot exceed total beds (" + hospital.getTotalBeds() + ")");
        }
        boolean isNew = (hospital.getId() == null);
        Hospital saved = hospitalRepository.save(hospital);
        if (isNew) {
            activityLogService.logActivity("HOSPITAL_ADDED", "Added new hospital: " + saved.getName() + " with " + saved.getTotalBeds() + " total beds.");
        } else {
            activityLogService.logActivity("HOSPITAL_UPDATED", "Updated hospital details for: " + saved.getName());
        }
        return saved;
    }

    public Hospital updateAvailableBeds(Long id, Integer newAvailableBeds) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hospital not found with ID: " + id));
        if (newAvailableBeds < 0 || newAvailableBeds > hospital.getTotalBeds()) {
            throw new IllegalArgumentException("Available beds must be between 0 and " + hospital.getTotalBeds());
        }
        int oldBeds = hospital.getAvailableBeds();
        hospital.setAvailableBeds(newAvailableBeds);
        Hospital updated = hospitalRepository.save(hospital);
        activityLogService.logActivity("BEDS_UPDATED", "Hospital " + updated.getName() + " available beds updated from " + oldBeds + " to " + newAvailableBeds);
        return updated;
    }

    public void deleteHospital(Long id) {
        Optional<Hospital> hospitalOpt = hospitalRepository.findById(id);
        if (hospitalOpt.isPresent()) {
            String name = hospitalOpt.get().getName();
            hospitalRepository.deleteById(id);
            activityLogService.logActivity("HOSPITAL_DELETED", "Deleted hospital record: " + name + " (ID: #" + id + ")");
        }
    }

    public long countTotalHospitals() {
        return hospitalRepository.count();
    }

    public long sumTotalBeds() {
        Long sum = hospitalRepository.sumTotalBeds();
        return sum != null ? sum : 0;
    }

    public long sumAvailableBeds() {
        Long sum = hospitalRepository.sumAvailableBeds();
        return sum != null ? sum : 0;
    }
}
