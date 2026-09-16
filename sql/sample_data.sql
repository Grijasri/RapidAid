-- RapidAid Sample Data Script
-- Default Admin Account: admin / admin123
-- BCrypt Hash for "admin123": $2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a

INSERT INTO users (username, password, full_name, role) VALUES 
('admin', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'Chief Medical Officer Admin', 'ROLE_ADMIN'),
('staff', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'Dispatcher Staff', 'ROLE_STAFF');

INSERT INTO patients (name, age, gender, blood_group, phone, address, medical_history, created_at) VALUES 
('Eleanor Vance', 34, 'FEMALE', 'O_POSITIVE', '9876543210', '742 Evergreen Terrace, Sector 4', 'Hypertension, Asthma', NOW()),
('Robert Chen', 58, 'MALE', 'A_POSITIVE', '9876543211', '128 Pinecrest Avenue, Block B', 'Diabetes Type 2', NOW()),
('Sophia Patel', 29, 'FEMALE', 'B_NEGATIVE', '9876543212', '45 Grandview Boulevard, Apt 3C', 'No known chronic conditions', NOW()),
('Marcus Sterling', 45, 'MALE', 'AB_POSITIVE', '9876543213', '89 Oakridge Drive', 'Cardiac Arrhythmia', NOW()),
('Hannah Abbott', 62, 'FEMALE', 'O_NEGATIVE', '9876543214', '12 Maple Leaf Street', 'Arthritis', NOW());

INSERT INTO ambulances (vehicle_number, driver_name, driver_phone, status, type, base_location) VALUES 
('TN01AB1001', 'John Miller', '9876590001', 'AVAILABLE', 'ICU', 'Central Hub - Station 1'),
('TN02CD1002', 'Sarah Jenkins', '9876590002', 'ON_DUTY', 'ADVANCED', 'North Sector Depot'),
('TN03EF1003', 'David Garcia', '9876590003', 'AVAILABLE', 'BASIC', 'Eastside Station'),
('TN04GH1004', 'Emily Watson', '9876590004', 'MAINTENANCE', 'ICU', 'Central Workshop'),
('TN05IJ1005', 'Michael Chang', '9876590005', 'AVAILABLE', 'ADVANCED', 'South Sector Depot');

INSERT INTO hospitals (name, address, contact_phone, total_beds, available_beds) VALUES 
('St. Jude Emergency Medical Center', '500 Health Care Way, Downtown', '9876580001', 150, 42),
('Metropolitan General Hospital', '1200 University Avenue, Midtown', '9876580002', 250, 85),
('Valley View Community Hospital', '350 Mountain Road, West District', '9876580003', 80, 15),
('Apex Cardiac & Trauma Institute', '88 Specialist Lane, East Sector', '9876580004', 120, 28);

INSERT INTO emergency_requests (patient_id, location, description, status, ambulance_id, hospital_id, request_time, completion_time) VALUES 
(1, '742 Evergreen Terrace', 'Severe shortness of breath and chest tightness', 'ASSIGNED', 2, 1, NOW(), NULL),
(2, '128 Pinecrest Avenue', 'Fall from ladder, suspected ankle fracture', 'PENDING', NULL, NULL, NOW(), NULL),
(3, '45 Grandview Boulevard', 'Acute allergic reaction, anaphylaxis symptoms', 'COMPLETED', 1, 2, NOW(), NOW());

INSERT INTO activity_logs (timestamp, action, details, performed_by) VALUES 
(NOW(), 'SYSTEM_INITIALIZATION', 'RapidAid System initialized with seed entities and default administrative users.', 'SYSTEM'),
(NOW(), 'REQUEST_CREATED', 'Emergency Request #1 registered for Patient Eleanor Vance.', 'admin'),
(NOW(), 'DISPATCH_ASSIGNED', 'Assigned Ambulance TN02CD1002 and St. Jude Medical Center to Request #1.', 'admin'),
(NOW(), 'REQUEST_COMPLETED', 'Emergency Request #3 marked COMPLETED. Ambulance TN01AB1001 released.', 'admin');
