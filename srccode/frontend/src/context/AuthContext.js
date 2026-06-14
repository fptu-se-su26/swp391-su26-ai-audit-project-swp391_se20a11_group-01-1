import React, { createContext, useContext, useState } from 'react';
import API from '../services/api';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const savedUser = localStorage.getItem('user');
    return savedUser ? JSON.parse(savedUser) : null;
  });

  const login = async (email, password) => {
    try {
      const response = await API.post('/auth/login', {
        email,
        password
      });

      const userData = response.data;

      setUser(userData);
      localStorage.setItem('user', JSON.stringify(userData));

      return {
        success: true,
        user: userData,
        role: userData.roleName
      };
    } catch (error) {
      console.error('Login error:', error);

      return {
        success: false,
        message:
          error.response?.data?.message ||
          'Email hoặc mật khẩu không đúng'
      };
    }
  };

  const register = async ({ username, email, password }) => {
    try {
      const response = await API.post('/auth/register', {
        username,
        email,
        password
      });

      return {
        success: true,
        user: response.data,
        message: 'Đăng ký thành công'
      };
    } catch (error) {
      console.error('Register error:', error);

      return {
        success: false,
        message:
          error.response?.data?.message ||
          'Đăng ký thất bại'
      };
    }
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem('user');
    localStorage.removeItem('restaurant_user');
  };

  const hasRole = (roles) => {
    if (!user) return false;

    const userRole = (user.roleName || user.role || '').toUpperCase();

    if (!roles) return true;

    const allowedRoles = roles.map(role => role.toUpperCase());

    return allowedRoles.includes(userRole);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        login,
        register,
        logout,
        hasRole
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}