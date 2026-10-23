export type UserRole = 'SUPER_ADMIN' | 'SOCIETY_ADMIN' | 'RESIDENT';

export type ResidencyType = 'OWNER_OCCUPIED' | 'TENANT' | 'AUTHORIZED_OCCUPANT';

export type ResidencyStatus = 'PENDING' | 'ACTIVE' | 'ENDED' | 'REJECTED';

export type ComplaintPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';

export type ComplaintStatus =
  | 'SUBMITTED'
  | 'ACKNOWLEDGED'
  | 'IN_PROGRESS'
  | 'ON_HOLD'
  | 'RESOLVED'
  | 'CLOSED'
  | 'REOPENED'
  | 'CANCELLED';

export interface UserSummary {
  id: number;
  email: string;
  fullName: string;
  role: UserRole;
  phone?: string | null;
  societyId?: number | null;
  societyName?: string | null;
  apartmentUnitId?: number | null;
  unitNumber?: string | null;
  buildingName?: string | null;
}

export interface ApiErrorResponse {
  timestamp: string;
  status: number;
  code: string;
  message: string;
  path: string;
  fieldErrors?: Array<{ field: string; message: string }>;
}
