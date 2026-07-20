import axios from 'axios';

let accessToken = sessionStorage.getItem('access_token');
let refreshPromise = null;

export const setAccessToken = (token) => {
  accessToken = token || null;
  if (token) sessionStorage.setItem('access_token', token);
  else sessionStorage.removeItem('access_token');
};

const API = axios.create({
  baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080/api',
  withCredentials: true,
  headers: { 'Content-Type': 'application/json' }
});

API.interceptors.request.use((config) => {
  if (accessToken) config.headers.Authorization = `Bearer ${accessToken}`;
  return config;
});

API.interceptors.response.use(
  response => response,
  async error => {
    const original = error.config;
    const isAuthEndpoint = original?.url?.includes('/auth/login') || original?.url?.includes('/auth/refresh');
    if (error.response?.status !== 401 || original?._retried || isAuthEndpoint) {
      return Promise.reject(error);
    }
    original._retried = true;
    try {
      if (!refreshPromise) {
        refreshPromise = API.post('/auth/refresh', {}, { _retried: true })
          .then(({ data }) => {
            setAccessToken(data.token);
            return data.token;
          })
          .finally(() => { refreshPromise = null; });
      }
      const token = await refreshPromise;
      original.headers.Authorization = `Bearer ${token}`;
      return API(original);
    } catch (refreshError) {
      setAccessToken(null);
      window.dispatchEvent(new Event('auth-expired'));
      return Promise.reject(refreshError);
    }
  }
);

export default API;
