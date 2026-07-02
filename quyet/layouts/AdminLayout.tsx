import React from 'react';
import { Outlet, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

export const AdminLayout: React.FC = () => {
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
          <h2>Admin Portal</h2>
          <nav>
            <NavLink to="/admin" end className={({ isActive }) => isActive ? 'active' : ''}>Dashboard</NavLink> |{' '}
            <NavLink to="/admin/users" className={({ isActive }) => isActive ? 'active' : ''}>Người dùng</NavLink> |{' '}
            <NavLink to="/admin/foods" className={({ isActive }) => isActive ? 'active' : ''}>Món ăn</NavLink> |{' '}
            <NavLink to="/admin/categories" className={({ isActive }) => isActive ? 'active' : ''}>Danh mục</NavLink> |{' '}
            <NavLink to="/admin/coupons" className={({ isActive }) => isActive ? 'active' : ''}>Coupon</NavLink> |{' '}
            <NavLink to="/admin/reports" className={({ isActive }) => isActive ? 'active' : ''}>Báo cáo</NavLink>
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
