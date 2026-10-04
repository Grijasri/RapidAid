package com.rapidaid.dto;

import com.rapidaid.model.Ambulance;

public class AmbulanceDistanceDTO {

    private Ambulance ambulance;
    private Double distanceKm;
    private Integer etaMinutes;
    private boolean isClosest;

    public AmbulanceDistanceDTO() {}

    public AmbulanceDistanceDTO(Ambulance ambulance, Double distanceKm, Integer etaMinutes, boolean isClosest) {
        this.ambulance = ambulance;
        this.distanceKm = distanceKm;
        this.etaMinutes = etaMinutes;
        this.isClosest = isClosest;
    }

    public Ambulance getAmbulance() { return ambulance; }
    public void setAmbulance(Ambulance ambulance) { this.ambulance = ambulance; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

    public Integer getEtaMinutes() { return etaMinutes; }
    public void setEtaMinutes(Integer etaMinutes) { this.etaMinutes = etaMinutes; }

    public boolean isClosest() { return isClosest; }
    public void setClosest(boolean closest) { isClosest = closest; }
}
