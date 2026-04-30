🏥 Patient Tracker – Backend (Monolith, Pre‑Microservices)
📌 Overview
Patient Tracker is a healthcare management backend built with Spring Boot 3.5.x, designed to manage users, doctors, patients, appointments, 
medical cases, availability, and notifications.
The backend currently runs as a well‑structured monolith, intentionally prepared for gradual migration to microservices using Docker Compose 
and Spring Cloud.

🛠 Tech Stack

Java 17
Spring Boot 3.5.x
Spring Security (JWT based)
Spring Data JPA
Hibernate
Flyway (database migrations)
MySQL
Maven
Docker (planned for microservices phase)


🧩 Core Modules & Responsibilities
🔐 Authentication & Authorization

JWT‑based authentication
Roles: ADMIN, DOCTOR, PATIENT
Secure role‑based access (@PreAuthorize)
Prepared for extraction into auth‑service


👤 User Management (Admin)

Activate / block users
Role assignment
Links users to doctor/patient profiles
Central admin control


🩺 Doctor Management

Doctor profile CRUD
Approval / Reject workflow
Soft delete & Restore
Hard delete (admin‑only, guarded)
Doctor availability management (day, time slots)
Business rules enforced:

Deleted doctors cannot be approved
Unapproved doctors cannot accept appointments




🧑‍🤝‍🧑 Patient Management

Patient profile CRUD
Soft delete & Restore
Hard delete (admin‑only)
Patient self‑service profile update
Deleted patients are excluded from normal reads


📅 Appointment Management

Create, view, manage appointments
Validates:

Doctor approval
Doctor availability
Patient active status


Ready for event‑driven notifications


📁 Medical Case Management

Medical case CRUD
Automatic case versioning
Full case history retrieval
Immutable historical records


🔔 Notifications

Email / SMS abstraction
Triggered after DB commit
Designed for event‑driven extraction later


🗃 Database Design

Single MySQL database (monolith phase)
Entities:

users
doctors
patients
appointments
medical_cases
medical_case_versions


Soft delete implemented via deleted flags
No unsafe hard deletes in business controllers


🔐 API Security

JWT required for all secured endpoints
Stateless authentication
Role‑based access enforcement


▶️ Running the Backend Locally
mvn clean spring-boot:run

Application runs on:
http://localhost:8080

✅ Current State
✅ Stable
✅ Production‑ready monolith
✅ Admin safety implemented
✅ Frontend fully integrated
✅ Ready for microservices extraction

🔮 Next Phase (Coming Up)

API Gateway (Spring Cloud Gateway)
Auth service extraction
Doctor, Patient, Appointment microservices
Docker Compose orchestration
