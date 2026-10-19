# Apartment Maintenance Complaint Tracker

A multi-tenant housing society maintenance request tracking system with role-based access control, tenant isolation, and strict single-active-complaint enforcement per residential unit.

## Architecture & Technology Baseline

- **Monorepo Structure**: Spring Boot backend + React TypeScript frontend
- **Backend**: Spring Boot 3.5.16, Java 21 LTS, Spring Data JPA, Spring Security 6, Flyway, PostgreSQL Driver
- **Frontend**: React 19, TypeScript 5, Vite, React Router, TanStack Query, React Hook Form, Zod, Lucide React
- **Database**: PostgreSQL 16/17 (Local development and Neon compatibility)
- **Tooling**: Maven Wrapper (`./mvnw`), Node 24 LTS / npm, Docker Compose

---

## Directory Structure

```text
apartment-maintenance-tracker/
├── backend/               # Spring Boot 3.5.16 application
│   ├── pom.xml
│   ├── mvnw / mvnw.cmd
│   └── src/
├── frontend/              # React 19 + TypeScript SPA
│   ├── package.json
│   ├── vite.config.ts
│   └── src/
├── docs/                  # Architecture, schema, API contracts, ADRs
│   ├── architecture.md
│   ├── database-design.md
│   ├── api-contract.md
│   └── decisions/
├── infra/                 # Local docker-compose environment
│   └── docker-compose.yml
├── .editorconfig
├── .gitignore
├── .nvmrc
├── .env.example
├── README.md
└── CHANGELOG.md
```

---

## Getting Started

### Prerequisites
- JDK 21 LTS
- Node.js 24 LTS (or compatible Node 22+) & npm
- PostgreSQL 16 or 17

### Running the Backend
```bash
cd backend
./mvnw clean compile
./mvnw test
```

### Running the Frontend
```bash
cd frontend
npm install
npm run build
npm run dev
```

### Local Database with Docker (Optional)
```bash
cd infra
docker compose up -d
```
