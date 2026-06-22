import React from 'react';
import { Outlet, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

export const KitchenLayout: React.FC = () => {
  const { logout, user } = useAuth();
  const navigate = useNavigate();
  
  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="layout">
      <header>
        <div className="header-left">
          <h2>Kitchen Portal</h2>
          <nav>
            <NavLink to="/kitchen/orders" className={({ isActive }) => isActive ? 'active' : ''}>Đơn chờ / Đang chế biến / Món sẵn sàng</NavLink>
          </nav>
        </div>
        <div className="header-right">
          <span>{user?.username} ({user?.roles?.join(', ')})</span>
          <button onClick={handleLogout} style={{ marginLeft: '1rem' }}>Logout</button>
        </div>
      </header>
      <main>
        <Outlet />
      </main>
    </div>
  );
};
