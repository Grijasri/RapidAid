package com.rapidaid.repository;

import com.rapidaid.model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    
    List<Hospital> findByAvailableBedsGreaterThan(Integer minBeds);

    @Query("SELECT SUM(h.totalBeds) FROM Hospital h")
    Long sumTotalBeds();

    @Query("SELECT SUM(h.availableBeds) FROM Hospital h")
    Long sumAvailableBeds();
}
