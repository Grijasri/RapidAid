package com.rapidaid.service.impl;

import com.rapidaid.service.PriorityEngine;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class RuleBasedPriorityEngine implements PriorityEngine {

    @Override
    public PriorityResult evaluatePriority(String emergencyType, String description) {
        String combined = ((emergencyType != null ? emergencyType : "") + " " + (description != null ? description : "")).toLowerCase(Locale.ROOT);

        // High priority keywords
        if (containsAny(combined, "cardiac", "heart attack", "not breathing", "unconscious", "stroke", 
                "anaphylaxis", "chest pain", "severe bleeding", "hemorrhage", "respiratory", "choking", "drowning")) {
            return new PriorityResult(95, "HIGH");
        }

        // Medium priority keywords
        if (containsAny(combined, "fracture", "fall", "head injury", "burn", "trauma", "labor", 
                "pregnancy", "accident", "bleeding", "asthma", "seizure")) {
            return new PriorityResult(60, "MEDIUM");
        }

        // Low priority keywords / fallback
        if (containsAny(combined, "fever", "minor", "cut", "cold", "pain", "vomiting", "dizziness")) {
            return new PriorityResult(25, "LOW");
        }

        // Default baseline score
        return new PriorityResult(40, "LOW");
    }

    private boolean containsAny(String text, String... keywords) {
        for (String kw : keywords) {
            if (text.contains(kw)) {
                return true;
            }
        }
        return false;
    }
}
