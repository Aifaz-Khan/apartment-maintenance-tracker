# Changelog

All notable changes to this project will be documented in this file.

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
