import React from 'react';
import { Outlet, Link } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

export const CustomerLayout: React.FC = () => {
  const { logout } = useAuth();
  
  return (
    <div className="customer-layout">
      <header>
        <h2>Customer Portal</h2>
        <nav>
          <Link to="/customer">Dashboard</Link> | <button onClick={logout}>Logout</button>
        </nav>
      </header>
      <main>
        <Outlet />
      </main>
    </div>
  );
};
