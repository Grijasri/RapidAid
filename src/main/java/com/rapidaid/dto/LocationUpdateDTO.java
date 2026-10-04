package com.rapidaid.dto;

import java.time.LocalDateTime;

public class LocationUpdateDTO {

    private Long ambulanceId;
    private String vehicleNumber;
    private String driverName;
    private Double latitude;
    private Double longitude;
    private LocalDateTime timestamp;
    private boolean isStale;

    public LocationUpdateDTO() {}

    public LocationUpdateDTO(Long ambulanceId, String vehicleNumber, String driverName, Double latitude, Double longitude, LocalDateTime timestamp, boolean isStale) {
        this.ambulanceId = ambulanceId;
        this.vehicleNumber = vehicleNumber;
        this.driverName = driverName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.isStale = isStale;
    }

    public Long getAmbulanceId() { return ambulanceId; }
    public void setAmbulanceId(Long ambulanceId) { this.ambulanceId = ambulanceId; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public boolean isStale() { return isStale; }
    public void setStale(boolean stale) { isStale = stale; }
}
