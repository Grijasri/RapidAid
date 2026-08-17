-- RapidAid Sample Data Script
-- Default Admin Account: admin / admin123
-- BCrypt Hash for "admin123": $2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a

INSERT INTO users (username, password, full_name, role) VALUES 
('admin', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'Chief Medical Officer Admin', 'ROLE_ADMIN'),
('staff', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'Dispatcher Staff', 'ROLE_STAFF');

INSERT INTO patients (name, age, gender, blood_group, phone, address, medical_history, created_at) VALUES 
('Eleanor Vance', 34, 'FEMALE', 'O_POSITIVE', '+1-555-0192', '742 Evergreen Terrace, Sector 4', 'Hypertension, Asthma', NOW()),
('Robert Chen', 58, 'MALE', 'A_POSITIVE', '+1-555-0144', '128 Pinecrest Avenue, Block B', 'Diabetes Type 2', NOW()),
('Sophia Patel', 29, 'FEMALE', 'B_NEGATIVE', '+1-555-0188', '45 Grandview Boulevard, Apt 3C', 'No known chronic conditions', NOW()),
('Marcus Sterling', 45, 'MALE', 'AB_POSITIVE', '+1-555-0122', '89 Oakridge Drive', 'Cardiac Arrhythmia', NOW()),
('Hannah Abbott', 62, 'FEMALE', 'O_NEGATIVE', '+1-555-0177', '12 Maple Leaf Street', 'Arthritis', NOW());

INSERT INTO ambulances (vehicle_number, driver_name, driver_phone, status, type, base_location) VALUES 
('AMB-101', 'John Miller', '+1-555-9001', 'AVAILABLE', 'ICU', 'Central Hub - Station 1'),
('AMB-102', 'Sarah Jenkins', '+1-555-9002', 'ON_DUTY', 'ADVANCED', 'North Sector Depot'),
('AMB-103', 'David Garcia', '+1-555-9003', 'AVAILABLE', 'BASIC', 'Eastside Station'),
('AMB-104', 'Emily Watson', '+1-555-9004', 'MAINTENANCE', 'ICU', 'Central Workshop'),
('AMB-105', 'Michael Chang', '+1-555-9005', 'AVAILABLE', 'ADVANCED', 'South Sector Depot');

INSERT INTO hospitals (name, address, contact_phone, total_beds, available_beds) VALUES 
('St. Jude Emergency Medical Center', '500 Health Care Way, Downtown', '+1-555-8000', 150, 42),
('Metropolitan General Hospital', '1200 University Avenue, Midtown', '+1-555-8100', 250, 85),
('Valley View Community Hospital', '350 Mountain Road, West District', '+1-555-8200', 80, 15),
('Apex Cardiac & Trauma Institute', '88 Specialist Lane, East Sector', '+1-555-8300', 120, 28);

INSERT INTO emergency_requests (patient_id, location, description, status, ambulance_id, hospital_id, request_time, completion_time) VALUES 
(1, '742 Evergreen Terrace', 'Severe shortness of breath and chest tightness', 'ASSIGNED', 2, 1, NOW(), NULL),
(2, '128 Pinecrest Avenue', 'Fall from ladder, suspected ankle fracture', 'PENDING', NULL, NULL, NOW(), NULL),
(3, '45 Grandview Boulevard', 'Acute allergic reaction, anaphylaxis symptoms', 'COMPLETED', 1, 2, NOW(), NOW());

INSERT INTO activity_logs (timestamp, action, details, performed_by) VALUES 
(NOW(), 'SYSTEM_INITIALIZATION', 'RapidAid System initialized with seed entities and default administrative users.', 'SYSTEM'),
(NOW(), 'REQUEST_CREATED', 'Emergency Request #1 registered for Patient Eleanor Vance.', 'admin'),
(NOW(), 'DISPATCH_ASSIGNED', 'Assigned Ambulance AMB-102 and St. Jude Medical Center to Request #1.', 'admin'),
(NOW(), 'REQUEST_COMPLETED', 'Emergency Request #3 marked COMPLETED. Ambulance AMB-101 released.', 'admin');
