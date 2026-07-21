import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function PrivateRoute({ children, roles }) {
  const { user, hasRole, authLoading } = useAuth();

  if (authLoading) {
    return <div style={{ padding: 40, textAlign: 'center' }}>Đang kiểm tra phiên đăng nhập...</div>;
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (roles && !hasRole(roles)) {
    return <Navigate to="/login" replace />;
  }

  return children;
}

export default PrivateRoute;
