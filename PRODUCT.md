# Product

<!-- impeccable:product-schema 1 -->

## Platform

web

## Users

- **Primary: Residents** who need to request maintenance for their apartment and follow the status of their requests.
- **Society Admins** who manage units and residents and handle requests for their assigned society.
- **Super Admins** who manage societies and Society Admin assignments across the platform.

## Product Purpose

The Apartment Maintenance Complaint Tracker lets apartment residents report maintenance issues and lets society teams review, resolve, and track those requests.

## Positioning

The product enforces society-level access boundaries and allows only one active complaint per apartment unit at a time. No broader market positioning has been established.

## Operating Context

- Residents may be required to request access and wait for Society Admin approval before using resident features.
- Society Admins work only within the society assigned to their account.
- Residents submit requests, attach supporting media, review status history, and can request a transfer to another unit in their society.
- Society Admins manage units and resident access, review transfer requests, and update complaint status.
- Super Admins create societies and appoint Society Admins.

## Capabilities and Constraints

- Existing authentication, APIs, role permissions, validation, business rules, and workflows are product behavior and must remain unchanged during this UI-only redesign.
- A unit may have only one active complaint; a new complaint can be created after the current complaint is closed or cancelled.
- Society Admins must not access another society's private information.
- Complaint attachments are private and require authenticated, authorized access.
- Email notifications are optional and depend on SMTP configuration.
- Comments, reports, and other capabilities identified as future phases must not be represented as implemented unless present in the application.

## Brand Commitments

- Product name: **Apartment Maintenance Complaint Tracker** (the UI may use the shorter display name **Maintenance Tracker**).
- The user selected a warm editorial direction using clay and forest tones for this redesign.

## Evidence on Hand

- Implemented workflows and existing text are in the repository's frontend and backend source.
- No approved logo, real customer data, testimonials, or production usage claims were supplied. Do not fabricate these.

## Product Principles

- Make it clear what a resident should do next.
- Keep Society Admin work scoped and legible across requests, residents, and units.
- Show complaint status and history in terms residents can understand.
- Preserve tenant boundaries and existing business rules.

## Accessibility & Inclusion

The user requires accessible, responsive interfaces with clear hierarchy, readable contrast, keyboard access, and usable forms for residents who may not be technically experienced.
