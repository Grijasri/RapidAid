-- RapidAid Database Schema
-- Compatible with MySQL 8.x and H2 Database

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_STAFF'
);

CREATE TABLE IF NOT EXISTS patients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    gender VARCHAR(20) NOT NULL,
    blood_group VARCHAR(20) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address VARCHAR(255) NOT NULL,
    medical_history TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS ambulances (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vehicle_number VARCHAR(50) NOT NULL UNIQUE,
    driver_name VARCHAR(100) NOT NULL,
    driver_phone VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    type VARCHAR(30) NOT NULL DEFAULT 'BASIC',
    base_location VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS hospitals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    contact_phone VARCHAR(20) NOT NULL,
    total_beds INT NOT NULL,
    available_beds INT NOT NULL
);

CREATE TABLE IF NOT EXISTS emergency_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    location VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    ambulance_id BIGINT NULL,
    hospital_id BIGINT NULL,
    request_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completion_time TIMESTAMP NULL,
    CONSTRAINT fk_req_patient FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    CONSTRAINT fk_req_ambulance FOREIGN KEY (ambulance_id) REFERENCES ambulances(id) ON DELETE SET NULL,
    CONSTRAINT fk_req_hospital FOREIGN KEY (hospital_id) REFERENCES hospitals(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS activity_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    action VARCHAR(100) NOT NULL,
    details TEXT NOT NULL,
    performed_by VARCHAR(100) NOT NULL
);
