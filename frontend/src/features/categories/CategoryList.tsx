import React from 'react';
import { useQuery } from '@tanstack/react-query';
import { fetchCategories } from './categoryService';
import { Tag, Globe, Building } from 'lucide-react';

export const CategoryList: React.FC = () => {
  const { data: categories, isLoading, isError, error } = useQuery({
    queryKey: ['categories'],
    queryFn: () => fetchCategories(),
  });

  return (
    <div className="card" style={{ marginTop: '1.5rem' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '1rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <Tag size={20} color="var(--accent-color)" />
          <h3 style={{ fontSize: '1.15rem' }}>Available Maintenance Categories</h3>
        </div>
        <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
          API: <code>GET /api/v1/categories</code>
        </span>
      </div>

      {isLoading && (
        <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
          Loading categories from backend...
        </p>
      )}

      {isError && (
        <div style={{ padding: '0.75rem', borderRadius: '0.5rem', backgroundColor: 'rgba(239, 68, 68, 0.1)', color: 'var(--danger-color)', fontSize: '0.9rem' }}>
          Failed to load categories: {error instanceof Error ? error.message : 'Unknown error'}
        </div>
      )}

      {!isLoading && !isError && categories && categories.length === 0 && (
        <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
          No active complaint categories found.
        </p>
      )}

      {!isLoading && !isError && categories && categories.length > 0 && (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(260px, 1fr))', gap: '0.75rem' }}>
          {categories.map((cat) => (
            <div
              key={cat.id}
              style={{
                padding: '0.75rem 1rem',
                borderRadius: '0.5rem',
                border: '1px solid var(--border-color)',
                backgroundColor: 'rgba(255, 255, 255, 0.02)',
              }}
            >
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '0.25rem' }}>
                <strong style={{ fontSize: '0.95rem' }}>{cat.name}</strong>
                <span className={cat.isGlobal ? 'badge badge-info' : 'badge badge-success'} style={{ fontSize: '0.7rem' }}>
                  {cat.isGlobal ? (
                    <span style={{ display: 'inline-flex', alignItems: 'center', gap: '0.2rem' }}>
                      <Globe size={11} /> Global
                    </span>
                  ) : (
                    <span style={{ display: 'inline-flex', alignItems: 'center', gap: '0.2rem' }}>
                      <Building size={11} /> Society
                    </span>
                  )}
                </span>
              </div>
              {cat.description && (
                <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)', margin: 0 }}>
                  {cat.description}
                </p>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
