package com.rapidaid;

import com.rapidaid.model.EmergencyRequest;
import com.rapidaid.model.NotificationLog;
import com.rapidaid.model.RequestStatus;
import com.rapidaid.repository.EmergencyRequestRepository;
import com.rapidaid.repository.NotificationLogRepository;
import com.rapidaid.service.EmergencyRequestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FeatureVerificationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmergencyRequestService requestService;

    @Autowired
    private EmergencyRequestRepository requestRepository;

    @Autowired
    private NotificationLogRepository notificationLogRepository;

    @Test
    public void testFeature1_LiveRequestsEndpoint() throws Exception {
        mockMvc.perform(get("/api/requests/live"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void testFeature2_OutboundSmsFailsGracefullyWithAuditLog() {
        long initialCount = notificationLogRepository.count();

        // Find a pending request and assign dispatch
        List<EmergencyRequest> pending = requestService.getRequestsByStatus(RequestStatus.PENDING);
        assertFalse(pending.isEmpty(), "Pending requests list should not be empty");

        EmergencyRequest req = pending.get(0);
        assertDoesNotThrow(() -> requestService.assignDispatch(req.getId(), 1L, 1L));

        // Confirm notification log entry added with FAILED or DISABLED_NOOP status without 500 error
        long updatedCount = notificationLogRepository.count();
        assertTrue(updatedCount > initialCount, "Notification audit log entry should be created on dispatch assignment");

        List<NotificationLog> logs = notificationLogRepository.findAll();
        NotificationLog latest = logs.get(logs.size() - 1);
        assertEquals("SMS", latest.getType());
        assertTrue("FAILED".equals(latest.getStatus()) || "DISABLED_NOOP".equals(latest.getStatus()));
    }

    @Test
    public void testFeature3_InboundSmsWebhookCreatesPendingRequestAndReturnsTwiml() throws Exception {
        long initialCount = requestRepository.count();

        mockMvc.perform(post("/api/sms/inbound")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("From", "+15558889999")
                        .param("Body", "SOS 123 Main Street severe chest pain"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML))
                .andExpect(xpath("/Response/Message").exists());

        long updatedCount = requestRepository.count();
        assertEquals(initialCount + 1, updatedCount, "Inbound SMS must create a new EmergencyRequest");

        List<EmergencyRequest> all = requestRepository.findAll();
        EmergencyRequest latest = all.get(all.size() - 1);
        assertEquals("+15558889999", latest.getPatientPhone());
        assertEquals(RequestStatus.PENDING, latest.getStatus());
        assertTrue(latest.getLocation().contains("123 Main Street"));
    }
}
