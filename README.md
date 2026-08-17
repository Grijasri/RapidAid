# RapidAid — Smart Emergency Ambulance Coordination System

**RapidAid** is a Java full-stack web application designed for emergency medical services to coordinate patients, ambulance fleets, hospital bed allocations, and emergency dispatches in real-time.

It evolves traditional console/paper register workflows into a modern browser-based command center powered by **Spring Boot 3**, **Thymeleaf**, **Spring Security**, and **MySQL 8** (with zero-config H2 fallback).

---

## Key Features

- **Public Landing Page (`/`)**: Hero banner, live dynamic database metrics (patients, available ambulances, free hospital beds, requests handled), and "How It Works" workflow overview.
- **Staff Authentication (`/login`)**: Protected route security powered by Spring Security 6 with BCrypt password encryption, custom login UI, and inline error alerts.
- **Command & Control Dashboard (`/dashboard`)**: Dynamic metric cards displaying fleet status, bed occupancy rates, active dispatch metrics, and a real-time request stream.
- **Patient Management (`/patients`)**: Full CRUD with name/phone search, pagination, server-side form validation (`@Valid`), delete confirmation prompts, and patient detail profile views with historical emergency requests.
- **Ambulance Fleet Management (`/ambulances`)**: Status badges (`AVAILABLE`, `ON_DUTY`, `MAINTENANCE`), filter by availability, quick inline status updates, and vehicle CRUD.
- **Hospital & Bed Management (`/hospitals`)**: Total capacity vs available beds with dynamic percentage progress bars, quick bed counter updates directly from table rows, and hospital CRUD.
- **Emergency Dispatch Queue (`/requests`)**: Register new emergency incidents, status filtering (`PENDING`, `ASSIGNED`, `COMPLETED`), cross-module assignment dispatch (auto-updates ambulance status to `ON_DUTY` and decrements hospital `availableBeds`), and completion workflow (frees ambulance back to `AVAILABLE`).
- **Activity Audit Log (`/admin/log`)**: Audit trail browser table logging system initialization, patient registrations, ambulance status updates, dispatches, and request completions.

---

## Technology Stack

- **Java Version**: Java 21 (compatible with Java 17+)
- **Backend Framework**: Spring Boot `3.2.5` (`spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-validation`)
- **Security**: Spring Security 6 (`spring-boot-starter-security`, `thymeleaf-extras-springsecurity6`)
- **Templating Engine**: Thymeleaf (Server-rendered HTML - zero Node.js/npm required)
- **Database**: MySQL 8.x / H2 In-Memory Database (Spring Data JPA / Hibernate)
- **Styling & UI**: Bootstrap 5 CDN + FontAwesome 6 CDN + Custom Emergency Medical Theme CSS
- **Build Tool**: Apache Maven (`pom.xml`)

---

## Default Login Credentials

Initial administrative and staff accounts are automatically seeded upon first run:

| Role | Username | Password | Access Level |
| :--- | :--- | :--- | :--- |
| **Chief Administrator** | `admin` | `admin123` | Full Access (Dashboard, CRUD, Audit Log) |
| **Dispatcher Staff** | `staff` | `admin123` | Staff Access |

---

## Quick Start & Setup Instructions

### Option 1: Instant Run (Default - H2 In-Memory Database)

The application is pre-configured with H2 in-memory mode so you can run it immediately without setting up MySQL:

1. Open a terminal in the project directory:
   ```bash
   cd RapidAid
   ```
2. Build and run with single Maven command:
   ```bash
   mvn spring-boot:run
   ```
3. Open your browser and navigate to:
   [http://localhost:8080](http://localhost:8080)

---

### Option 2: MySQL 8 Database Setup

To run against a local MySQL 8 database:

1. **Create Database**:
   Open MySQL Workbench or MySQL Command Line and run:
   ```sql
   CREATE DATABASE IF NOT EXISTS rapidaid_db;
   ```
2. **Import Schema & Sample Data (Optional)**:
   You can manually run `schema.sql` and `sample_data.sql` included in the root directory:
   ```bash
   mysql -u root -p rapidaid_db < schema.sql
   mysql -u root -p rapidaid_db < sample_data.sql
   ```
   *(Note: The Java application also includes automatic database initialization via `DataInitializer.java` on first launch).*

3. **Update Database Credentials**:
   Open `src/main/resources/application.properties` and uncomment the MySQL section while commenting out H2:

   ```properties
   # MySQL 8 Configuration
   spring.datasource.url=jdbc:mysql://localhost:3306/rapidaid_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
   spring.datasource.username=root
   spring.datasource.password=YOUR_MYSQL_PASSWORD

   spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
   ```

4. **Launch Application**:
   ```bash
   mvn spring-boot:run
   ```
5. Open [http://localhost:8080](http://localhost:8080) in your browser.

---

## Project Structure Overview

```
RapidAid/
├── pom.xml                                   # Maven dependencies & build configuration
├── README.md                                  # Setup & user documentation
├── schema.sql                                 # Root SQL database schema
├── sample_data.sql                            # Root SQL seed data script
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── rapidaid/
        │           ├── RapidAidApplication.java # Spring Boot Main Entry Point
        │           ├── config/                # SecurityConfig, CustomUserDetailsService, DataInitializer
        │           ├── controller/            # Controllers (Home, Auth, Dashboard, Patient, Ambulance, Hospital, Request, Log, Error)
        │           ├── model/                 # JPA Entities (User, Patient, Ambulance, Hospital, Request, Log, Enums)
        │           ├── repository/            # Spring Data JPA Repositories
        │           └── service/               # Core Business Logic & Cross-Module Dispatch Services
        └── resources/
            ├── application.properties         # DB connections, JPA & Thymeleaf settings
            ├── schema.sql                     # Spring auto-init schema
            ├── data.sql                       # Spring auto-init sample data
            ├── static/
            │   ├── css/style.css              # Custom Emergency Theme & Layout CSS
            │   └── js/main.js                 # Client-side scripts & confirmation modals
            └── templates/
                ├── fragments/                 # Navbar, Sidebar, Footer, Alerts
                ├── patients/                  # Patient views (list, form, detail)
                ├── ambulances/                # Ambulance views (list, form)
                ├── hospitals/                 # Hospital views (list, form)
                ├── requests/                  # Emergency Request views (list, create, assign)
                ├── admin/                     # Activity audit log view
                ├── home.html                  # Public landing page
                ├── login.html                 # Login page
                ├── dashboard.html             # Command Center Dashboard
                └── error.html                 # Custom friendly error handler
```

---

---

## 🚀 Live Cloud Deployment Guide

RapidAid includes a pre-configured `Dockerfile` and `render.yaml` for instant deployment on cloud platforms like **Render** or **Railway**.

### Option A: 1-Click Deployment on Render (Free)

1. Log in to [Render.com](https://render.com) and click **New +** -> **Web Service**.
2. Connect your GitHub repository: `https://github.com/Grijasri/RapidAid`.
3. Choose **Docker** as the Runtime environment (Render will automatically detect the included `Dockerfile`).
4. Select the **Free** instance type.
5. Click **Create Web Service**. Render will build the container and deploy your live URL (e.g. `https://rapidaid.onrender.com`).

### Option B: Deployment on Railway

1. Log in to [Railway.app](https://railway.app) and click **New Project** -> **Deploy from GitHub Repo**.
2. Select `Grijasri/RapidAid`.
3. Railway will automatically detect the `Dockerfile` and build/deploy your application.

---

*RapidAid - Smart Emergency Ambulance Coordination System*
