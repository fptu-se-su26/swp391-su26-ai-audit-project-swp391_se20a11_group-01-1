import React, { createContext, useContext, useEffect, useState } from 'react';
import API, { setAccessToken } from '../services/api';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [authLoading, setAuthLoading] = useState(true);

  const storeSession = (data) => {
    setAccessToken(data?.token);
    const nextUser = data ? {
      userId: data.userId, username: data.username, email: data.email, roleName: data.roleName
    } : null;
    setUser(nextUser);
    return nextUser;
  };

  useEffect(() => {
    const bootstrap = async () => {
      try {
        const { data } = await API.post('/auth/refresh');
        storeSession(data);
      } catch {
        setAccessToken(null);
        setUser(null);
      } finally {
        setAuthLoading(false);
      }
    };
    bootstrap();
    const expire = () => { setUser(null); setAccessToken(null); };
    window.addEventListener('auth-expired', expire);
    return () => window.removeEventListener('auth-expired', expire);
  }, []);

  const login = async (email, password) => {
    try {
      const { data } = await API.post('/auth/login', { email, password });
      const nextUser = storeSession(data);
      return { success: true, user: nextUser, role: nextUser.roleName };
    } catch {
      return { success: false, message: 'Email hoặc mật khẩu không đúng' };
    }
  };

  const register = async ({ username, email, password }) => {
    try {
      const { data } = await API.post('/auth/register', { username, email, password });
      return { success: true, user: storeSession(data), message: 'Đăng ký thành công' };
    } catch (error) {
      return { success: false, message: error.response?.data?.message || 'Đăng ký thất bại' };
    }
  };

  const changePassword = async (oldPassword, newPassword) => {
    try {
      await API.post('/auth/change-password', { email: user?.email, oldPassword, newPassword });
      await logout();
      return { success: true, message: 'Đổi mật khẩu thành công. Vui lòng đăng nhập lại.' };
    } catch (error) {
      return { success: false, message: error.response?.data?.message || 'Đổi mật khẩu thất bại' };
    }
  };

  const logout = async () => {
    try { await API.post('/auth/logout'); } catch {}
    setAccessToken(null);
    setUser(null);
    sessionStorage.removeItem('user');
    localStorage.removeItem('token');
  };

  const getRole = () => (user?.roleName || '').toUpperCase();
  const hasRole = roles => !!user && (!roles?.length || roles.map(r => r.toUpperCase()).includes(getRole()));

  return <AuthContext.Provider value={{
    user, authLoading, login, register, changePassword, logout,
    isAuthenticated: () => !!user, getRole, hasRole, setUser
  }}>{children}</AuthContext.Provider>;
}

export function useAuth() { return useContext(AuthContext); }
