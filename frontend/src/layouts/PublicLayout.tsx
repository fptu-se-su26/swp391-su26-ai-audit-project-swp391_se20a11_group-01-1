import React from 'react';
import { Outlet, NavLink, Link } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { getDefaultRouteByRoles } from '../utils/routeUtils';

export const PublicLayout: React.FC = () => {
  const { isAuthenticated, user, logout } = useAuth();

  return (
    <div className="layout">
      <header>
        <div className="header-left">
          <Link to="/" style={{ textDecoration: 'none', color: 'white' }}>
            <h2>Restaurant</h2>
          </Link>
          <nav>
            <NavLink to="/" end className={({ isActive }) => isActive ? 'active' : ''}>Trang chủ</NavLink> |{' '}
            <NavLink to="/menu" className={({ isActive }) => isActive ? 'active' : ''}>Thực đơn</NavLink>
          </nav>
        </div>
        <div className="header-right">
          {isAuthenticated ? (
            <>
              <Link to={getDefaultRouteByRoles(user?.roles)} style={{ color: '#bdc3c7' }}>
                Khu vực của bạn ({user?.roles?.join(', ')})
              </Link>
              <button onClick={logout} style={{ marginLeft: '1rem' }}>Logout</button>
            </>
          ) : (
            <nav>
              <NavLink to="/login" className={({ isActive }) => isActive ? 'active' : ''}>Đăng nhập</NavLink> |{' '}
              <NavLink to="/register" className={({ isActive }) => isActive ? 'active' : ''}>Đăng ký</NavLink>
            </nav>
          )}
        </div>
      </header>
      <main>
        <Outlet />
      </main>
    </div>
  );
};
