package com.rapidaid.repository;

import com.rapidaid.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    
    List<Patient> findByNameContainingIgnoreCase(String name);

    Page<Patient> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT p FROM Patient p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.phone) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Patient> searchPatients(@Param("query") String query, Pageable pageable);
}
