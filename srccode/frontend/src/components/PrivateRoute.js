import React from 'react';
import { Navigate } from 'react-router-dom';

function PrivateRoute({ children, roles }) {
  const savedUser = localStorage.getItem('user');
  const user = savedUser ? JSON.parse(savedUser) : null;

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  const userRole = (user.roleName || user.role || '').toUpperCase();

  const allowedRoles = roles
    ? roles.map(role => role.toUpperCase())
    : null;

  if (allowedRoles && !allowedRoles.includes(userRole)) {
    return <Navigate to="/login" replace />;
  }

  return children;
}

export default PrivateRoute;