package com.rapidaid.service.impl;

import com.rapidaid.service.DistanceService;
import com.rapidaid.service.RouteEtaCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultRouteEtaCalculator implements RouteEtaCalculator {

    private final DistanceService distanceService;
    private static final double ASSUMED_AMBULANCE_SPEED_KMH = 45.0; // average city speed for emergency dispatch

    @Autowired
    public DefaultRouteEtaCalculator(DistanceService distanceService) {
        this.distanceService = distanceService;
    }

    @Override
    public int calculateEtaMinutes(double originLat, double originLng, double destLat, double destLng) {
        /* 
         * EXTENSION POINT NOTE:
         * To swap in Google Maps Directions API:
         * 1. Inject RestTemplate / WebClient or Google Maps Java Client library.
         * 2. Call https://maps.googleapis.com/maps/api/directions/json?origin={lat},{lng}&destination={lat},{lng}&key={API_KEY}
         * 3. Extract duration_in_traffic.value (in seconds) / 60.
         * 4. Fall back to straight-line distance if API key is missing or call fails.
         */
        double distanceKm = distanceService.calculateDistanceKm(originLat, originLng, destLat, destLng);
        double hours = distanceKm / ASSUMED_AMBULANCE_SPEED_KMH;
        int minutes = (int) Math.ceil(hours * 60);
        return Math.max(1, minutes); // Minimum 1 minute ETA
    }
}
