# Changelog

All notable changes to this project will be documented in this file.

## [1.0.0] - 2026-10-28

### Added
- **Phase 10: Email Notifications**
  - Optional asynchronous email alerts to active Society Admins after complaint submission commits
  - SMTP, sender, authentication, and STARTTLS environment configuration
  - Mail delivery failure handling that does not interrupt complaint submission

## [0.9.0] - 2026-10-27

### Added
- **Phase 9: Private Complaint Attachments**
  - Authenticated upload, listing, and download for complaint images and audio
  - Private local file storage with randomized keys and database metadata
  - MIME type, file signature, request size, and per-media size validation
  - Resident unit and Society Admin tenant checks for every attachment operation
  - Simple complaint screen controls to add and download files

## [0.8.0] - 2026-10-27

### Added
- **Phase 8: Complaint Management & Status History**
  - Resident and assigned Society Admin complaint lists, with tenant-scoped detail and history access
  - Society Admin complaint status updates with enforced transitions and required resolution notes
  - Status audit history for submission and every administrative transition
  - Simple complaint management panel for status updates and history review

## [0.7.0] - 2026-10-27

### Added
- **Phase 7: Resident Complaint Submission**
  - Resident-only complaint creation using server-resolved user, active unit, and society context
  - Category ownership validation and duplicate active complaint conflicts (`409 ACTIVE_COMPLAINT_EXISTS`)
  - Simple resident complaint form with priority and location details

## [0.6.0] - 2026-10-26

### Added
- **Phase 6: Units & Resident Onboarding**
  - Society Admin unit listing and creation with server-side society boundary checks
  - Public resident registration requests with inactive accounts and pending residencies
  - Society Admin approval/rejection workflow, account activation, and active-residency enforcement
  - Simple dashboard forms for units, resident review, and public access requests

### Changed
- Removed infrastructure stack/version labels from the frontend header and dashboard

## [0.5.0] - 2026-10-23

### Added
- **Phase 5: Society & Society Admin Management**
  - Super Admin secured society create, paginated list, detail, update, deactivation, and admin appointment APIs
  - Server-side Society Admin tenant scope resolver
  - Dashboard society management interface for creation, activation control, and admin account appointment

## [0.4.0] - 2026-10-23

### Added
- **Phase 4: Authentication & Security Foundation**
  - Stateless Spring Security 6 OAuth2 Resource Server JWT validation and HS256 token issuance with 24-hour default expiry
  - BCrypt password hashing, generic invalid credential responses, and environment configurable Super Admin bootstrap
  - Public login endpoint, protected current-user endpoint, role claim authority mapping, explicit CORS origins, and JSON 401/403 responses
  - Frontend sign in, session profile, active-user header badge, and sign out controls

## [0.3.0] - 2026-10-20

### Added
- **Phase 3: First Vertical Slice (Categories API & Agent Handoff Guide)**
  - `CategoryRepository` Spring Data JPA repository for global and society-specific category queries
  - `CategoryService` transactional application service with DTO mapping
  - `CategoryController` REST controller exposing `GET /api/v1/categories?societyId=...`
  - `CategoryResponse` DTO with `isGlobal` and `societyId` attributes
  - Springdoc OpenAPI 2.8.5 integration with Swagger UI at `/swagger-ui.html` and docs at `/v3/api-docs`
  - Automated unit test `CategoryServiceTest` and WebMvc test `CategoryControllerTest` (100% pass)
  - Minimal frontend `CategoryList` component integrated into `DashboardView` with loading, error, and empty states
  - `docs/AGENT_HANDOFF.md` comprehensive context guide for AI agent session continuations

## [0.2.0] - 2026-10-19

### Added
- **Phase 2: Local PostgreSQL Connection & Migration Foundation**
  - Dedicated PostgreSQL connection configuration with fallback to application profile
  - Flyway migration `V1__initial_schema.sql` covering all 11 core domain tables, constraints, foreign keys, and seeded categories
  - Partial unique indexes enforcing core business invariants:
    - `uq_active_residency_per_unit` (at most one ACTIVE residency per apartment)
    - `uq_one_active_complaint_per_apartment` (at most one ACTIVE complaint per apartment)
  - Complete JPA domain entities with optimistic locking (`@Version`) and corresponding domain enums
  - Verified Hibernate schema validation (`ddl-auto: validate`) against PostgreSQL

## [0.1.0] - 2026-10-19

### Added
- **Phase 0: Specifications & Final Architecture**
  - Architecture overview (`docs/architecture.md`)
  - Database schema & ER design with invariant partial indexes (`docs/database-design.md`)
  - Complete REST API contract (`docs/api-contract.md`)
  - Architectural decision record ADR-0001 for 1-to-1 Society Admin scope (`docs/decisions/0001-society-admin-scope.md`)
- **Phase 1: Project Scaffolding & Foundation**
  - Git repository structure with `.gitignore`, `.editorconfig`, `.nvmrc`, `.env.example`, and `README.md`
  - Local infrastructure Docker Compose file for PostgreSQL 17 (`infra/docker-compose.yml`)
  - Spring Boot 3.5.16 backend setup with Java 21, Maven Wrapper, and package-by-feature layout
  - React 19 + TypeScript frontend setup with Vite, React Router, TanStack Query, and Lucide React
