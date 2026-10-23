# Agent Handoff & Project Context Guide

> **Purpose**: This document provides an instant, token-efficient briefing for any AI agent or developer continuing work on this project in a new session. Read this file first to resume development immediately.

---

## 1. Project Overview & Core Domain Rules

The **Apartment Maintenance Complaint Tracker** is a secure, multi-tenant portal for housing societies:
- **Roles**:
  - `SUPER_ADMIN`: Manages societies and appoints Society Admins. Does not manage routine complaints.
  - `SOCIETY_ADMIN`: Administers exactly one society (MVP): units, resident onboarding verification, category customization, complaints, and status transitions.
  - `RESIDENT`: Authorized household representative for an apartment unit. Can lodge and track complaints for their unit only.
  - **No Staff Role**: External vendors handle maintenance repairs coordinated by Society Admins. No staff login or worker assignment dashboards.
- **Critical Business Invariants**:
  - **Tenant Isolation**: A Society Admin can never view or mutate data belonging to another society. `society_id` is derived from server-side authenticated context, never trusted from client request payloads.
  - **One Active Residency per Unit**: Enforced at DB level with partial index `uq_active_residency_per_unit` on `unit_residencies (apartment_unit_id) WHERE status = 'ACTIVE'`.
  - **At Most One Active Complaint per Unit**: An apartment unit may have at most ONE unclosed complaint at any time, regardless of which resident lodged it. Enforced at DB level with partial index `uq_one_active_complaint_per_apartment` on `complaints (apartment_unit_id) WHERE status IN ('SUBMITTED', 'ACKNOWLEDGED', 'IN_PROGRESS', 'ON_HOLD', 'RESOLVED', 'REOPENED')`. Terminal states `CLOSED` and `CANCELLED` unblock new complaint submissions.

---

## 2. Technology Baseline & Environment

- **Architecture**: Modular Monolith.
- **Backend**:
  - Java 21 LTS (located on local Mac at `/opt/homebrew/opt/openjdk@21`).
  - Spring Boot `3.5.16` (`spring-boot-starter-parent:3.5.16`).
  - Spring Data JPA + Hibernate with `spring.jpa.hibernate.ddl-auto=validate`.
  - Flyway migrations (`db/migration/`) as schema source of truth.
  - Springdoc OpenAPI 2.8.5 (`/swagger-ui.html`, `/v3/api-docs`).
  - Build tool: Maven Wrapper (`./mvnw`).
- **Frontend**:
  - Node 24 LTS / npm (`.nvmrc`).
  - React 19, TypeScript 5 (Strict mode), Vite 6.
  - TanStack Query 5, React Router 7, Axios, React Hook Form, Zod, Lucide React.
- **Database**:
  - Local PostgreSQL 17/18 on `127.0.0.1:5432`.
  - Database name: `maintenance_tracker`.
  - Dedicated role: `maintenance_app` / `maintenance_pass` (with fallback to system user).

---

## 3. Repository Layout & Package Structure

```text
apartment-maintenance-tracker/
├── backend/
│   ├── pom.xml
│   ├── mvnw / mvnw.cmd
│   └── src/
│       ├── main/java/com/example/maintenance/
│       │   ├── MaintenanceApplication.java
│       │   ├── shared/            # config, exception, security, web, validation
│       │   ├── auth/              # login, token issuance, current user
│       │   ├── society/           # societies, society admin mapping
│       │   ├── user/              # user accounts, credentials
│       │   ├── apartment/         # buildings, apartment units
│       │   ├── residency/         # residencies, transfer requests
│       │   ├── category/          # complaint categories
│       │   ├── complaint/         # complaints, status history
│       │   ├── attachment/        # private image/audio media
│       │   ├── comment/           # discussion comments
│       │   ├── notification/      # email alerts
│       │   └── reporting/         # reports & aggregates
│       └── main/resources/
│           ├── application.yml
│           ├── application-local.yml
│           ├── application-test.yml
│           └── db/migration/V1__initial_schema.sql
├── frontend/
│   ├── package.json
│   ├── vite.config.ts
│   └── src/
│       ├── app/                   # queryClient, router, providers
│       ├── components/            # layout, UI primitives
│       ├── features/              # domain features (categories, complaints, etc.)
│       ├── lib/                   # apiClient.ts (Axios)
│       └── types/                 # shared TypeScript interfaces
├── docs/
│   ├── architecture.md
│   ├── database-design.md
│   ├── api-contract.md
│   ├── AGENT_HANDOFF.md           # This document
│   └── decisions/0001-society-admin-scope.md
└── infra/
    └── docker-compose.yml
```

