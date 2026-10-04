# RapidAid — Smart Emergency Ambulance Coordination System

**RapidAid** is a production-ready, full-stack Java emergency coordination platform designed for emergency medical services to coordinate public incident requests, automated AI priority scoring, nearest-ambulance GPS matching, live STOMP WebSocket map tracking, and SMS/Email notifications.

It evolves traditional console/paper register workflows into a modern command center powered by **Spring Boot 3**, **Thymeleaf**, **Spring Security**, **WebSocket STOMP**, **Leaflet Maps**, and **MySQL 8** (with zero-config H2 fallback).

---

## 🌟 Expanded Platform Features

1. **Public Request Portal (`/request`)**
   - Unauthenticated emergency submission form requiring no login.
   - HTML5 Geolocation API integration ("Use My GPS Location" button).
   - In-memory sliding-window IP rate-limiting (max 5 requests per 10 mins per IP) and bot honeypot validation.
   - Live plain-language tracking page (`/request/track/{id}`) showing progress: *"Request received"* → *"Ambulance assigned"* → *"On the way"* → *"Completed"*.

2. **GPS Live Tracking & WebSocket Telemetry (`/driver/share-location`)**
   - Driver-facing web interface using `navigator.geolocation.watchPosition()` broadcasting GPS coordinates every 10-15 seconds via `POST /api/v1/ambulances/{id}/location`.
   - Real-time Spring WebSocket + STOMP broker broadcasting position updates to `/topic/tracking`.
   - Embedded interactive Leaflet.js map on tracking page (`/request/track/{id}`) moving markers live.
   - Stale location detection (>3 minutes without updates triggers a warning badge).

3. **Priority-Based Assignment & AI Urgency Scoring**
   - Extensible `PriorityEngine` with `RuleBasedPriorityEngine` analyzing emergency types & description keywords to assign priority scores (0-100) and badges (`HIGH`, `MEDIUM`, `LOW`).
   - Admin pending request queue sorted by priority score first, then creation time.
   - `DistanceService` (Haversine formula) computing straight-line distance to available ambulances.
   - `RouteEtaCalculator` extension point providing nearest-ambulance AI suggestions with one-click auto-select on `/requests/assign/{id}` (ready for Google Maps Directions API integration).

4. **SMS Notifications (Twilio Integration)**
   - `SmsNotificationService` sending instant SMS to assigned ambulance drivers and requesters on dispatch assignment and request completion.
   - Graceful degradation: missing Twilio credentials log startup warning and no-op without throwing errors.
   - Persistent delivery logs stored in `notification_log` database table.

5. **Email Notifications (Jakarta SMTP)**
   - `EmailNotificationService` sending HTML confirmation emails to requesters upon submission and incoming dispatch alerts to destination hospitals upon assignment.
   - Graceful degradation: missing SMTP settings log startup warning and record `DISABLED_NOOP` status in audit logs.

6. **Product Marketing Landing Page (`/`) & Hospital Partner Portal**
   - Modern product landing page featuring hero imagery, workflow breakdown, live network metrics (including average response time), and FAQ accordion.
   - Hospital partner interest form storing inquiries in `partner_inquiries` table, accessible to admins at `/admin/partner-inquiries`.
   - Admin notification audit log viewer at `/admin/notifications`.

---

## 🔑 Environment Variables

Configure the following optional environment variables to enable real-time SMS and Email delivery. If omitted, the application will degrade gracefully with clear startup warnings and `DISABLED_NOOP` audit entries.

### SMS Notifications (Twilio)
| Environment Variable | Description | Example / Default |
| :--- | :--- | :--- |
| `TWILIO_ACCOUNT_SID` | Twilio Account SID | `ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx` |
| `TWILIO_AUTH_TOKEN` | Twilio Auth Token | `your_auth_token` |
| `TWILIO_FROM_NUMBER` | Twilio registered phone number | `+1234567890` |

### Email Notifications (Real SMTP / Gmail Setup)
| Environment Variable | Description | Example / Default |
| :--- | :--- | :--- |
| `SPRING_MAIL_HOST` / `MAIL_HOST` | SMTP server host | `smtp.gmail.com` |
| `SPRING_MAIL_PORT` / `MAIL_PORT` | SMTP server port | `587` |
| `SPRING_MAIL_USERNAME` / `MAIL_USERNAME` | Real Email Address | `your-email@gmail.com` |
| `SPRING_MAIL_PASSWORD` / `MAIL_PASSWORD` | Real Email / App Password | `xxxx xxxx xxxx xxxx` (16-char Gmail App Password) |
| `SPRING_MAIL_FROM` / `MAIL_FROM` | Sender display email | `noreply@rapidaid.com` |

#### 📧 How to Send Real Emails to User Inboxes (Gmail Setup)
1. Go to your **Google Account** → **Security** → Enable **2-Step Verification**.
2. Search for **App Passwords** in your Google Account settings.
3. Generate a new App Password (select App: *Other*, name it `RapidAid`).
4. Set environment variables before launching the app:
   ```powershell
   $env:MAIL_USERNAME="your-email@gmail.com"
   $env:MAIL_PASSWORD="your-16-char-app-password"
   $env:JAVA_HOME="C:\Program Files\Java\jdk-21"; ./mvnw spring-boot:run
   ```
