# ADR 0001: 1-to-1 Society Admin Assignment for MVP

## Status
Accepted

## Context
A housing society is the core multi-tenant boundary. We need to decide whether a `SOCIETY_ADMIN` user can administer multiple societies simultaneously or exactly one society for the MVP.

## Decision
For the initial release (MVP):
1. A `SOCIETY_ADMIN` is assigned to **exactly one** housing society via the `society_admins` mapping table (with a unique constraint on `user_id`).
2. Server-side authorization queries resolve the admin's `society_id` directly from this mapping and inject it into all query specifications and mutation checks.
3. Super Admins remain global and assign admins to a specific society during invitation/appointment.

## Consequences
- **Positive**: Eliminates multi-tenant context-switching complexity in both the backend and frontend.
- **Positive**: Strict, simple database queries and bulletproof tenant isolation.
- **Future Adaptation**: If multi-society administration is required in the future, the unique constraint on `user_id` can be relaxed and an active society session context switch can be introduced without redesigning the underlying data model.
