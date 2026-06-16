import axiosInstance from './axiosInstance';

/** POST /api/auth/login — body: { email, password } */
export const login = (credentials) => axiosInstance.post('/auth/login', credentials);

/** POST /api/auth/register — body: { fullName, email, password, phone } */
export const register = (userData) => axiosInstance.post('/auth/register', userData);

/** Save auth info to localStorage */
export const saveAuth = (data) => {
  localStorage.setItem('token', data.token);
  localStorage.setItem('user', JSON.stringify({
    email: data.email,
    fullName: data.fullName,
    roles: data.roles, // Set<String> e.g. ["ROLE_ADMIN"]
  }));
};

export const logout = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('user');
};

export const getCurrentUser = () => {
  const user = localStorage.getItem('user');
  return user ? JSON.parse(user) : null;
};

export const isAuthenticated = () => !!localStorage.getItem('token');

/** roles is a Set<String> like ["ROLE_ADMIN"] */
export const isAdmin = () => {
  const user = getCurrentUser();
  return user?.roles?.includes('ROLE_ADMIN');
};
