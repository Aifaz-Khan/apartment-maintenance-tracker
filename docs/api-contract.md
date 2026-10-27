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
All endpoints in this section require `SUPER_ADMIN` authority. Society admin account creation is controlled by the platform; callers cannot choose the assigned role.
- `POST /api/v1/super-admin/societies`
  - Creates a new housing society. Required fields: name, code, addressLine1, city, state, postalCode, contactEmail. Returns 201.
- `GET /api/v1/super-admin/societies?page=0&size=20`
  - Paginated list of societies (size 1–100).
- `GET /api/v1/super-admin/societies/{id}`
  - Detail view.
- `PATCH /api/v1/super-admin/societies/{id}`
  - Partial update of society details or `status` (`ACTIVE` / `INACTIVE`).
- `POST /api/v1/super-admin/societies/{id}/admins`
  - Creates and assigns a Society Admin account. Required fields: email, fullName, initialPassword (12–72 characters); optional phone. Email must be unused. The Super Admin shares the initial password securely; automated email invitations are deferred to Phase 10.

Society Admin scope is resolved through the authenticated user ID and the `society_admins` mapping. `SocietyAccessService` provides the server-side scope check used by subsequent society-owned APIs; client-supplied society IDs are not trusted as authorization context.

### 3.3 Society Admin: Buildings & Units
- `GET /api/v1/societies/{societyId}/units?page=0&size=20`
  - Paginated list of apartment units. Role: `SOCIETY_ADMIN`; society ID must match the authenticated admin's assignment.
- `POST /api/v1/societies/{societyId}/units`
  - Body: `{ "buildingName": "A", "unitNumber": "101", "floorNumber": 1, "unitLabel": "optional" }`. Creates a unit in the caller's assigned society. Duplicate building/unit pairs return 409.

### 3.4 Resident Onboarding & Residencies
- `POST /api/v1/auth/resident-registration-requests`
  - Public request body: `{ "societyId": 1, "apartmentUnitId": 2, "email": "...", "password": "12+ chars", "fullName": "...", "phone": "optional", "residencyType": "OWNER_OCCUPIED|TENANT|AUTHORIZED_OCCUPANT" }`. Creates an inactive resident account and PENDING residency; successful response is 201.
- `GET /api/v1/admin/resident-requests?page=0&size=20`
  - Lists pending requests only for the authenticated Society Admin's assigned society.
- `PATCH /api/v1/admin/resident-requests/{id}`
  - Body: `{ "action": "APPROVE" | "REJECT", "rejectionReason": "required when rejecting" }`. Approval activates the account and residency; a unit with an existing active residency cannot be approved. Role: `SOCIETY_ADMIN`; request's society is checked server-side.
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
  - Role: `RESIDENT`. Body: `{ "categoryId": 1, "title": "...", "description": "...", "locationArea": "...", "locationDetails": "optional", "priority": "LOW|MEDIUM|HIGH|URGENT" }`.
  - The server derives the resident, active unit, and society from the authenticated user and active residency; the body cannot set those IDs. Categories must be active and global or belong to the resident's society.
  - Returns 201 with the submitted complaint. If an active complaint already exists for that apartment: `409 ACTIVE_COMPLAINT_EXISTS`.
- `GET /api/v1/complaints`
  - Paginated list (`page`, `size`). Scoped: `RESIDENT` sees their active unit; `SOCIETY_ADMIN` sees their society.
- `GET /api/v1/complaints/{id}`
  - Get complaint detail.
- `PATCH /api/v1/complaints/{id}/status`
  - Body: `{ "newStatus": "ACKNOWLEDGED|IN_PROGRESS|ON_HOLD|RESOLVED|CLOSED|CANCELLED", "remarks": "optional", "resolutionNotes": "required when resolving" }`. Role: `SOCIETY_ADMIN`; society ownership is verified by the server. Invalid transitions return `409 INVALID_STATUS_TRANSITION`.
  - Allowed transitions: `SUBMITTED → ACKNOWLEDGED|CANCELLED`; `ACKNOWLEDGED → IN_PROGRESS|ON_HOLD|CANCELLED`; `IN_PROGRESS → ON_HOLD|RESOLVED|CANCELLED`; `ON_HOLD → IN_PROGRESS|RESOLVED|CANCELLED`; `RESOLVED → CLOSED`; `REOPENED → ACKNOWLEDGED|IN_PROGRESS|ON_HOLD|RESOLVED`. `CLOSED` and `CANCELLED` are terminal.
- `POST /api/v1/complaints/{id}/reopen-requests`
  - Body: `{ "reason": "..." }`. Role: `RESIDENT`.
- `GET /api/v1/complaints/{id}/history`
  - Audit trail of status transitions, visible only to a resident assigned to the complaint's unit or a Society Admin assigned to its society. New complaints receive an initial `SUBMITTED` history entry.
- `GET /api/v1/complaints/{id}/comments`
  - List discussion comments.
- `POST /api/v1/complaints/{id}/comments`
  - Add discussion comment. Role: `RESIDENT` or `SOCIETY_ADMIN`.

### 3.7 Attachments
- `POST /api/v1/complaints/{id}/attachments`
  - Multipart upload for `IMAGE` (jpeg, png, webp <= 5MB) or `AUDIO` (mp3, wav, m4a, webm <= 10MB).
- `GET /api/v1/complaints/{id}/attachments/{attachmentId}`
  - Stream/download authorized attachment.
