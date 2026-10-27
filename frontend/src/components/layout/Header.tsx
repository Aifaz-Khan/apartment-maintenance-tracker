import React from 'react';
import { Building2 } from 'lucide-react';
import { useAuth } from '../../features/auth/useAuth';

export const Header: React.FC = () => {
  const { user, logout, loading } = useAuth();
  return <header className="app-header">
    <div className="app-header-inner">
      <div className="brand"><span className="brand-mark"><Building2 size={17}/></span><span>Maintenance Tracker</span></div>
      <div className="header-session">
        {loading ? <span className="session-muted">Checking session…</span> : user ? <>
          <span className="badge badge-success">{user.role.replace('_',' ')}</span>
          <span className="session-email">{user.email}</span>
          <button type="button" className="auth-button auth-button-quiet" onClick={logout}>Sign out</button>
        </> : <span className="badge badge-neutral">Not signed in</span>}
      </div>
    </div>
  </header>;
};
