package com.rapidaid.config;

import com.rapidaid.model.*;
import com.rapidaid.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final AmbulanceRepository ambulanceRepository;
    private final HospitalRepository hospitalRepository;
    private final EmergencyRequestRepository requestRepository;
    private final ActivityLogRepository activityLogRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserRepository userRepository,
                           PatientRepository patientRepository,
                           AmbulanceRepository ambulanceRepository,
                           HospitalRepository hospitalRepository,
                           EmergencyRequestRepository requestRepository,
                           ActivityLogRepository activityLogRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.ambulanceRepository = ambulanceRepository;
        this.hospitalRepository = hospitalRepository;
        this.requestRepository = requestRepository;
        this.activityLogRepository = activityLogRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            User admin = new User("admin", passwordEncoder.encode("admin123"), "Chief Medical Officer Admin", "ROLE_ADMIN");
            User staff = new User("staff", passwordEncoder.encode("admin123"), "Dispatcher Staff", "ROLE_STAFF");
            userRepository.save(admin);
            userRepository.save(staff);
        }

        if (patientRepository.count() == 0) {
            Patient p1 = new Patient("Eleanor Vance", 34, Gender.FEMALE, BloodGroup.O_POSITIVE, "+1-555-0192", "742 Evergreen Terrace, Sector 4", "Hypertension, Asthma");
            Patient p2 = new Patient("Robert Chen", 58, Gender.MALE, BloodGroup.A_POSITIVE, "+1-555-0144", "128 Pinecrest Avenue, Block B", "Diabetes Type 2");
            Patient p3 = new Patient("Sophia Patel", 29, Gender.FEMALE, BloodGroup.B_NEGATIVE, "+1-555-0188", "45 Grandview Boulevard, Apt 3C", "No known chronic conditions");
            Patient p4 = new Patient("Marcus Sterling", 45, Gender.MALE, BloodGroup.AB_POSITIVE, "+1-555-0122", "89 Oakridge Drive", "Cardiac Arrhythmia");
            Patient p5 = new Patient("Hannah Abbott", 62, Gender.FEMALE, BloodGroup.O_NEGATIVE, "+1-555-0177", "12 Maple Leaf Street", "Arthritis");

            patientRepository.save(p1);
            patientRepository.save(p2);
            patientRepository.save(p3);
            patientRepository.save(p4);
            patientRepository.save(p5);
        }

        if (ambulanceRepository.count() == 0) {
            Ambulance a1 = new Ambulance("AMB-101", "John Miller", "+1-555-9001", AmbulanceStatus.AVAILABLE, AmbulanceType.ICU, "Central Hub - Station 1");
            Ambulance a2 = new Ambulance("AMB-102", "Sarah Jenkins", "+1-555-9002", AmbulanceStatus.ON_DUTY, AmbulanceType.ADVANCED, "North Sector Depot");
            Ambulance a3 = new Ambulance("AMB-103", "David Garcia", "+1-555-9003", AmbulanceStatus.AVAILABLE, AmbulanceType.BASIC, "Eastside Station");
            Ambulance a4 = new Ambulance("AMB-104", "Emily Watson", "+1-555-9004", AmbulanceStatus.MAINTENANCE, AmbulanceType.ICU, "Central Workshop");
            Ambulance a5 = new Ambulance("AMB-105", "Michael Chang", "+1-555-9005", AmbulanceStatus.AVAILABLE, AmbulanceType.ADVANCED, "South Sector Depot");

            ambulanceRepository.save(a1);
            ambulanceRepository.save(a2);
            ambulanceRepository.save(a3);
            ambulanceRepository.save(a4);
            ambulanceRepository.save(a5);
        }

        if (hospitalRepository.count() == 0) {
            Hospital h1 = new Hospital("St. Jude Emergency Medical Center", "500 Health Care Way, Downtown", "+1-555-8000", 150, 42);
            Hospital h2 = new Hospital("Metropolitan General Hospital", "1200 University Avenue, Midtown", "+1-555-8100", 250, 85);
            Hospital h3 = new Hospital("Valley View Community Hospital", "350 Mountain Road, West District", "+1-555-8200", 80, 15);
            Hospital h4 = new Hospital("Apex Cardiac & Trauma Institute", "88 Specialist Lane, East Sector", "+1-555-8300", 120, 28);

            hospitalRepository.save(h1);
            hospitalRepository.save(h2);
            hospitalRepository.save(h3);
            hospitalRepository.save(h4);
        }

        if (requestRepository.count() == 0 && patientRepository.count() > 0) {
            Patient p1 = patientRepository.findAll().get(0);
            Patient p2 = patientRepository.findAll().get(1);
            Patient p3 = patientRepository.findAll().get(2);

            Ambulance a1 = ambulanceRepository.findAll().get(0);
            Ambulance a2 = ambulanceRepository.findAll().get(1);

            Hospital h1 = hospitalRepository.findAll().get(0);
            Hospital h2 = hospitalRepository.findAll().get(1);

            EmergencyRequest req1 = new EmergencyRequest(p1, "742 Evergreen Terrace", "Severe shortness of breath and chest tightness");
            req1.setStatus(RequestStatus.ASSIGNED);
            req1.setAmbulance(a2);
            req1.setHospital(h1);

            EmergencyRequest req2 = new EmergencyRequest(p2, "128 Pinecrest Avenue", "Fall from ladder, suspected ankle fracture");
            req2.setStatus(RequestStatus.PENDING);

            EmergencyRequest req3 = new EmergencyRequest(p3, "45 Grandview Boulevard", "Acute allergic reaction, anaphylaxis symptoms");
            req3.setStatus(RequestStatus.COMPLETED);
            req3.setAmbulance(a1);
            req3.setHospital(h2);
            req3.setCompletionTime(LocalDateTime.now());

            requestRepository.save(req1);
            requestRepository.save(req2);
            requestRepository.save(req3);
        }

        if (activityLogRepository.count() == 0) {
            activityLogRepository.save(new ActivityLog("SYSTEM_INITIALIZATION", "RapidAid System initialized with seed entities and default administrative users.", "SYSTEM"));
            activityLogRepository.save(new ActivityLog("REQUEST_CREATED", "Emergency Request registered for Patient Eleanor Vance.", "admin"));
            activityLogRepository.save(new ActivityLog("DISPATCH_ASSIGNED", "Assigned Ambulance AMB-102 and St. Jude Medical Center to Request #1.", "admin"));
            activityLogRepository.save(new ActivityLog("REQUEST_COMPLETED", "Emergency Request #3 marked COMPLETED. Ambulance AMB-101 released.", "admin"));
        }
    }
}