---

## 4. Phase Status & Progress Checklist

- [x] **Phase 0 — Finalize rules and diagrams**: Architecture, schema, API contracts, ADR-0001 under `docs/`.
- [x] **Phase 1 — Repository Scaffolding & Builds**: Spring Boot 3.5.16 + React 19 + TypeScript baseline with clean builds.
- [x] **Phase 2 — Database Connection & Migration Foundation**: PostgreSQL connected, `maintenance_app` user, `V1__initial_schema.sql` applied, 11 JPA domain entities created, partial unique indexes active, Hibernate `validate` passing.
- [x] **Phase 3 — First Vertical Slice**: Complaint Categories (`GET /api/v1/categories`), DTOs, OpenAPI documentation, unit & controller tests, minimal UI component.
- [x] **Phase 4 — Authentication & Security Foundation**: Spring Security 6, JWT token issuance, password hashing, `POST /api/v1/auth/login`, `GET /api/v1/users/me`, role authorization, Super Admin bootstrap.
- [x] **Phase 5 — Society & Society Admin Management**: Super Admin society CRUD, Society Admin appointments, tenant isolation checks.
- [ ] **Phase 6 — Units, Resident Onboarding & Verification**: Building & unit creation, resident registration request, Society Admin approval workflow.
- [ ] **Phase 7 — Complaint Creation & One-Active-Complaint Rule**: Resident complaint creation, server-side context resolution, `409 ACTIVE_COMPLAINT_EXISTS` conflict handling.
- [ ] **Phase 8 — Society Admin Complaint Management & Status History**: Status transition map enforcement, audit history persistence, resolution notes.
- [ ] **Phase 9 — Private Attachments (Image & Audio)**: Local private storage abstraction, MIME/size validation, authorized streaming.
- [ ] **Phase 10 — Email Notifications**: Society Admin email dispatcher on complaint submission.
- [ ] **Phase 11 — Residency Transfer Workflow**: Residency transfer request, admin atomic approval.
- [ ] **Phase 12 — Comments, Reopening, Dashboards & Reports**: Complaint discussion, reopening window, society-scoped metrics.
- [ ] **Phase 13 — Hardening & Comprehensive Tests**: Integration test suite, Testcontainers.
- [ ] **Phase 14 — CI/CD & Cloud Deployment**: GitHub Actions, Neon PostgreSQL, Vercel & Render.



### Phase 5 Society Management & Tenant Scope
- Super Admin APIs at `/api/v1/super-admin/societies` provide paginated list, detail, create, partial update/status control, and Society Admin appointment. All routes require `ROLE_SUPER_ADMIN`.
- Admin appointment creates a BCrypt protected `SOCIETY_ADMIN` account and a `society_admins` mapping in one transaction; duplicate account emails are rejected. Initial credentials are supplied through the protected Super Admin route and must be shared securely. Email invitation delivery is deferred to Phase 10.
- `SocietyAccessService` resolves a Society Admin's assigned society from the authenticated user mapping and rejects any mismatched society ID. Future tenant-owned endpoints should use this service for all reads and writes.
- Deactivation sets society status to `INACTIVE`; it preserves society data and assignments.

### Phase 4 Authentication & Security
- JWT access tokens are HS256, issued and validated through Spring Security Nimbus support, with issuer `maintenance-tracker` and 86400 second default expiration. Configure `JWT_SECRET` with at least 32 bytes in deployed environments.
- `POST /api/v1/auth/login` is public and returns a generic `INVALID_CREDENTIALS` response for invalid, missing, or inactive accounts. `GET /api/v1/users/me` requires a valid bearer token.
- Passwords use BCrypt. Roles come from controlled database account creation and token role claims map to `ROLE_<ROLE>`. Public endpoints cannot assign roles.
- The startup runner seeds a Super Admin only if none exists, using `SUPERADMIN_EMAIL` and `SUPERADMIN_PASSWORD`. Local fallback: `superadmin@maintenancetracker.com` / `Admin@12345`; override these outside local development.
- CORS origins are configured through `CORS_ALLOWED_ORIGINS` (defaults to localhost ports 5173 and 3000).

---

## 5. Verification Commands for Any New Agent

```bash
# Backend Verification (Java 21)
cd backend
JAVA_HOME=/opt/homebrew/opt/openjdk@21 ./mvnw clean test

# Frontend Verification
cd frontend
npm run build

# Git Status
git status
```
