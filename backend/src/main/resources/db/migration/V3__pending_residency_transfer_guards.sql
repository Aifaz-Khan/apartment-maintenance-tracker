-- A resident and a target unit may each have only one transfer awaiting review.
CREATE UNIQUE INDEX uq_pending_transfer_per_resident
ON residency_transfer_requests (proposed_user_id)
WHERE status = 'PENDING';

CREATE UNIQUE INDEX uq_pending_transfer_per_unit
ON residency_transfer_requests (apartment_unit_id)
WHERE status = 'PENDING';
