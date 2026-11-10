import React from 'react';
import { useAuth } from '../auth/useAuth';
import { CategoryList } from '../categories/CategoryList';
import { AuthPanel } from '../auth/AuthPanel';
import { SocietyManagementPanel } from '../societies/SocietyManagementPanel';
import { ResidentOperationsPanel } from '../units/ResidentOperationsPanel';
import { ResidentRegistrationPanel } from '../units/ResidentRegistrationPanel';
import { ResidentComplaintPanel } from '../complaints/ResidentComplaintPanel';
import { ResidentTransferPanel } from '../units/ResidentTransferPanel';
import { ComplaintManagementPanel } from '../complaints/ComplaintManagementPanel';

type NavigationItem={href:string;label:string};

export const DashboardView: React.FC = () => {
  const {user}=useAuth();
  const navigation:NavigationItem[]=user?.role==='RESIDENT'?
    [{href:'#report-issue',label:'Report an issue'},{href:'#my-requests',label:'My requests'},{href:'#unit-transfer',label:'Unit transfer'}]:
    user?.role==='SOCIETY_ADMIN'?
    [{href:'#resident-operations',label:'Units & residents'},{href:'#maintenance-requests',label:'Requests'},{href:'#maintenance-categories',label:'Categories'}]:
    user?.role==='SUPER_ADMIN'?
    [{href:'#society-management',label:'Societies'},{href:'#maintenance-categories',label:'Categories'}]:
    [{href:'#sign-in',label:'Sign in'},{href:'#resident-access',label:'Request access'},{href:'#maintenance-categories',label:'Categories'}];
  const title=user?.role==='RESIDENT'?'Your maintenance workspace':user?.role==='SOCIETY_ADMIN'?(user.societyName??'Society workspace'):user?.role==='SUPER_ADMIN'?'Society management':'Maintenance, made easier';
  const description=user?.role==='RESIDENT'?'Report an issue, follow its progress, and keep your home information up to date.':user?.role==='SOCIETY_ADMIN'?'Manage resident access and keep maintenance requests moving for your society.':user?.role==='SUPER_ADMIN'?'Keep society information and administrator access in order.':'A clear place to report apartment issues and follow what happens next.';
  return <div className="dashboard-frame" id="workspace">
    <aside className="workspace-rail" aria-label="Workspace navigation">
      <div className="rail-heading"><span className="rail-house" aria-hidden="true"><svg viewBox="0 0 28 28" fill="none"><path d="m4 12 10-8 10 8v11H4V12Z"/><path d="M10 23v-7h4v7m3-10h3m-3 3h3"/></svg></span><div><strong>{user?.societyName??'My workspace'}</strong><small>{user?.role?.replace('_',' ')??'GET STARTED'}</small></div></div>
      <p className="rail-label">WORKSPACE</p>
      <nav className="workspace-nav">{navigation.map(item=><a className="workspace-nav-link" href={item.href} key={item.href}><span className="nav-waypoint" aria-hidden="true"/>{item.label}</a>)}</nav>
      <div className="rail-note"><span className="rail-note-mark" aria-hidden="true"/><p>Good care starts with a clear next step.</p></div>
    </aside>
    <div className="workspace-main">
      <div className="workspace-intro"><div><h1>{title}</h1><p>{description}</p></div>{user?.apartmentUnitId&&<span className="home-location"><span className="location-pin" aria-hidden="true"/>{user.buildingName} · {user.unitNumber}</span>}</div>
      <div className="workspace-panels">
        <AuthPanel />
        <SocietyManagementPanel />
        <ResidentOperationsPanel />
        <ResidentComplaintPanel />
        <ComplaintManagementPanel />
        <ResidentRegistrationPanel />
        <ResidentTransferPanel />
        <CategoryList />
      </div>
      <footer className="workspace-footer"><span>Maintenance Tracker</span><span>For the places we call home</span></footer>
    </div>
  </div>;
};
