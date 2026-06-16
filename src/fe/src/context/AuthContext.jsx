import { createContext, useContext, useState } from 'react';
import { login as apiLogin, register as apiRegister, saveAuth, logout as doLogout, getCurrentUser } from '../services/authService';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(getCurrentUser());
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const login = async (credentials) => {
    setLoading(true); setError(null);
    try {
      const { data } = await apiLogin(credentials);
      saveAuth(data);
      setUser(getCurrentUser());
      return data;
    } catch (err) {
      const msg = err.response?.data?.message || 'Đăng nhập thất bại';
      setError(msg); throw err;
    } finally { setLoading(false); }
  };

  const register = async (userData) => {
    setLoading(true); setError(null);
    try {
      const { data } = await apiRegister(userData);
      saveAuth(data);
      setUser(getCurrentUser());
      return data;
    } catch (err) {
      const msg = err.response?.data?.message || 'Đăng ký thất bại';
      setError(msg); throw err;
    } finally { setLoading(false); }
  };

  const logout = () => { doLogout(); setUser(null); };

  const isAdmin   = () => user?.roles?.includes('ROLE_ADMIN');
  const isStaff   = () => user?.roles?.includes('ROLE_STAFF');
  const isKitchen = () => user?.roles?.includes('ROLE_KITCHEN');

  return (
    <AuthContext.Provider value={{ user, loading, error, login, register, logout, isAdmin, isStaff, isKitchen, isAuthenticated: !!user }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used inside AuthProvider');
  return ctx;
};

export default AuthContext;
