import React from 'react';
import { Server, Layers, Database, CheckCircle2 } from 'lucide-react';

export const DashboardView: React.FC = () => {
  return (
    <div className="container" style={{ marginTop: '2rem' }}>
      <div className="card" style={{ marginBottom: '1.5rem' }}>
        <h2 style={{ fontSize: '1.5rem', marginBottom: '0.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <CheckCircle2 color="var(--success-color)" size={24} /> System Scaffolding Complete
        </h2>
        <p style={{ color: 'var(--text-muted)' }}>
          Phase 0 architecture specifications and Phase 1 full-stack baseline have been successfully scaffolded and verified.
        </p>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '1rem' }}>
        <div className="card">
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1rem' }}>
            <Server color="var(--accent-color)" size={22} />
            <h3 style={{ fontSize: '1.1rem' }}>Backend Baseline</h3>
          </div>
          <p style={{ fontSize: '0.875rem', color: 'var(--text-muted)', marginBottom: '0.75rem' }}>
            Spring Boot 3.5.16 on Java 21 LTS with Maven Wrapper, Spring Data JPA, and Flyway.
          </p>
          <span className="badge badge-success">Verified Healthy</span>
        </div>

        <div className="card">
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1rem' }}>
            <Database color="var(--accent-color)" size={22} />
            <h3 style={{ fontSize: '1.1rem' }}>Database Architecture</h3>
          </div>
          <p style={{ fontSize: '0.875rem', color: 'var(--text-muted)', marginBottom: '0.75rem' }}>
            PostgreSQL 16/17 connected with partial unique indexes for single active residency and complaint invariants.
          </p>
          <span className="badge badge-success">Local DB Ready</span>
        </div>

        <div className="card">
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1rem' }}>
            <Layers color="var(--accent-color)" size={22} />
            <h3 style={{ fontSize: '1.1rem' }}>Frontend Client</h3>
          </div>
          <p style={{ fontSize: '0.875rem', color: 'var(--text-muted)', marginBottom: '0.75rem' }}>
            React 19 + TypeScript SPA bundled with Vite, TanStack Query, and feature-based structure.
          </p>
          <span className="badge badge-success">Strict Mode Enabled</span>
        </div>
      </div>
    </div>
  );
};
