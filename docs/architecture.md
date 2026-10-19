# Architecture & System Design

## 1. System Vision

The **Apartment Maintenance Complaint Tracker** is a secure, multi-tenant maintenance request tracking platform tailored for residential housing societies.

The system enforces strict tenant isolation, auditable residency management, and an explicit rule of **at most one active complaint per apartment unit at a time**.

## 2. Roles and Permissions Matrix

| Capability / Action | SUPER_ADMIN | SOCIETY_ADMIN | RESIDENT |
|---|:---:|:---:|:---:|
| Create / Deactivate Societies | Yes | No | No |
| Appoint / Manage Society Admins | Yes | No | No |
| View Platform Metrics / Operational Health | Yes | No | No |
| Manage Society Buildings & Apartment Units | No | Yes (own society only) | No |
| Review & Verify Resident Onboarding Requests | No | Yes (own society only) | No |
| Review & Approve Residency Transfers | No | Yes (own society only) | No |
| Manage Complaint Categories | No | Yes (own society only) | No |
| View Complaints & Timeline | No | Yes (own society only) | Yes (own unit only) |
| Transition Complaint Status & Add Resolution Notes | No | Yes (own society only) | No |
| Submit New Complaint | No | No | Yes (if no active complaint on unit) |
| Add Comments to Complaint | No | Yes (own society only) | Yes (own unit only) |
| Request Reopening of Closed Complaint | No | No | Yes (own unit, within policy window) |
| Direct Role Self-Assignment | No | No | No |

> **No Staff Role**: Maintenance repairs are executed by external third-party contractors coordinated by Society Admins. Staff accounts, assignment queues, and worker dashboards are intentionally out of scope.

## 3. High-Level Architecture

The system uses a **Modular Monolith** architecture:
- **Frontend**: Single-Page Application (SPA) built with React 19, TypeScript, and Vite.
- **Backend**: Spring Boot 3.5.16 REST API powered by Java 21 LTS, Spring Security 6, Spring Data JPA, and Flyway.
- **Database**: PostgreSQL 17 with partial unique indexes for business invariant enforcement.

```text
┌─────────────────────────────────────────────────────────────┐
│                   React 19 + TypeScript SPA                 │
│       Pages • Features • TanStack Query • Route Guards       │
└──────────────────────────────┬──────────────────────────────┘
                               │ HTTPS / JSON REST API
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                 Spring Boot 3.5.16 Monolith                 │
│                                                             │
│  ┌────────────────── HTTP / API Layer ──────────────────┐  │
│  │ REST Controllers • Request/Response DTOs • Validator │  │
│  └─────────────────────────┬─────────────────────────────┘  │
│                            ▼                                │
│  ┌──────────────── Application Service Layer ───────────┐  │
│  │ Auth • Society • User • Unit • Residency • Complaint │  │
│  │ Comment • Attachment • Notification • Reporting      │  │
│  └─────────────────────────┬─────────────────────────────┘  │
│                            ▼                                │
│  ┌──────────────── Persistence Layer (JPA) ─────────────┐  │
│  │ Repositories • Specifications • Flyway Migrations   │  │
│  └─────────────────────────┬─────────────────────────────┘  │
│                            ▼                                │
│                    PostgreSQL Database                      │
│        (Schema validation, Partial Unique Indexes)          │
└─────────────────────────────────────────────────────────────┘
```

## 4. Backend Package Structure

Package-by-feature organization adhering to controller (`api`) → service (`application`) → domain (`domain`) → repository (`infrastructure`):

```text
com.example.maintenance
├── MaintenanceApplication.java
├── shared/
│   ├── config/          # CORS, Jackson, JPA, Async configurations
│   ├── exception/       # ErrorResponse, GlobalExceptionHandler, custom exceptions
│   ├── security/        # SecurityFilterChain, UserDetails, JWT handling
│   ├── web/             # Base controller helpers, pagination utilities
│   └── validation/      # Custom constraint validators
├── auth/                # Login, token issuance, current-user context
├── society/             # Society CRUD, management, multi-tenant boundaries
├── user/                # User accounts, credentials, password reset
├── apartment/           # Buildings, wings, apartment units
├── residency/           # Occupancy records, verification, transfer workflows
├── category/            # Complaint categories (global/society)
├── complaint/           # Complaint lifecycle, active constraint, status transitions
├── attachment/          # Private image/audio metadata and storage abstraction
├── comment/             # Complaint comments and internal notes
├── notification/        # Email dispatcher, event listeners
└── reporting/           # Summary statistics and admin reports
```

## 5. Tenant Isolation Principles

1. **Server-Side Enforcement**: The tenant boundary (`society_id`) is strictly resolved from the authenticated user's session/token or server-side DB context. It is never trusted from request body payloads or client headers.
2. **Society Admin Scope**: A `SOCIETY_ADMIN` is associated with exactly one society for the MVP. Every query for units, resident requests, complaints, and reports filters on that society ID.
3. **Resident Scope**: A `RESIDENT` can only view and mutate records associated with their active apartment unit. Unverified residents have no complaint-submission or data-reading capabilities.
