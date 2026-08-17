package com.rapidaid.model;

public enum AmbulanceType {
    BASIC("Basic Life Support (BLS)"),
    ADVANCED("Advanced Life Support (ALS)"),
    ICU("Mobile ICU");

    private final String displayName;

    AmbulanceType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
