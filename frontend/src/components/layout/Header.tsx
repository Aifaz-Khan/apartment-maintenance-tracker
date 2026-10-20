import React from 'react';
import { Building2, Database, Server, FileCode, Check } from 'lucide-react';

export const Header: React.FC = () => {
  return (
    <header style={{
      borderBottom: '1px solid var(--border-subtle)',
      backgroundColor: 'var(--bg-surface)',
      padding: '0.625rem 1.5rem',
    }}>
      <div style={{
        maxWidth: '1360px',
        margin: '0 auto',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        gap: '1rem',
      }}>
        {/* Left: Branding & Breadcrumb */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
          <div style={{
            width: '28px',
            height: '28px',
            borderRadius: '4px',
            backgroundColor: 'var(--bg-subtle)',
            border: '1px solid var(--border-medium)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
          }}>
            <Building2 size={16} color="var(--accent-color)" />
          </div>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <span style={{ fontWeight: 600, fontSize: '0.875rem' }}>Maintenance Tracker</span>
              <span style={{ color: 'var(--border-strong)', fontSize: '0.75rem' }}>/</span>
              <span style={{ color: 'var(--text-secondary)', fontSize: '0.8125rem' }}>Platform Operations</span>
            </div>
            <p style={{ fontSize: '0.6875rem', color: 'var(--text-tertiary)', margin: 0 }}>
              Multi-tenant Monolith Architecture
            </p>
          </div>
        </div>

        {/* Right: Operational Status Pill & Links */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.875rem' }}>
          <div style={{
            display: 'flex',
            alignItems: 'center',
            gap: '0.625rem',
            padding: '0.25rem 0.625rem',
            borderRadius: '3px',
            backgroundColor: 'var(--bg-app)',
            border: '1px solid var(--border-subtle)',
            fontSize: '0.75rem',
          }}>
            <span style={{ display: 'inline-flex', alignItems: 'center', gap: '0.3125rem', color: 'var(--text-secondary)' }}>
              <Database size={12} color="var(--text-tertiary)" />
              <span className="tnum">PG 17</span>
            </span>
            <span style={{ width: '1px', height: '10px', backgroundColor: 'var(--border-medium)' }} />
            <span style={{ display: 'inline-flex', alignItems: 'center', gap: '0.3125rem', color: 'var(--text-secondary)' }}>
              <Server size={12} color="var(--text-tertiary)" />
              <span className="tnum">Boot 3.5.16</span>
            </span>
            <span style={{ width: '1px', height: '10px', backgroundColor: 'var(--border-medium)' }} />
            <span style={{ display: 'inline-flex', alignItems: 'center', gap: '0.25rem', color: 'var(--status-success-text)' }}>
              <Check size={12} />
              <span>Healthy</span>
            </span>
          </div>

          <a
            href="http://localhost:8080/swagger-ui.html"
            target="_blank"
            rel="noopener noreferrer"
            style={{
              display: 'inline-flex',
              alignItems: 'center',
              gap: '0.375rem',
              padding: '0.25rem 0.5rem',
              borderRadius: '3px',
              border: '1px solid var(--border-medium)',
              backgroundColor: 'var(--bg-subtle)',
              fontSize: '0.75rem',
              color: 'var(--text-secondary)',
            }}
          >
            <FileCode size={13} />
            <span>OpenAPI Docs</span>
          </a>
        </div>
      </div>
    </header>
  );
};
