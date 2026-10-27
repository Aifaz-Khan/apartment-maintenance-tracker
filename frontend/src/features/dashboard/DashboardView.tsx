import React from 'react';
import { CategoryList } from '../categories/CategoryList';
import { AuthPanel } from '../auth/AuthPanel';
import { SocietyManagementPanel } from '../societies/SocietyManagementPanel';
import { ResidentOperationsPanel } from '../units/ResidentOperationsPanel';
import { ResidentRegistrationPanel } from '../units/ResidentRegistrationPanel';

export const DashboardView: React.FC = () => <div className="main-content">
  <AuthPanel />
  <SocietyManagementPanel />
  <ResidentOperationsPanel />
  <ResidentRegistrationPanel />
  <CategoryList />
</div>;
