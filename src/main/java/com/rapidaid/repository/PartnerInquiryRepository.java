package com.rapidaid.repository;

import com.rapidaid.model.PartnerInquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartnerInquiryRepository extends JpaRepository<PartnerInquiry, Long> {
    List<PartnerInquiry> findAllByOrderBySubmittedAtDesc();
}
