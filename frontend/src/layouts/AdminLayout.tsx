import React from 'react';
import { Outlet, Link } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

export const AdminLayout: React.FC = () => {
  const { logout } = useAuth();
  
  return (
    <div className="admin-layout">
      <header>
        <h2>Admin Portal</h2>
        <nav>
          <Link to="/admin">Dashboard</Link> | <button onClick={logout}>Logout</button>
        </nav>
      </header>
      <main>
        <Outlet />
      </main>
    </div>
  );
};
