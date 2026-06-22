import React from 'react';
import { Outlet, Link } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

export const StaffLayout: React.FC = () => {
  const { logout } = useAuth();
  
  return (
    <div className="staff-layout">
      <header>
        <h2>Staff Portal</h2>
        <nav>
          <Link to="/staff">Dashboard</Link> | <button onClick={logout}>Logout</button>
        </nav>
      </header>
      <main>
        <Outlet />
      </main>
    </div>
  );
};
