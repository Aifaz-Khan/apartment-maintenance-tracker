import React from 'react';
import { CategoryList } from '../categories/CategoryList';
import { Check, ShieldAlert, Cpu, Database, Code, BookOpen } from 'lucide-react';

export const DashboardView: React.FC = () => {
  return (
    <div className="main-content">
      {/* Dense System Metric Strip (Borders & Spacing, No Cards) */}
      <div style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))',
        border: '1px solid var(--border-subtle)',
        borderRadius: '4px',
        backgroundColor: 'var(--bg-surface)',
        marginBottom: '1rem',
      }}>
        {/* Metric 1 */}
        <div style={{ padding: '0.75rem 1rem', borderRight: '1px solid var(--border-subtle)' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.375rem', marginBottom: '0.25rem' }}>
            <Cpu size={13} color="var(--text-tertiary)" />
            <span style={{ fontSize: '0.6875rem', textTransform: 'uppercase', color: 'var(--text-tertiary)', fontWeight: 600, letterSpacing: '0.04em' }}>
              Backend Runtime
            </span>
          </div>
          <div style={{ display: 'flex', alignItems: 'baseline', gap: '0.5rem' }}>
            <span className="tnum" style={{ fontSize: '0.9375rem', fontWeight: 600 }}>Java 21 LTS</span>
            <span style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>Spring Boot 3.5.16</span>
          </div>
        </div>

        {/* Metric 2 */}
        <div style={{ padding: '0.75rem 1rem', borderRight: '1px solid var(--border-subtle)' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.375rem', marginBottom: '0.25rem' }}>
            <Database size={13} color="var(--text-tertiary)" />
            <span style={{ fontSize: '0.6875rem', textTransform: 'uppercase', color: 'var(--text-tertiary)', fontWeight: 600, letterSpacing: '0.04em' }}>
              Database Migration
            </span>
          </div>
          <div style={{ display: 'flex', alignItems: 'baseline', gap: '0.5rem' }}>
            <span className="tnum" style={{ fontSize: '0.9375rem', fontWeight: 600 }}>Flyway V1</span>
            <span className="tnum" style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>11 Tables</span>
          </div>
        </div>

        {/* Metric 3 */}
        <div style={{ padding: '0.75rem 1rem', borderRight: '1px solid var(--border-subtle)' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.375rem', marginBottom: '0.25rem' }}>
            <ShieldAlert size={13} color="var(--text-tertiary)" />
            <span style={{ fontSize: '0.6875rem', textTransform: 'uppercase', color: 'var(--text-tertiary)', fontWeight: 600, letterSpacing: '0.04em' }}>
              Enforced Invariants
            </span>
          </div>
          <div style={{ display: 'flex', alignItems: 'baseline', gap: '0.5rem' }}>
            <span className="tnum" style={{ fontSize: '0.9375rem', fontWeight: 600 }}>2 Partial Indexes</span>
            <span style={{ fontSize: '0.75rem', color: 'var(--status-success-text)' }}>Enforced</span>
          </div>
        </div>

        {/* Metric 4 */}
        <div style={{ padding: '0.75rem 1rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.375rem', marginBottom: '0.25rem' }}>
            <Check size={13} color="var(--status-success-text)" />
            <span style={{ fontSize: '0.6875rem', textTransform: 'uppercase', color: 'var(--text-tertiary)', fontWeight: 600, letterSpacing: '0.04em' }}>
              Automated Verification
            </span>
          </div>
          <div style={{ display: 'flex', alignItems: 'baseline', gap: '0.5rem' }}>
            <span className="tnum" style={{ fontSize: '0.9375rem', fontWeight: 600, color: 'var(--status-success-text)' }}>4 / 4 Passing</span>
            <span style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>Build Green</span>
          </div>
        </div>
      </div>

      {/* Main Grid: Data Table + Architectural Reference Panel */}
      <div style={{
        display: 'grid',
        gridTemplateColumns: 'minmax(0, 1fr) 320px',
        gap: '1rem',
        alignItems: 'start',
      }}>
        {/* Left Column: Category Slice Data Table */}
        <div style={{ minWidth: 0 }}>
          <CategoryList />
        </div>

        {/* Right Column: Operational Architecture Specs Panel */}
        <div className="section-panel" style={{ marginTop: '1rem' }}>
          <div className="panel-header">
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <BookOpen size={14} color="var(--accent-color)" />
              <h3>Phase Execution State</h3>
            </div>
            <span className="badge badge-info tnum">Phases 0–3</span>
          </div>

          <div style={{ padding: '0.875rem 1rem' }}>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
              {/* Item 1 */}
              <div style={{ display: 'flex', gap: '0.625rem', alignItems: 'flex-start' }}>
                <div style={{ marginTop: '2px', width: '14px', height: '14px', borderRadius: '50%', backgroundColor: 'var(--status-success-bg)', border: '1px solid var(--status-success-border)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                  <Check size={10} color="var(--status-success-text)" />
                </div>
                <div>
                  <div style={{ fontWeight: 500, fontSize: '0.8125rem' }}>Phase 0: Specifications</div>
                  <p style={{ color: 'var(--text-tertiary)', fontSize: '0.75rem' }}>Architecture, ER diagrams, REST contracts in <code>docs/</code></p>
                </div>
              </div>

              {/* Item 2 */}
              <div style={{ display: 'flex', gap: '0.625rem', alignItems: 'flex-start' }}>
                <div style={{ marginTop: '2px', width: '14px', height: '14px', borderRadius: '50%', backgroundColor: 'var(--status-success-bg)', border: '1px solid var(--status-success-border)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                  <Check size={10} color="var(--status-success-text)" />
                </div>
                <div>
                  <div style={{ fontWeight: 500, fontSize: '0.8125rem' }}>Phase 1: Foundation Scaffolding</div>
                  <p style={{ color: 'var(--text-tertiary)', fontSize: '0.75rem' }}>Spring Boot 3.5.16 + React 19 + TypeScript</p>
                </div>
              </div>

              {/* Item 3 */}
              <div style={{ display: 'flex', gap: '0.625rem', alignItems: 'flex-start' }}>
                <div style={{ marginTop: '2px', width: '14px', height: '14px', borderRadius: '50%', backgroundColor: 'var(--status-success-bg)', border: '1px solid var(--status-success-border)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                  <Check size={10} color="var(--status-success-text)" />
                </div>
                <div>
                  <div style={{ fontWeight: 500, fontSize: '0.8125rem' }}>Phase 2: PostgreSQL Persistence</div>
                  <p style={{ color: 'var(--text-tertiary)', fontSize: '0.75rem' }}>Flyway V1, 11 JPA entities, 2 partial unique indexes</p>
                </div>
              </div>

              {/* Item 4 */}
              <div style={{ display: 'flex', gap: '0.625rem', alignItems: 'flex-start' }}>
                <div style={{ marginTop: '2px', width: '14px', height: '14px', borderRadius: '50%', backgroundColor: 'var(--status-success-bg)', border: '1px solid var(--status-success-border)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                  <Check size={10} color="var(--status-success-text)" />
                </div>
                <div>
                  <div style={{ fontWeight: 500, fontSize: '0.8125rem' }}>Phase 3: Vertical Slice</div>
                  <p style={{ color: 'var(--text-tertiary)', fontSize: '0.75rem' }}><code>GET /api/v1/categories</code> + OpenAPI Swagger UI</p>
                </div>
              </div>
            </div>

            {/* Invariant rules breakdown */}
            <div style={{ marginTop: '1rem', paddingTop: '0.875rem', borderTop: '1px solid var(--border-subtle)' }}>
              <div style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--text-secondary)', marginBottom: '0.5rem', textTransform: 'uppercase', letterSpacing: '0.04em' }}>
                Core Invariants
              </div>
              <ul style={{ listStyle: 'none', display: 'flex', flexDirection: 'column', gap: '0.375rem', fontSize: '0.75rem', color: 'var(--text-secondary)' }}>
                <li style={{ display: 'flex', alignItems: 'center', gap: '0.375rem' }}>
                  <span style={{ color: 'var(--status-success-text)' }}>•</span>
                  <span>1 Active residency per unit</span>
                </li>
                <li style={{ display: 'flex', alignItems: 'center', gap: '0.375rem' }}>
                  <span style={{ color: 'var(--status-success-text)' }}>•</span>
                  <span>Max 1 active complaint per unit</span>
                </li>
                <li style={{ display: 'flex', alignItems: 'center', gap: '0.375rem' }}>
                  <span style={{ color: 'var(--status-success-text)' }}>•</span>
                  <span>Server-side tenant isolation</span>
                </li>
              </ul>
            </div>

            {/* Quick Link */}
            <div style={{ marginTop: '0.875rem', paddingTop: '0.75rem', borderTop: '1px solid var(--border-subtle)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <span style={{ fontSize: '0.6875rem', color: 'var(--text-tertiary)' }}>Context Guide</span>
              <a href="https://github.com/Aifaz-Khan/apartment-maintenance-tracker/blob/main/docs/AGENT_HANDOFF.md" target="_blank" rel="noreferrer" style={{ fontSize: '0.75rem', display: 'inline-flex', alignItems: 'center', gap: '0.25rem' }}>
                <Code size={12} />
                <span>AGENT_HANDOFF.md</span>
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
