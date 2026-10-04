package com.rapidaid.controller;

import com.rapidaid.dto.LocationUpdateDTO;
import com.rapidaid.model.Ambulance;
import com.rapidaid.service.AmbulanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Controller
public class AmbulanceLocationController {

    private final AmbulanceService ambulanceService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public AmbulanceLocationController(AmbulanceService ambulanceService,
                                       SimpMessagingTemplate messagingTemplate) {
        this.ambulanceService = ambulanceService;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/api/v1/ambulances/{id}/location")
    @ResponseBody
    public ResponseEntity<LocationUpdateDTO> updateLocation(@PathVariable("id") Long id,
                                                             @RequestBody Map<String, Double> payload) {
        Double lat = payload.get("latitude");
        Double lng = payload.get("longitude");

        if (lat == null || lng == null) {
            return ResponseEntity.badRequest().build();
        }

        Ambulance updated = ambulanceService.updateLocation(id, lat, lng);

        LocationUpdateDTO dto = new LocationUpdateDTO(
                updated.getId(),
                updated.getVehicleNumber(),
                updated.getDriverName(),
                updated.getLatitude(),
                updated.getLongitude(),
                updated.getLastLocationUpdate(),
                updated.isLocationStale(3)
        );

        // Broadcast live update over STOMP topic
        messagingTemplate.convertAndSend("/topic/tracking", dto);
        messagingTemplate.convertAndSend("/topic/ambulance/" + id, dto);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/driver/share-location")
    public String driverLocationPage(Model model) {
        model.addAttribute("ambulances", ambulanceService.getAllAmbulances());
        return "ambulances/driver_share";
    }

    @GetMapping("/driver/share-location/{id}")
    public String driverLocationPageForAmbulance(@PathVariable("id") Long id, Model model) {
        Optional<Ambulance> ambOpt = ambulanceService.getAmbulanceById(id);
        if (ambOpt.isEmpty()) {
            return "redirect:/driver/share-location";
        }
        model.addAttribute("selectedAmbulance", ambOpt.get());
        model.addAttribute("ambulances", ambulanceService.getAllAmbulances());
        return "ambulances/driver_share";
    }
}
