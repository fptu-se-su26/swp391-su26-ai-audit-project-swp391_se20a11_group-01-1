import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

import MainLayout from '../layouts/MainLayout';
import AdminLayout from '../layouts/AdminLayout';
import AuthLayout from '../layouts/AuthLayout';

import Login from '../pages/auth/Login';
import Register from '../pages/auth/Register';
import ForgotPassword from '../pages/auth/ForgotPassword';

import Home from '../pages/customer/Home';
import Menu from '../pages/customer/Menu';
import FoodDetail from '../pages/customer/FoodDetail';
import Profile from '../pages/customer/Profile';
import Reviews from '../pages/customer/Reviews';

import Dashboard from '../pages/admin/Dashboard';
import UserManagement from '../pages/admin/UserManagement';
import FoodManagement from '../pages/admin/FoodManagement';
import CategoryManagement from '../pages/admin/CategoryManagement';
import ReviewManagement from '../pages/admin/ReviewManagement';

// Guard: chỉ cho vào nếu đã login
const PrivateRoute = ({ children }) => {
  const { isAuthenticated } = useAuth();
  return isAuthenticated ? children : <Navigate to="/login" replace />;
};

// Guard: chỉ cho ADMIN vào
const AdminRoute = ({ children }) => {
  const { isAuthenticated, isAdmin } = useAuth();
  if (!isAuthenticated) return <Navigate to="/login" replace />;
  if (!isAdmin()) return <Navigate to="/" replace />;
  return children;
};

const AppRoutes = () => (
  <Routes>
    {/* Auth routes */}
    <Route element={<AuthLayout />}>
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/forgot-password" element={<ForgotPassword />} />
    </Route>

    {/* Customer routes */}
    <Route element={<MainLayout />}>
      <Route path="/" element={<Home />} />
      <Route path="/menu" element={<Menu />} />
      <Route path="/food/:id" element={<FoodDetail />} />
      <Route path="/reviews" element={<Reviews />} />
      <Route path="/profile" element={<PrivateRoute><Profile /></PrivateRoute>} />
    </Route>

    {/* Admin routes */}
    <Route path="/admin" element={<AdminRoute><AdminLayout /></AdminRoute>}>
      <Route index element={<Navigate to="dashboard" replace />} />
      <Route path="dashboard" element={<Dashboard />} />
      <Route path="users" element={<UserManagement />} />
      <Route path="foods" element={<FoodManagement />} />
      <Route path="categories" element={<CategoryManagement />} />
      <Route path="reviews" element={<ReviewManagement />} />
    </Route>

    {/* Fallback */}
    <Route path="*" element={<Navigate to="/" replace />} />
  </Routes>
);

export default AppRoutes;
