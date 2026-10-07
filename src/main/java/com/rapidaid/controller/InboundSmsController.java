package com.rapidaid.controller;

import com.rapidaid.model.EmergencyRequest;
import com.rapidaid.model.RequestStatus;
import com.rapidaid.service.EmergencyRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sms")
public class InboundSmsController {

    private static final Logger log = LoggerFactory.getLogger(InboundSmsController.class);
    private final EmergencyRequestService requestService;

    @Autowired
    public InboundSmsController(EmergencyRequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping(value = "/inbound", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> handleInboundSmsForm(
            @RequestParam(name = "From", required = false, defaultValue = "Unknown") String from,
            @RequestParam(name = "Body", required = false, defaultValue = "") String body) {
        return processSms(from, body);
    }

    @PostMapping(value = "/inbound", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> handleInboundSmsFallback(
            @RequestParam(name = "From", required = false, defaultValue = "Unknown") String from,
            @RequestParam(name = "Body", required = false, defaultValue = "") String body) {
        return processSms(from, body);
    }

    private ResponseEntity<String> processSms(String from, String body) {
        log.info("Received Inbound SMS Webhook -> From: {}, Body: '{}'", from, body);

        String trimmedBody = body != null ? body.trim() : "";
        String lowerBody = trimmedBody.toLowerCase();

        EmergencyRequest emergencyRequest = new EmergencyRequest();
        emergencyRequest.setPatientPhone(from);
        emergencyRequest.setPatientName("SMS Caller (" + from + ")");
        emergencyRequest.setStatus(RequestStatus.PENDING);

        String replyMessageText;

        if (lowerBody.startsWith("sos")) {
            // Format matched: "SOS <location and description>"
            String details = trimmedBody.substring(3).trim();
            if (details.startsWith(":") || details.startsWith("-")) {
                details = details.substring(1).trim();
            }
            String loc = !details.isEmpty() ? details : "Location provided via SMS from " + from;

            emergencyRequest.setEmergencyType("SMS SOS Urgent");
            emergencyRequest.setLocation(loc);
            emergencyRequest.setDescription("Inbound SOS SMS from " + from + ": " + trimmedBody);

            EmergencyRequest created = requestService.createRequest(emergencyRequest);

            replyMessageText = "RapidAid SOS CONFIRMED: Request #" + created.getId() + 
                    " received. Emergency dispatch team is being notified. If this is life-threatening, please also call your local emergency hotline immediately.";

        } else {
            // Raw message format
            String loc = !trimmedBody.isEmpty() ? trimmedBody : "Location unspecified (SMS)";
            emergencyRequest.setEmergencyType("SMS Inbound Message");
            emergencyRequest.setLocation(loc);
            emergencyRequest.setDescription("Raw Inbound SMS from " + from + ": " + trimmedBody);

            EmergencyRequest created = requestService.createRequest(emergencyRequest);

            replyMessageText = "RapidAid: Emergency request received (#" + created.getId() + 
                    "). For faster dispatch, please resend your message using the format: 'SOS <location and details>'. If life-threatening, call emergency services immediately.";
        }

        String twimlXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<Response>\n" +
                "    <Message>" + escapeXml(replyMessageText) + "</Message>\n" +
                "</Response>";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .body(twimlXml);
    }

    private String escapeXml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
