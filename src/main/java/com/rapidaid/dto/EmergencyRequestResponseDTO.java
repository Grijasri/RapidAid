package com.rapidaid.dto;

import com.rapidaid.model.EmergencyRequest;
import java.time.format.DateTimeFormatter;

public class EmergencyRequestResponseDTO {

    private Long id;
    private String patientName;
    private String patientPhone;
    private String emergencyType;
    private String location;
    private String description;
    private String status;
    private Integer priorityScore;
    private String priorityLabel;
    private String assignedAmbulance;
    private String assignedHospital;
    private String requestTime;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public EmergencyRequestResponseDTO() {}

    public static EmergencyRequestResponseDTO fromEntity(EmergencyRequest req) {
        EmergencyRequestResponseDTO dto = new EmergencyRequestResponseDTO();
        dto.setId(req.getId());
        dto.setPatientName(req.getPatientName());
        dto.setPatientPhone(req.getPatientPhone());
        dto.setEmergencyType(req.getEmergencyType());
        dto.setLocation(req.getLocation());
        dto.setDescription(req.getDescription());
        dto.setStatus(req.getStatus() != null ? req.getStatus().name() : "PENDING");
        dto.setPriorityScore(req.getPriorityScore());
        dto.setPriorityLabel(req.getPriorityLabel());

        if (req.getAmbulance() != null) {
            dto.setAssignedAmbulance(req.getAmbulance().getVehicleNumber() + " (" + req.getAmbulance().getDriverName() + ")");
        } else {
            dto.setAssignedAmbulance("Unassigned");
        }

        if (req.getHospital() != null) {
            dto.setAssignedHospital(req.getHospital().getName());
        } else {
            dto.setAssignedHospital("Unassigned");
        }

        if (req.getRequestTime() != null) {
            dto.setRequestTime(req.getRequestTime().format(FORMATTER));
        } else {
            dto.setRequestTime("");
        }

        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getPatientPhone() { return patientPhone; }
    public void setPatientPhone(String patientPhone) { this.patientPhone = patientPhone; }

    public String getEmergencyType() { return emergencyType; }
    public void setEmergencyType(String emergencyType) { this.emergencyType = emergencyType; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getPriorityScore() { return priorityScore; }
    public void setPriorityScore(Integer priorityScore) { this.priorityScore = priorityScore; }

    public String getPriorityLabel() { return priorityLabel; }
    public void setPriorityLabel(String priorityLabel) { this.priorityLabel = priorityLabel; }

    public String getAssignedAmbulance() { return assignedAmbulance; }
    public void setAssignedAmbulance(String assignedAmbulance) { this.assignedAmbulance = assignedAmbulance; }

    public String getAssignedHospital() { return assignedHospital; }
    public void setAssignedHospital(String assignedHospital) { this.assignedHospital = assignedHospital; }

    public String getRequestTime() { return requestTime; }
    public void setRequestTime(String requestTime) { this.requestTime = requestTime; }
}
