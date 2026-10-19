import React from 'react';
import { QueryClientProvider } from '@tanstack/react-query';
import { queryClient } from './app/queryClient';
import { Header } from './components/layout/Header';
import { DashboardView } from './features/dashboard/DashboardView';

export const App: React.FC = () => {
  return (
    <QueryClientProvider client={queryClient}>
      <div className="app-shell">
        <Header />
        <main>
          <DashboardView />
        </main>
      </div>
    </QueryClientProvider>
  );
};

export default App;