5. When a public user submits an emergency request or when dispatch accepts the request, a real HTML email notification will land directly in the user's inbox!


---

## 🧪 How to Test the System End-to-End

### 1. Test Public Request Portal & Tracking
1. Open your browser and navigate to `http://localhost:8080/request`.
2. Click **"Use My GPS Location"** to fill coordinates via browser Geolocation API.
3. Fill in Patient Name (*e.g., Jane Doe*), Phone (*e.g., 9876543210*), Email (*e.g., jane@example.com*), Emergency Type (*e.g., Cardiac / Heart Attack*), and submit.
4. You will be redirected to `http://localhost:8080/request/track/{id}` displaying status *"Request received"*, priority rating, and an embedded Leaflet map.

### 2. Test Driver Location Sharing
1. Open a new browser tab/window at `http://localhost:8080/driver/share-location`.
2. Select an ambulance unit (*e.g., TN01AB1001*).
3. Click **"START SHARING LOCATION NOW"**. The browser will stream live position updates every 10-15s to `/api/v1/ambulances/{id}/location`.

### 3. Test Admin Priority Queue & AI Nearest Match
1. Log in at `http://localhost:8080/login` with `admin` / `admin123`.
2. Navigate to `/dashboard` or `/requests`. Notice your new request is sorted at the top of the queue with a **HIGH** priority badge.
3. Click **"Assign"** on the request.
4. The system will display a green callout box: **"RECOMMENDED NEAREST AMBULANCE"** showing distance in km and estimated ETA in minutes.
5. Click **"Auto-Select Closest Unit"** and select a destination hospital, then click **"Confirm & Dispatch Unit"**.

### 4. Verify Live Map & Notifications
1. Return to the public tracking tab (`/request/track/{id}`). Notice the status has dynamically updated over WebSocket STOMP to *"Ambulance assigned - On the way"*, and the ambulance marker appears live on the map.
2. Navigate to `http://localhost:8080/admin/notifications` to inspect logged SMS & Email notification attempts.

---

## ✉️ Verifying SMS & Email Delivery Modes

### Unconfigured Mode (Default)
- **Startup Behavior**: Console prints:
  - `WARN: SMS notifications disabled - no Twilio credentials configured.`
  - `WARN: Email notifications disabled - no SMTP credentials configured.`
- **Execution Behavior**: Application creates requests and assigns dispatches without throwing exceptions.
- **Audit Verification**: Visit `/admin/notifications`. Delivery attempts are recorded with status `DISABLED_NOOP` and clear diagnostic messages.

### Configured Mode (Production)
- Set `TWILIO_*` and `MAIL_*` environment variables in your terminal or shell prior to running:
  ```bash
  export TWILIO_ACCOUNT_SID="ACxxx..."
  export TWILIO_AUTH_TOKEN="xxx..."
  export TWILIO_FROM_NUMBER="+1234567890"
  export MAIL_HOST="smtp.gmail.com"
  export MAIL_PORT="587"
  export MAIL_USERNAME="you@gmail.com"
  export MAIL_PASSWORD="app_password"
  mvn spring-boot:run
  ```
- **Execution Behavior**: Real SMS messages are dispatched to driver & patient phone numbers, and real HTML emails are sent to hospitals and patients.
- **Audit Verification**: Visit `/admin/notifications` to see delivery entries with status `SUCCESS`.

---

## 🛠️ Technology Stack & Dependencies

- **Java Version**: Java 21 (compatible with Java 17+)
- **Backend Framework**: Spring Boot `3.2.5` (`spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-validation`, `spring-boot-starter-websocket`, `spring-boot-starter-mail`)
- **Third-Party Integrations**: Twilio SDK (`10.1.0`), Leaflet.js Maps, SockJS, StompJS
- **Security**: Spring Security 6 (`spring-boot-starter-security`, `thymeleaf-extras-springsecurity6`)
- **Templating Engine**: Thymeleaf (Server-rendered HTML)
- **Database**: MySQL 8.x / H2 In-Memory Database (Spring Data JPA / Hibernate)
- **Styling & UI**: Bootstrap 5 + FontAwesome 6 + Custom Emergency Theme CSS
- **Build Tool**: Apache Maven (`pom.xml`)

---

## 👤 Default Login Credentials

| Role | Username | Password | Access Level |
| :--- | :--- | :--- | :--- |
| **Chief Administrator** | `admin` | `admin123` | Full Access (Dashboard, Dispatches, Audit Logs, Partner Inquiries) |
| **Dispatcher Staff** | `staff` | `admin123` | Dispatch & Patient Access |

---

## 🚀 Quick Start Instructions

```bash
# Clone & navigate to directory
cd RapidAid

# Run with Maven (Default H2 database mode)
mvn spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080) in your browser.

---

*RapidAid — Smart Emergency Ambulance Coordination System*
