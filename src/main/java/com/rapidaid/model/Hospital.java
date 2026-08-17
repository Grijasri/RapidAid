package com.rapidaid.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "hospitals")
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Hospital name is required")
    @Size(min = 2, max = 100, message = "Hospital name must be between 2 and 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Address is required")
    @Column(nullable = false, length = 255)
    private String address;

    @NotBlank(message = "Contact phone is required")
    @Pattern(regexp = "^[0-9+\\-\\s()]{7,15}$", message = "Please enter a valid phone number")
    @Column(name = "contact_phone", nullable = false, length = 20)
    private String contactPhone;

    @NotNull(message = "Total beds count is required")
    @Min(value = 1, message = "Total beds must be at least 1")
    @Column(name = "total_beds", nullable = false)
    private Integer totalBeds;

    @NotNull(message = "Available beds count is required")
    @Min(value = 0, message = "Available beds cannot be negative")
    @Column(name = "available_beds", nullable = false)
    private Integer availableBeds;

    public Hospital() {}

    public Hospital(String name, String address, String contactPhone, Integer totalBeds, Integer availableBeds) {
        this.name = name;
        this.address = address;
        this.contactPhone = contactPhone;
        this.totalBeds = totalBeds;
        this.availableBeds = availableBeds;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public Integer getTotalBeds() { return totalBeds; }
    public void setTotalBeds(Integer totalBeds) { this.totalBeds = totalBeds; }

    public Integer getAvailableBeds() { return availableBeds; }
    public void setAvailableBeds(Integer availableBeds) { this.availableBeds = availableBeds; }

    // Utility getter for bed occupancy percentage
    public int getOccupancyPercentage() {
        if (totalBeds == null || totalBeds == 0) return 0;
        int occupied = totalBeds - (availableBeds != null ? availableBeds : 0);
        return Math.min(100, Math.max(0, (int) Math.round((double) occupied / totalBeds * 100)));
    }
}
