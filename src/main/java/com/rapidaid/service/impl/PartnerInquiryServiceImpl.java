package com.rapidaid.service.impl;

import com.rapidaid.model.PartnerInquiry;
import com.rapidaid.repository.PartnerInquiryRepository;
import com.rapidaid.service.ActivityLogService;
import com.rapidaid.service.PartnerInquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PartnerInquiryServiceImpl implements PartnerInquiryService {

    private final PartnerInquiryRepository inquiryRepository;
    private final ActivityLogService activityLogService;

    @Autowired
    public PartnerInquiryServiceImpl(PartnerInquiryRepository inquiryRepository,
                                     ActivityLogService activityLogService) {
        this.inquiryRepository = inquiryRepository;
        this.activityLogService = activityLogService;
    }

    @Override
    public PartnerInquiry saveInquiry(PartnerInquiry inquiry) {
        PartnerInquiry saved = inquiryRepository.save(inquiry);
        activityLogService.logActivity("PARTNER_INQUIRY_RECEIVED", 
                "Received partner onboarding interest from " + saved.getHospitalName() + " (Contact: " + saved.getName() + ")");
        return saved;
    }

    @Override
    public List<PartnerInquiry> getAllInquiries() {
        return inquiryRepository.findAllByOrderBySubmittedAtDesc();
    }
}
