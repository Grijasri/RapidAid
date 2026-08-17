package com.rapidaid.model;

public enum AmbulanceStatus {
    AVAILABLE("Available", "bg-success"),
    ON_DUTY("On Duty", "bg-danger"),
    MAINTENANCE("Maintenance", "bg-warning text-dark");

    private final String displayName;
    private final String badgeClass;

    AmbulanceStatus(String displayName, String badgeClass) {
        this.displayName = displayName;
        this.badgeClass = badgeClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBadgeClass() {
        return badgeClass;
    }
}
