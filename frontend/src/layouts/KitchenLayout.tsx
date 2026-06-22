import React from 'react';
import { Outlet, Link } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

export const KitchenLayout: React.FC = () => {
  const { logout } = useAuth();
  
  return (
    <div className="kitchen-layout">
      <header>
        <h2>Kitchen Portal</h2>
        <nav>
          <Link to="/kitchen">Dashboard</Link> | <button onClick={logout}>Logout</button>
        </nav>
      </header>
      <main>
        <Outlet />
      </main>
    </div>
  );
};
