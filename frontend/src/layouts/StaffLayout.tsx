import React from 'react';
import { Outlet, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

export const StaffLayout: React.FC = () => {
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
          <h2>Staff Portal</h2>
          <nav>
            <NavLink to="/staff" end className={({ isActive }) => isActive ? 'active' : ''}>Trang chủ</NavLink> |{' '}
            <NavLink to="/staff/orders" className={({ isActive }) => isActive ? 'active' : ''}>Quản lý Đơn hàng</NavLink>
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
