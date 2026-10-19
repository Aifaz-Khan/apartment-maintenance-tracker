# REST API Contract Specification

Base Path: `/api/v1`  
All timestamps are serialized in ISO-8601 UTC (`yyyy-MM-dd'T'HH:mm:ss'Z'`).

## 1. Standard Error Envelope

When any request results in an HTTP 4xx or 5xx status code, the response body adheres to:

```json
{
  "timestamp": "2026-10-19T10:00:00Z",
  "status": 409,
  "code": "ACTIVE_COMPLAINT_EXISTS",
  "message": "An active complaint already exists for this apartment unit.",
  "path": "/api/v1/complaints",
  "fieldErrors": []
}
```

Validation error response:
```json
{
  "timestamp": "2026-10-19T10:00:00Z",
  "status": 400,
  "code": "VALIDATION_FAILED",
  "message": "Validation failed for request fields.",
  "path": "/api/v1/complaints",
  "fieldErrors": [
    {
      "field": "title",
      "message": "Title is required and must not exceed 200 characters"
    }
  ]
}
```

---

## 2. Standard Pagination Envelope

All paginated endpoints accept:
- `page`: zero-based page index (default: `0`)
- `size`: page size, max 100 (default: `20`)
- `sort`: field,direction (e.g., `createdAt,desc`)

Response structure:
```json
{
  "content": [],
  "pageNumber": 0,
  "pageSize": 20,
  "totalElements": 1,
  "totalPages": 1,
  "isFirst": true,
  "isLast": true
}
```

---

## 3. Endpoints by Domain

### 3.1 Authentication & Profile
- `POST /api/v1/auth/login`
  - Body: `{ "email": "...", "password": "..." }`
  - Response (200): `{ "token": "...", "expiresIn": 86400, "user": { "id": 1, "fullName": "...", "role": "..." } }`
- `GET /api/v1/users/me`
  - Requires Auth. Returns user profile, role, and active residency / society context.

### 3.2 Super Admin: Societies & Society Admins
- `POST /api/v1/super-admin/societies`
  - Creates a new housing society. Role: `SUPER_ADMIN`.
- `GET /api/v1/super-admin/societies`
  - Paginated list of societies. Role: `SUPER_ADMIN`.
- `GET /api/v1/super-admin/societies/{id}`
  - Detail view. Role: `SUPER_ADMIN`.
- `PATCH /api/v1/super-admin/societies/{id}`
  - Update status/details. Role: `SUPER_ADMIN`.
- `POST /api/v1/super-admin/societies/{id}/admins`
  - Appoint / invite a Society Admin for the society. Role: `SUPER_ADMIN`.

### 3.3 Society Admin: Buildings & Units
- `GET /api/v1/societies/{societyId}/units`
  - Paginated list of apartment units in the caller's society. Role: `SOCIETY_ADMIN`.
- `POST /api/v1/societies/{societyId}/units`
  - Create a new apartment unit in the caller's society. Role: `SOCIETY_ADMIN`.

### 3.4 Resident Onboarding & Residencies
- `POST /api/v1/auth/resident-registration-requests`
  - Public registration request by resident for a specified society and unit.
- `GET /api/v1/admin/resident-requests`
  - List pending resident verification requests. Role: `SOCIETY_ADMIN` (own society).
- `PATCH /api/v1/admin/resident-requests/{id}`
  - Body: `{ "action": "APPROVE" | "REJECT", "rejectionReason": "..." }`. Role: `SOCIETY_ADMIN`.
- `POST /api/v1/residency-transfers`
  - Initiate residency transfer request. Role: `RESIDENT` or `SOCIETY_ADMIN`.
- `GET /api/v1/admin/residency-transfers`
  - List transfer requests. Role: `SOCIETY_ADMIN`.
- `PATCH /api/v1/admin/residency-transfers/{id}`
  - Approve or reject transfer. Role: `SOCIETY_ADMIN`.

### 3.5 Categories
- `GET /api/v1/categories`
  - List active categories applicable to the caller's society (includes global + society-specific).
- `POST /api/v1/admin/categories`
  - Create society-scoped category. Role: `SOCIETY_ADMIN`.

### 3.6 Complaints
- `POST /api/v1/complaints`
  - Lodges a new complaint for caller's active apartment unit. Role: `RESIDENT`.
  - Rejection if active complaint exists: `409 ACTIVE_COMPLAINT_EXISTS`.
- `GET /api/v1/complaints`
  - List complaints. Scoped: `RESIDENT` sees their active unit; `SOCIETY_ADMIN` sees their society.
- `GET /api/v1/complaints/{id}`
  - Get complaint detail.
- `PATCH /api/v1/complaints/{id}/status`
  - Body: `{ "newStatus": "...", "remarks": "...", "resolutionNotes": "..." }`. Role: `SOCIETY_ADMIN`.
- `POST /api/v1/complaints/{id}/reopen-requests`
  - Body: `{ "reason": "..." }`. Role: `RESIDENT`.
- `GET /api/v1/complaints/{id}/history`
  - Audit trail of status transitions.
- `GET /api/v1/complaints/{id}/comments`
  - List discussion comments.
- `POST /api/v1/complaints/{id}/comments`
  - Add discussion comment. Role: `RESIDENT` or `SOCIETY_ADMIN`.

### 3.7 Attachments
- `POST /api/v1/complaints/{id}/attachments`
  - Multipart upload for `IMAGE` (jpeg, png, webp <= 5MB) or `AUDIO` (mp3, wav, m4a, webm <= 10MB).
- `GET /api/v1/complaints/{id}/attachments/{attachmentId}`
  - Stream/download authorized attachment.
