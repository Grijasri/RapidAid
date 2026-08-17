package com.rapidaid.model;

public enum RequestStatus {
    PENDING("Pending", "bg-warning text-dark"),
    ASSIGNED("Assigned", "bg-info text-dark"),
    COMPLETED("Completed", "bg-success"),
    CANCELLED("Cancelled", "bg-secondary");

    private final String displayName;
    private final String badgeClass;

    RequestStatus(String displayName, String badgeClass) {
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
