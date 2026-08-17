package com.rapidaid.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "ambulances")
public class Ambulance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Vehicle number is required")
    @Pattern(regexp = "^[A-Z0-9\\-]{3,20}$", message = "Vehicle number should contain uppercase letters, numbers, or hyphens (e.g. AMB-101)")
    @Column(name = "vehicle_number", nullable = false, unique = true, length = 50)
    private String vehicleNumber;

    @NotBlank(message = "Driver name is required")
    @Size(min = 2, max = 100, message = "Driver name must be between 2 and 100 characters")
    @Column(name = "driver_name", nullable = false, length = 100)
    private String driverName;

    @NotBlank(message = "Driver phone is required")
    @Pattern(regexp = "^[0-9+\\-\\s()]{7,15}$", message = "Please enter a valid phone number")
    @Column(name = "driver_phone", nullable = false, length = 20)
    private String driverPhone;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AmbulanceStatus status = AmbulanceStatus.AVAILABLE;

    @NotNull(message = "Ambulance type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AmbulanceType type = AmbulanceType.BASIC;

    @NotBlank(message = "Base location is required")
    @Column(name = "base_location", nullable = false, length = 100)
    private String baseLocation;

    public Ambulance() {}

    public Ambulance(String vehicleNumber, String driverName, String driverPhone, AmbulanceStatus status, AmbulanceType type, String baseLocation) {
        this.vehicleNumber = vehicleNumber;
        this.driverName = driverName;
        this.driverPhone = driverPhone;
        this.status = status;
        this.type = type;
        this.baseLocation = baseLocation;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getDriverPhone() { return driverPhone; }
    public void setDriverPhone(String driverPhone) { this.driverPhone = driverPhone; }

    public AmbulanceStatus getStatus() { return status; }
    public void setStatus(AmbulanceStatus status) { this.status = status; }

    public AmbulanceType getType() { return type; }
    public void setType(AmbulanceType type) { this.type = type; }

    public String getBaseLocation() { return baseLocation; }
    public void setBaseLocation(String baseLocation) { this.baseLocation = baseLocation; }
}
