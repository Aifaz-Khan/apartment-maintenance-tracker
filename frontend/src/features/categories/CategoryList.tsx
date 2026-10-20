import React, { useState, useMemo, useRef, useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import { fetchCategories, ComplaintCategoryItem } from './categoryService';
import { Search, X, RefreshCw } from 'lucide-react';

export const CategoryList: React.FC = () => {
  const [filterQuery, setFilterQuery] = useState('');
  const [scopeFilter, setScopeFilter] = useState<'ALL' | 'GLOBAL' | 'SOCIETY'>('ALL');
  const searchInputRef = useRef<HTMLInputElement>(null);

  const { data: categories, isLoading, isError, error, refetch, isFetching } = useQuery({
    queryKey: ['categories'],
    queryFn: () => fetchCategories(),
  });

  // Keyboard shortcut '/' to instantly focus search
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === '/' && document.activeElement !== searchInputRef.current) {
        e.preventDefault();
        searchInputRef.current?.focus();
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, []);

  const filteredCategories = useMemo(() => {
    if (!categories) return [];
    return categories.filter((cat: ComplaintCategoryItem) => {
      const matchesScope =
        scopeFilter === 'ALL' ||
        (scopeFilter === 'GLOBAL' && cat.isGlobal) ||
        (scopeFilter === 'SOCIETY' && !cat.isGlobal);

      const query = filterQuery.trim().toLowerCase();
      const matchesQuery =
        query === '' ||
        cat.name.toLowerCase().includes(query) ||
        (cat.description && cat.description.toLowerCase().includes(query)) ||
        cat.id.toString().includes(query);

      return matchesScope && matchesQuery;
    });
  }, [categories, filterQuery, scopeFilter]);

  const globalCount = useMemo(() => categories?.filter(c => c.isGlobal).length ?? 0, [categories]);
  const societyCount = useMemo(() => categories?.filter(c => !c.isGlobal).length ?? 0, [categories]);
  const totalCount = categories?.length ?? 0;

  return (
    <div className="section-panel" style={{ marginTop: '1rem' }}>
      {/* Panel Action Bar */}
      <div className="panel-header">
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
          <h2>Maintenance Categories</h2>
          <span className="code-inline tnum">
            {filteredCategories.length} of {totalCount} records
          </span>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <button
            onClick={() => refetch()}
            disabled={isFetching}
            className="btn-tab"
            title="Refresh categories"
            style={{ display: 'inline-flex', alignItems: 'center', gap: '0.25rem' }}
          >
            <RefreshCw size={12} className={isFetching ? 'spin' : ''} />
            <span>Sync</span>
          </button>
        </div>
      </div>

      {/* Filter & Search Bar */}
      <div style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        padding: '0.5rem 1rem',
        borderBottom: '1px solid var(--border-subtle)',
        backgroundColor: 'var(--bg-app)',
        gap: '0.75rem',
        flexWrap: 'wrap',
      }}>
        {/* Scope Tabs */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
          <button
            className={`btn-tab ${scopeFilter === 'ALL' ? 'active' : ''}`}
            onClick={() => setScopeFilter('ALL')}
          >
            All <span className="tnum">({totalCount})</span>
          </button>
          <button
            className={`btn-tab ${scopeFilter === 'GLOBAL' ? 'active' : ''}`}
            onClick={() => setScopeFilter('GLOBAL')}
          >
            Global <span className="tnum">({globalCount})</span>
          </button>
          <button
            className={`btn-tab ${scopeFilter === 'SOCIETY' ? 'active' : ''}`}
            onClick={() => setScopeFilter('SOCIETY')}
          >
            Society Custom <span className="tnum">({societyCount})</span>
          </button>
        </div>

        {/* Search Field */}
        <div style={{ position: 'relative', width: '260px' }}>
          <Search size={13} style={{ position: 'absolute', left: '0.5rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-tertiary)' }} />
          <input
            ref={searchInputRef}
            type="text"
            className="filter-input"
            value={filterQuery}
            onChange={(e) => setFilterQuery(e.target.value)}
            placeholder="Filter categories... (Press /)"
            style={{ width: '100%', paddingLeft: '1.75rem', paddingRight: filterQuery ? '1.75rem' : '0.5rem' }}
          />
          {filterQuery && (
            <button
              onClick={() => setFilterQuery('')}
              style={{
                position: 'absolute',
                right: '0.375rem',
                top: '50%',
                transform: 'translateY(-50%)',
                background: 'none',
                border: 'none',
                color: 'var(--text-tertiary)',
                cursor: 'pointer',
                display: 'flex',
                alignItems: 'center',
              }}
            >
              <X size={13} />
            </button>
          )}
        </div>
      </div>

      {/* Main Data Table */}
      <div className="data-table-container" style={{ border: 'none', borderRadius: 0 }}>
        <table className="data-table">
          <thead>
            <tr>
              <th style={{ width: '60px' }}>ID</th>
              <th style={{ width: '220px' }}>Category Name</th>
              <th>Description</th>
              <th style={{ width: '120px' }}>Scope</th>
              <th style={{ width: '100px', textAlign: 'right' }}>Status</th>
            </tr>
          </thead>
          <tbody>
            {isLoading && (
              <>
                {[1, 2, 3, 4, 5].map((i) => (
                  <tr key={i}>
                    <td className="tnum" style={{ color: 'var(--text-tertiary)' }}>---</td>
                    <td style={{ color: 'var(--text-tertiary)' }}>Loading catalog item...</td>
                    <td style={{ color: 'var(--text-tertiary)' }}>Fetching metadata from server...</td>
                    <td><span className="badge badge-neutral">---</span></td>
                    <td style={{ textAlign: 'right' }}><span className="badge badge-neutral">...</span></td>
                  </tr>
                ))}
              </>
            )}

            {isError && (
              <tr>
                <td colSpan={5} style={{ padding: '2rem 1rem', textAlign: 'center', color: 'var(--status-warning-text)' }}>
                  Failed to load categories: {error instanceof Error ? error.message : 'Unknown network error'}.
                  <button onClick={() => refetch()} className="btn-tab" style={{ marginLeft: '0.5rem', textDecoration: 'underline' }}>
                    Retry
                  </button>
                </td>
              </tr>
            )}

            {!isLoading && !isError && filteredCategories.length === 0 && (
              <tr>
                <td colSpan={5} style={{ padding: '2.5rem 1rem', textAlign: 'center', color: 'var(--text-tertiary)' }}>
                  No categories match current filters.
                  {filterQuery && (
                    <button
                      onClick={() => setFilterQuery('')}
                      className="btn-tab"
                      style={{ marginLeft: '0.5rem', color: 'var(--accent-color)' }}
                    >
                      Clear search
                    </button>
                  )}
                </td>
              </tr>
            )}

            {!isLoading && !isError && filteredCategories.map((cat) => (
              <tr key={cat.id}>
                <td className="tnum" style={{ color: 'var(--text-tertiary)' }}>
                  #{String(cat.id).padStart(3, '0')}
                </td>
                <td style={{ fontWeight: 500 }}>
                  {cat.name}
                </td>
                <td style={{ color: 'var(--text-secondary)' }}>
                  {cat.description || '—'}
                </td>
                <td>
                  {cat.isGlobal ? (
                    <span className="badge badge-neutral">Global Default</span>
                  ) : (
                    <span className="badge badge-info tnum">Society #{cat.societyId}</span>
                  )}
                </td>
                <td style={{ textAlign: 'right' }}>
                  <span className={cat.active ? 'badge badge-success' : 'badge badge-neutral'}>
                    {cat.active ? 'Active' : 'Archived'}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};
