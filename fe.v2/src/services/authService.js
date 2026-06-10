import axiosInstance from './axiosInstance';

/**
 * POST /api/auth/login
 * body: { email, password }
 * response: { token, type, id, email, name, role }
 */
export const login = (credentials) =>
  axiosInstance.post('/auth/login', credentials);

/**
 * POST /api/auth/register
 * body: { name, email, password }
 */
export const register = (userData) =>
  axiosInstance.post('/auth/register', userData);

/** Save auth info to localStorage */
export const saveAuth = (data) => {
  localStorage.setItem('token', data.token);
  localStorage.setItem('user', JSON.stringify({
    id: data.id,
    name: data.name,
    email: data.email,
    role: data.role,
  }));
};

/** Remove auth info from localStorage */
export const logout = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('user');
};

/** Get current user from localStorage */
export const getCurrentUser = () => {
  const user = localStorage.getItem('user');
  return user ? JSON.parse(user) : null;
};

/** Check if user is logged in */
export const isAuthenticated = () => !!localStorage.getItem('token');

/** Check if current user is admin */
export const isAdmin = () => {
  const user = getCurrentUser();
  return user?.role === 'ADMIN';
};
