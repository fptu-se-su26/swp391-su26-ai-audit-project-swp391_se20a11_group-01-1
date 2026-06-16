import { Navigate, Route, Routes } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

import AuthLayout from '../layouts/AuthLayout';
import AdminLayout from '../layouts/AdminLayout';
import StaffLayout from '../layouts/StaffLayout';
import KitchenLayout from '../layouts/KitchenLayout';
import CustomerLayout from '../layouts/CustomerLayout';

import Login from '../pages/auth/Login';
import Register from '../pages/auth/Register';
import ForgotPassword from '../pages/auth/ForgotPassword';
import Landing from '../pages/Landing';

import Dashboard from '../pages/admin/Dashboard';
import UserManagement from '../pages/admin/UserManagement';
import FoodManagement from '../pages/admin/FoodManagement';
import CategoryManagement from '../pages/admin/CategoryManagement';
import OrderManagement from '../pages/admin/OrderManagement';
import ReservationManagement from '../pages/admin/ReservationManagement';
import TableManagement from '../pages/admin/TableManagement';
import Reports from '../pages/admin/Reports';
import AdminFeedback from '../pages/admin/AdminFeedback';
import CouponManagement from '../pages/admin/CouponManagement';

import StaffTables from '../pages/staff/StaffTables';
import StaffOrders from '../pages/staff/StaffOrders';
import StaffReservations from '../pages/staff/StaffReservations';
import StaffCustomers from '../pages/staff/StaffCustomers';

import KitchenQueue from '../pages/kitchen/KitchenQueue';
import KitchenHistory from '../pages/kitchen/KitchenHistory';

import CustomerMenu from '../pages/customer/CustomerMenu';
import Cart from '../pages/customer/Cart';
import CustomerReservation from '../pages/customer/CustomerReservation';
import CustomerOrders from '../pages/customer/CustomerOrders';
import CustomerProfile from '../pages/customer/CustomerProfile';
import CustomerFeedback from '../pages/customer/CustomerFeedback';

const Require = ({ children, roles }) => {
  const { user, isAuthenticated } = useAuth();
  if (!isAuthenticated) return <Navigate to="/login" replace />;
  if (roles && !roles.some(r => user?.roles?.includes(r))) return <Navigate to="/login" replace />;
  return children;
};

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/landing" element={<Landing />} />

      <Route element={<AuthLayout />}>
        <Route path="/login"           element={<Login />} />
        <Route path="/register"        element={<Register />} />
        <Route path="/forgot-password" element={<ForgotPassword />} />
      </Route>

      {/* Admin */}
      <Route path="/admin" element={<Require roles={['ROLE_ADMIN']}><AdminLayout /></Require>}>
        <Route index element={<Navigate to="dashboard" replace />} />
        <Route path="dashboard"    element={<Dashboard />} />
        <Route path="users"        element={<UserManagement />} />
        <Route path="foods"        element={<FoodManagement />} />
        <Route path="categories"   element={<CategoryManagement />} />
        <Route path="orders"       element={<OrderManagement />} />
        <Route path="reservations" element={<ReservationManagement />} />
        <Route path="tables"       element={<TableManagement />} />
        <Route path="reports"      element={<Reports />} />
        <Route path="reviews"      element={<AdminFeedback />} />
        <Route path="coupons"      element={<CouponManagement />} />
      </Route>

      {/* Staff */}
      <Route path="/staff" element={<Require roles={['ROLE_STAFF']}><StaffLayout /></Require>}>
        <Route index element={<Navigate to="tables" replace />} />
        <Route path="tables"       element={<StaffTables />} />
        <Route path="orders"       element={<StaffOrders />} />
        <Route path="reservations" element={<StaffReservations />} />
        <Route path="customers"    element={<StaffCustomers />} />
      </Route>

      {/* Kitchen */}
      <Route path="/kitchen" element={<Require roles={['ROLE_KITCHEN']}><KitchenLayout /></Require>}>
        <Route index element={<Navigate to="queue" replace />} />
        <Route path="queue"   element={<KitchenQueue />} />
        <Route path="history" element={<KitchenHistory />} />
      </Route>

      {/* Customer */}
      <Route path="/customer" element={<Require roles={['ROLE_CUSTOMER']}><CustomerLayout /></Require>}>
        <Route index element={<Navigate to="menu" replace />} />
        <Route path="menu"        element={<CustomerMenu />} />
        <Route path="cart"        element={<Cart />} />
        <Route path="reservation" element={<CustomerReservation />} />
        <Route path="orders"      element={<CustomerOrders />} />
        <Route path="profile"     element={<CustomerProfile />} />
        <Route path="feedback"    element={<CustomerFeedback />} />
      </Route>

      <Route path="/" element={<Navigate to="/landing" replace />} />
      <Route path="*" element={<Navigate to="/landing" replace />} />
    </Routes>
  );
}
