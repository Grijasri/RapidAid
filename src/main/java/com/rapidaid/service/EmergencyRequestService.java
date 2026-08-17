package com.rapidaid.service;

import com.rapidaid.model.*;
import com.rapidaid.repository.AmbulanceRepository;
import com.rapidaid.repository.EmergencyRequestRepository;
import com.rapidaid.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EmergencyRequestService {

    private final EmergencyRequestRepository requestRepository;
    private final AmbulanceRepository ambulanceRepository;
    private final HospitalRepository hospitalRepository;
    private final ActivityLogService activityLogService;

    @Autowired
    public EmergencyRequestService(EmergencyRequestRepository requestRepository,
                                  AmbulanceRepository ambulanceRepository,
                                  HospitalRepository hospitalRepository,
                                  ActivityLogService activityLogService) {
        this.requestRepository = requestRepository;
        this.ambulanceRepository = ambulanceRepository;
        this.hospitalRepository = hospitalRepository;
        this.activityLogService = activityLogService;
    }

    public List<EmergencyRequest> getAllRequests() {
        return requestRepository.findAllByOrderByRequestTimeDesc();
    }

    public List<EmergencyRequest> getRequestsByStatus(RequestStatus status) {
        return requestRepository.findByStatus(status);
    }

    public List<EmergencyRequest> getRequestsByPatientId(Long patientId) {
        return requestRepository.findByPatientIdOrderByRequestTimeDesc(patientId);
    }

    public Optional<EmergencyRequest> getRequestById(Long id) {
        return requestRepository.findById(id);
    }

    public EmergencyRequest createRequest(EmergencyRequest request) {
        request.setStatus(RequestStatus.PENDING);
        request.setRequestTime(LocalDateTime.now());
        EmergencyRequest saved = requestRepository.save(request);
        activityLogService.logActivity("REQUEST_CREATED", 
                "Emergency request registered for Patient: " + saved.getPatient().getName() + " at " + saved.getLocation());
        return saved;
    }

    @Transactional
    public EmergencyRequest assignDispatch(Long requestId, Long ambulanceId, Long hospitalId) {
        EmergencyRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Emergency Request not found with ID: " + requestId));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Only PENDING emergency requests can be assigned.");
        }

        Ambulance ambulance = ambulanceRepository.findById(ambulanceId)
                .orElseThrow(() -> new IllegalArgumentException("Ambulance not found with ID: " + ambulanceId));

        if (ambulance.getStatus() != AmbulanceStatus.AVAILABLE) {
            throw new IllegalStateException("Ambulance " + ambulance.getVehicleNumber() + " is currently unavailable (" + ambulance.getStatus() + ").");
        }

        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new IllegalArgumentException("Hospital not found with ID: " + hospitalId));

        if (hospital.getAvailableBeds() <= 0) {
            throw new IllegalStateException("Hospital " + hospital.getName() + " has no available beds remaining.");
        }

        // Cross-module state updates
        ambulance.setStatus(AmbulanceStatus.ON_DUTY);
        ambulanceRepository.save(ambulance);

        hospital.setAvailableBeds(hospital.getAvailableBeds() - 1);
        hospitalRepository.save(hospital);

        request.setAmbulance(ambulance);
        request.setHospital(hospital);
        request.setStatus(RequestStatus.ASSIGNED);
        EmergencyRequest updatedRequest = requestRepository.save(request);

        activityLogService.logActivity("DISPATCH_ASSIGNED", 
                "Request #" + requestId + " assigned: Ambulance [" + ambulance.getVehicleNumber() + 
                "] & Hospital [" + hospital.getName() + "]");

        return updatedRequest;
    }

    @Transactional
    public EmergencyRequest completeRequest(Long requestId) {
        EmergencyRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Emergency Request not found with ID: " + requestId));

        if (request.getStatus() != RequestStatus.ASSIGNED) {
            throw new IllegalStateException("Only ASSIGNED emergency requests can be marked as COMPLETED.");
        }

        Ambulance ambulance = request.getAmbulance();
        if (ambulance != null) {
            ambulance.setStatus(AmbulanceStatus.AVAILABLE);
            ambulanceRepository.save(ambulance);
        }

        request.setStatus(RequestStatus.COMPLETED);
        request.setCompletionTime(LocalDateTime.now());
        EmergencyRequest completed = requestRepository.save(request);

        String ambulanceInfo = ambulance != null ? "Ambulance " + ambulance.getVehicleNumber() + " reset to AVAILABLE." : "";
        activityLogService.logActivity("REQUEST_COMPLETED", 
                "Emergency Request #" + requestId + " completed successfully. " + ambulanceInfo);

        return completed;
    }

    public void deleteRequest(Long id) {
        Optional<EmergencyRequest> reqOpt = requestRepository.findById(id);
        if (reqOpt.isPresent()) {
            requestRepository.deleteById(id);
            activityLogService.logActivity("REQUEST_DELETED", "Deleted Emergency Request #" + id);
        }
    }

    public long countTotalRequests() {
        return requestRepository.count();
    }

    public long countByStatus(RequestStatus status) {
        return requestRepository.countByStatus(status);
    }
}
