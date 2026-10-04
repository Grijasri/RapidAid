package com.rapidaid.service;

import com.rapidaid.model.PartnerInquiry;
import java.util.List;

public interface PartnerInquiryService {
    PartnerInquiry saveInquiry(PartnerInquiry inquiry);
    List<PartnerInquiry> getAllInquiries();
}
