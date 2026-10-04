package com.rapidaid.controller;

import com.rapidaid.service.PartnerInquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/partner-inquiries")
public class PartnerInquiryController {

    private final PartnerInquiryService partnerInquiryService;

    @Autowired
    public PartnerInquiryController(PartnerInquiryService partnerInquiryService) {
        this.partnerInquiryService = partnerInquiryService;
    }

    @GetMapping
    public String listInquiries(Model model) {
        model.addAttribute("inquiries", partnerInquiryService.getAllInquiries());
        return "admin/partner_inquiries";
    }
}
