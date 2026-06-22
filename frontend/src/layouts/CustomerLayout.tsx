import React from 'react';
import { Outlet, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

export const CustomerLayout: React.FC = () => {
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
          <h2>Customer Portal</h2>
          <nav>
            <NavLink to="/customer/menu" className={({ isActive }) => isActive ? 'active' : ''}>Thực đơn</NavLink> |{' '}
            <NavLink to="/customer/cart" className={({ isActive }) => isActive ? 'active' : ''}>Giỏ hàng</NavLink> |{' '}
            <NavLink to="/customer/orders" className={({ isActive }) => isActive ? 'active' : ''}>Đơn hàng</NavLink> |{' '}
            <NavLink to="/customer/reservations" className={({ isActive }) => isActive ? 'active' : ''}>Đặt bàn</NavLink> |{' '}
            <NavLink to="/customer/profile" className={({ isActive }) => isActive ? 'active' : ''}>Hồ sơ</NavLink>
          </nav>
        </div>
        <div className="header-right">
          <span>{user?.fullName || user?.username} ({user?.roles?.join(', ')})</span>
          <button onClick={handleLogout} style={{ marginLeft: '1rem' }}>Logout</button>
        </div>
      </header>
      <main>
        <Outlet />
      </main>
    </div>
  );
};
