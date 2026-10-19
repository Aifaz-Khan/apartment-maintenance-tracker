-- ============================================================================
-- Apartment Maintenance Complaint Tracker - V1 Initial Schema
-- ============================================================================

-- 1. Societies (Tenant Boundary)
CREATE TABLE societies (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    contact_email VARCHAR(150) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 2. Users (Global Platform Accounts)
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(30) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 3. Society Admins (1-to-1 Mapping for MVP)
CREATE TABLE society_admins (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    society_id BIGINT NOT NULL REFERENCES societies(id) ON DELETE CASCADE,
    assigned_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 4. Apartment Units
CREATE TABLE apartment_units (
    id BIGSERIAL PRIMARY KEY,
    society_id BIGINT NOT NULL REFERENCES societies(id) ON DELETE CASCADE,
    building_name VARCHAR(100) NOT NULL,
    unit_number VARCHAR(50) NOT NULL,
    floor_number INT,
    unit_label VARCHAR(150),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_society_building_unit UNIQUE (society_id, building_name, unit_number)
);

-- 5. Unit Residencies (Occupancy History)
CREATE TABLE unit_residencies (
    id BIGSERIAL PRIMARY KEY,
    apartment_unit_id BIGINT NOT NULL REFERENCES apartment_units(id) ON DELETE CASCADE,
    resident_user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    residency_type VARCHAR(30) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    status VARCHAR(20) NOT NULL,
    verified_by BIGINT REFERENCES users(id),
    verified_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Invariant: Exactly one ACTIVE residency per apartment unit
CREATE UNIQUE INDEX uq_active_residency_per_unit
ON unit_residencies (apartment_unit_id)
WHERE status = 'ACTIVE';

-- 6. Residency Transfer Requests
CREATE TABLE residency_transfer_requests (
    id BIGSERIAL PRIMARY KEY,
    apartment_unit_id BIGINT NOT NULL REFERENCES apartment_units(id) ON DELETE CASCADE,
    current_residency_id BIGINT REFERENCES unit_residencies(id),
    proposed_user_id BIGINT NOT NULL REFERENCES users(id),
    residency_type VARCHAR(30) NOT NULL,
    requested_by BIGINT NOT NULL REFERENCES users(id),
    effective_date DATE NOT NULL,
    reason TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    reviewed_by BIGINT REFERENCES users(id),
    reviewed_at TIMESTAMPTZ,
    rejection_reason TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 7. Complaint Categories
CREATE TABLE complaint_categories (
    id BIGSERIAL PRIMARY KEY,
    society_id BIGINT REFERENCES societies(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 8. Complaints
CREATE TABLE complaints (
    id BIGSERIAL PRIMARY KEY,
    society_id BIGINT NOT NULL REFERENCES societies(id) ON DELETE CASCADE,
    apartment_unit_id BIGINT NOT NULL REFERENCES apartment_units(id) ON DELETE CASCADE,
    category_id BIGINT NOT NULL REFERENCES complaint_categories(id),
    created_by BIGINT NOT NULL REFERENCES users(id),
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    location_area VARCHAR(100) NOT NULL,
    location_details TEXT,
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    status VARCHAR(30) NOT NULL DEFAULT 'SUBMITTED',
    resolution_notes TEXT,
    resolved_at TIMESTAMPTZ,
    closed_at TIMESTAMPTZ,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Invariant: At most one ACTIVE complaint per apartment unit
-- Terminal statuses CLOSED and CANCELLED do not block new complaints.
CREATE UNIQUE INDEX uq_one_active_complaint_per_apartment
ON complaints (apartment_unit_id)
WHERE status IN (
    'SUBMITTED', 'ACKNOWLEDGED', 'IN_PROGRESS', 'ON_HOLD', 'RESOLVED', 'REOPENED'
);

-- 9. Complaint Attachments (Private Media Metadata)
CREATE TABLE complaint_attachments (
    id BIGSERIAL PRIMARY KEY,
    complaint_id BIGINT NOT NULL REFERENCES complaints(id) ON DELETE CASCADE,
    uploaded_by BIGINT NOT NULL REFERENCES users(id),
    storage_key VARCHAR(255) NOT NULL UNIQUE,
    original_filename VARCHAR(255) NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    size_bytes BIGINT NOT NULL,
    media_kind VARCHAR(20) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 10. Complaint Comments
CREATE TABLE complaint_comments (
    id BIGSERIAL PRIMARY KEY,
    complaint_id BIGINT NOT NULL REFERENCES complaints(id) ON DELETE CASCADE,
    author_id BIGINT NOT NULL REFERENCES users(id),
    body TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 11. Complaint Status History (Audit Trail)
CREATE TABLE complaint_status_history (
    id BIGSERIAL PRIMARY KEY,
    complaint_id BIGINT NOT NULL REFERENCES complaints(id) ON DELETE CASCADE,
    changed_by BIGINT NOT NULL REFERENCES users(id),
    previous_status VARCHAR(30),
    new_status VARCHAR(30) NOT NULL,
    remarks TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Indexes for frequent queries
CREATE INDEX idx_complaints_society_status ON complaints(society_id, status);
CREATE INDEX idx_complaints_unit ON complaints(apartment_unit_id);
CREATE INDEX idx_residencies_user ON unit_residencies(resident_user_id);
CREATE INDEX idx_units_society ON apartment_units(society_id);

-- Seed global baseline complaint categories (society_id = NULL)
INSERT INTO complaint_categories (society_id, name, description, active) VALUES
(NULL, 'Plumbing', 'Water supply, pipeline leakages, drainage, bathroom fixtures', TRUE),
(NULL, 'Electrical', 'Wiring, switchboards, power outages, common area lighting', TRUE),
(NULL, 'Carpentry', 'Doors, window frames, locks, cabinet hinges', TRUE),
(NULL, 'Civil & Structural', 'Wall seepage, plastering, tile breakage, dampness', TRUE),
(NULL, 'Elevator / Lift', 'Lift breakdown, door jamming, unusual noise, indicator failure', TRUE),
(NULL, 'Security & Intercom', 'Main gate intercom, security guard desk, RFID access tags', TRUE),
(NULL, 'Housekeeping & Cleanliness', 'Garbage collection, corridor cleaning, pest issues', TRUE),
(NULL, 'Other', 'General maintenance requests not covered under other categories', TRUE);
