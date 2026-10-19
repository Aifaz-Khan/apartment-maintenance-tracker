import React from 'react';
import { Building2, ShieldCheck } from 'lucide-react';

export const Header: React.FC = () => {
  return (
    <header style={{
      borderBottom: '1px solid var(--border-color)',
      backgroundColor: 'var(--bg-secondary)',
      padding: '1rem 0'
    }}>
      <div className="container" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
          <Building2 size={28} color="var(--accent-color)" />
          <div>
            <h1 style={{ fontSize: '1.25rem', fontWeight: 700, lineHeight: 1.2 }}>Apartment Maintenance Tracker</h1>
            <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Multi-tenant Housing Society Portal</p>
          </div>
        </div>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <span className="badge badge-info">
            <ShieldCheck size={14} /> Phase 1 Initialized
          </span>
        </div>
      </div>
    </header>
  );
};
