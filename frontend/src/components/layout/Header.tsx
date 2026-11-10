import React from 'react';
import { useAuth } from '../../features/auth/useAuth';

export const Header: React.FC = () => {
  const { user, logout, loading } = useAuth();
  return <header className="app-header">
    <div className="app-header-inner">
      <a className="brand" href="#workspace" aria-label="Maintenance Tracker home"><span className="brand-mark" aria-hidden="true"><svg viewBox="0 0 36 36" fill="none"><path d="M5.5 16.2 18 6.5l12.5 9.7v13.3H5.5V16.2Z"/><path d="M12 29.5v-8h5v8m3-13h4m-4 4h4"/><path d="M2.5 33.5h11l3-2.7 3.5 2.7h13"/></svg></span><span className="brand-copy"><strong>Maintenance Tracker</strong><small>APARTMENT CARE</small></span></a>
      <div className="header-session">
        {loading ? <span className="session-muted">Checking session…</span> : user ? <>
          <span className="session-context"><span className="session-dot"/><span>{user.fullName}</span><span className="session-divider"/><span className="badge badge-role">{user.role.replace('_',' ')}</span></span>
          <button type="button" className="auth-button auth-button-quiet" onClick={logout}>Sign out</button>
        </> : <span className="badge badge-neutral">Welcome</span>}
      </div>
    </div>
  </header>;
};
