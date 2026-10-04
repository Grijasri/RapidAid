package com.rapidaid.service;

/**
 * =========================================================================================
 * EXTENSION POINT FOR GOOGLE MAPS DIRECTIONS / DISTANCE MATRIX API INTEGRATION
 * =========================================================================================
 * Swap in an implementation that calls Google Maps Directions API to obtain real-time
 * turn-by-turn driving distance and live traffic-adjusted ETA in minutes.
 * =========================================================================================
 */
public interface RouteEtaCalculator {

    /**
     * Calculates estimated travel duration in minutes between origin and destination.
     *
     * @param originLat Pickup latitude
     * @param originLng Pickup longitude
     * @param destLat Ambulance latitude
     * @param destLng Ambulance longitude
     * @return Estimated duration in minutes
     */
    int calculateEtaMinutes(double originLat, double originLng, double destLat, double destLng);
}
