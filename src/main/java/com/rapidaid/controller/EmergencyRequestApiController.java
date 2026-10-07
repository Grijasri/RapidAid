package com.rapidaid.controller;

import com.rapidaid.dto.EmergencyRequestResponseDTO;
import com.rapidaid.model.EmergencyRequest;
import com.rapidaid.service.EmergencyRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/requests")
public class EmergencyRequestApiController {

    private final EmergencyRequestService requestService;

    @Autowired
    public EmergencyRequestApiController(EmergencyRequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping("/live")
    public ResponseEntity<List<EmergencyRequestResponseDTO>> getLiveRequests() {
        List<EmergencyRequest> requests = requestService.getAllRequests();
        List<EmergencyRequestResponseDTO> dtos = requests.stream()
                .map(EmergencyRequestResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
