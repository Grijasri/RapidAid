package com.rapidaid.service;

import com.rapidaid.model.Ambulance;
import com.rapidaid.model.AmbulanceStatus;
import com.rapidaid.repository.AmbulanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AmbulanceService {

    private final AmbulanceRepository ambulanceRepository;
    private final ActivityLogService activityLogService;

    @Autowired
    public AmbulanceService(AmbulanceRepository ambulanceRepository, ActivityLogService activityLogService) {
        this.ambulanceRepository = ambulanceRepository;
        this.activityLogService = activityLogService;
    }

    public List<Ambulance> getAllAmbulances() {
        return ambulanceRepository.findAll();
    }

    public List<Ambulance> getAvailableAmbulances() {
        return ambulanceRepository.findByStatus(AmbulanceStatus.AVAILABLE);
    }

    public Optional<Ambulance> getAmbulanceById(Long id) {
        return ambulanceRepository.findById(id);
    }

    public Ambulance saveAmbulance(Ambulance ambulance) {
        boolean isNew = (ambulance.getId() == null);
        if (isNew && ambulanceRepository.existsByVehicleNumber(ambulance.getVehicleNumber())) {
            throw new IllegalArgumentException("Ambulance with vehicle number '" + ambulance.getVehicleNumber() + "' already exists.");
        }
        Ambulance saved = ambulanceRepository.save(ambulance);
        if (isNew) {
            activityLogService.logActivity("AMBULANCE_ADDED", "Registered new ambulance: " + saved.getVehicleNumber() + " (" + saved.getType().getDisplayName() + ")");
        } else {
            activityLogService.logActivity("AMBULANCE_UPDATED", "Updated ambulance info for: " + saved.getVehicleNumber());
        }
        return saved;
    }

    public Ambulance updateStatus(Long id, AmbulanceStatus newStatus) {
        Ambulance ambulance = ambulanceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ambulance not found with ID: " + id));
        AmbulanceStatus oldStatus = ambulance.getStatus();
        ambulance.setStatus(newStatus);
        Ambulance updated = ambulanceRepository.save(ambulance);
        activityLogService.logActivity("AMBULANCE_STATUS_CHANGED", 
                "Ambulance " + updated.getVehicleNumber() + " status changed from " + oldStatus + " to " + newStatus);
        return updated;
    }

    public void deleteAmbulance(Long id) {
        Optional<Ambulance> ambulanceOpt = ambulanceRepository.findById(id);
        if (ambulanceOpt.isPresent()) {
            String vehicleNo = ambulanceOpt.get().getVehicleNumber();
            ambulanceRepository.deleteById(id);
            activityLogService.logActivity("AMBULANCE_DELETED", "Deleted ambulance vehicle: " + vehicleNo + " (ID: #" + id + ")");
        }
    }

    public long countTotalAmbulances() {
        return ambulanceRepository.count();
    }

    public long countByStatus(AmbulanceStatus status) {
        return ambulanceRepository.countByStatus(status);
    }
}
