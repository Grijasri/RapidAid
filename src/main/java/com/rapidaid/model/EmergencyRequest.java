package com.rapidaid.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "emergency_requests")
public class EmergencyRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = true)
    private Patient patient;

    @Column(name = "patient_name", length = 100)
    private String patientName;

    @Column(name = "patient_phone", length = 20)
    private String patientPhone;

    @Column(name = "patient_email", length = 100)
    private String patientEmail;

    @Column(name = "emergency_type", length = 100)
    private String emergencyType;

    @Column(name = "pickup_lat")
    private Double pickupLat;

    @Column(name = "pickup_lng")
    private Double pickupLng;

    @Column(name = "priority_score")
    private Integer priorityScore = 50;

    @Column(name = "priority_label", length = 20)
    private String priorityLabel = "MEDIUM";

    @NotBlank(message = "Emergency location is required")
    @Column(nullable = false, length = 255)
    private String location;

    @NotBlank(message = "Description of emergency is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RequestStatus status = RequestStatus.PENDING;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ambulance_id")
    private Ambulance ambulance;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @Column(name = "request_time", nullable = false, updatable = false)
    private LocalDateTime requestTime = LocalDateTime.now();

    @Column(name = "completion_time")
    private LocalDateTime completionTime;

    public EmergencyRequest() {}

    public EmergencyRequest(Patient patient, String location, String description) {
        this.patient = patient;
        if (patient != null) {
            this.patientName = patient.getName();
            this.patientPhone = patient.getPhone();
        }
        this.location = location;
        this.description = description;
        this.status = RequestStatus.PENDING;
        this.requestTime = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { 
        this.patient = patient; 
        if (patient != null) {
            if (this.patientName == null) this.patientName = patient.getName();
            if (this.patientPhone == null) this.patientPhone = patient.getPhone();
        }
    }

    public String getPatientName() {
        if (patientName != null && !patientName.isBlank()) return patientName;
        return patient != null ? patient.getName() : "Anonymous / Guest";
    }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getPatientPhone() {
        if (patientPhone != null && !patientPhone.isBlank()) return patientPhone;
        return patient != null ? patient.getPhone() : "";
    }
    public void setPatientPhone(String patientPhone) { this.patientPhone = patientPhone; }

    public String getPatientEmail() { return patientEmail; }
    public void setPatientEmail(String patientEmail) { this.patientEmail = patientEmail; }

    public String getEmergencyType() { return emergencyType; }
    public void setEmergencyType(String emergencyType) { this.emergencyType = emergencyType; }

    public Double getPickupLat() { return pickupLat; }
    public void setPickupLat(Double pickupLat) { this.pickupLat = pickupLat; }

    public Double getPickupLng() { return pickupLng; }
    public void setPickupLng(Double pickupLng) { this.pickupLng = pickupLng; }

    public Integer getPriorityScore() { return priorityScore != null ? priorityScore : 50; }
    public void setPriorityScore(Integer priorityScore) { this.priorityScore = priorityScore; }

    public String getPriorityLabel() { return priorityLabel != null ? priorityLabel : "MEDIUM"; }
    public void setPriorityLabel(String priorityLabel) { this.priorityLabel = priorityLabel; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }

    public Ambulance getAmbulance() { return ambulance; }
    public void setAmbulance(Ambulance ambulance) { this.ambulance = ambulance; }

    public Hospital getHospital() { return hospital; }
    public void setHospital(Hospital hospital) { this.hospital = hospital; }

    public LocalDateTime getRequestTime() { return requestTime; }
    public void setRequestTime(LocalDateTime requestTime) { this.requestTime = requestTime; }

    public LocalDateTime getCompletionTime() { return completionTime; }
    public void setCompletionTime(LocalDateTime completionTime) { this.completionTime = completionTime; }
}
