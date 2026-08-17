package com.rapidaid.repository;

import com.rapidaid.model.EmergencyRequest;
import com.rapidaid.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmergencyRequestRepository extends JpaRepository<EmergencyRequest, Long> {
    List<EmergencyRequest> findByStatus(RequestStatus status);
    List<EmergencyRequest> findByPatientIdOrderByRequestTimeDesc(Long patientId);
    long countByStatus(RequestStatus status);
    List<EmergencyRequest> findAllByOrderByRequestTimeDesc();
}
