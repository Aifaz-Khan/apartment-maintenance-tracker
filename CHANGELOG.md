# Changelog

All notable changes to this project will be documented in this file.

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
