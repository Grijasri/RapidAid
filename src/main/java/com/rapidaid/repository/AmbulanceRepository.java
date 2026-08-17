package com.rapidaid.repository;

import com.rapidaid.model.Ambulance;
import com.rapidaid.model.AmbulanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AmbulanceRepository extends JpaRepository<Ambulance, Long> {
    List<Ambulance> findByStatus(AmbulanceStatus status);
    long countByStatus(AmbulanceStatus status);
    Optional<Ambulance> findByVehicleNumber(String vehicleNumber);
    boolean existsByVehicleNumber(String vehicleNumber);
}
