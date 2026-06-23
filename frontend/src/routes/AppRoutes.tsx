import React from 'react';
import { Routes, Route } from 'react-router-dom';
import { ProtectedRoute } from './ProtectedRoute';

import { PublicLayout } from '../layouts/PublicLayout';
import { CustomerLayout } from '../layouts/CustomerLayout';
import { AdminLayout } from '../layouts/AdminLayout';
import { StaffLayout } from '../layouts/StaffLayout';
import { KitchenLayout } from '../layouts/KitchenLayout';

import { 
  HomePage, 
  LoginPage, 
  RegisterPage, 
  UnauthorizedPage, 
  NotFoundPage 
} from '../pages/public/PublicPages';

import { MenuPage } from '../pages/public/MenuPage';
import { FoodDetailPage } from '../pages/public/FoodDetailPage';
import { CartPage } from '../pages/customer/CartPage';
import { CheckoutPage } from '../pages/customer/CheckoutPage';
import { OrderListPage } from '../pages/customer/OrderListPage';
import { OrderDetailPage } from '../pages/customer/OrderDetailPage';

import { ReservationCreatePage } from '../pages/customer/ReservationCreatePage';
import { ReservationListPage } from '../pages/customer/ReservationListPage';
import { ReservationDetailPage } from '../pages/customer/ReservationDetailPage';
import { ProfilePage } from '../pages/customer/ProfilePage';

import { 
  CustomerHomePage, 
  AdminHomePage, 
  AdminUsersPage,
  AdminFoodsPage,
  AdminCategoriesPage,
  AdminCouponsPage,
  AdminReportsPage,
  StaffHomePage, 
  StaffOrdersPage,
  StaffTablesPage,
  KitchenHomePage,
  KitchenOrdersPage
} from '../pages/RolePages';

export const AppRoutes: React.FC = () => {
  return (
    <Routes>
      {/* Public Routes */}
      <Route element={<PublicLayout />}>
        <Route path="/" element={<HomePage />} />
        <Route path="/menu" element={<MenuPage baseUrl="/menu" />} />
        <Route path="/menu/:foodId" element={<FoodDetailPage backUrl="/menu" />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/unauthorized" element={<UnauthorizedPage />} />
      </Route>

      {/* Customer Routes */}
      <Route element={<ProtectedRoute allowedRoles={['ROLE_CUSTOMER']} />}>
        <Route element={<CustomerLayout />}>
          <Route path="/customer" element={<CustomerHomePage />} />
          <Route path="/customer/menu" element={<MenuPage baseUrl="/customer/menu" />} />
          <Route path="/customer/menu/:foodId" element={<FoodDetailPage backUrl="/customer/menu" />} />
          
          <Route path="/customer/cart" element={<CartPage />} />
          <Route path="/customer/checkout" element={<CheckoutPage />} />
          <Route path="/customer/orders" element={<OrderListPage />} />
          <Route path="/customer/orders/:orderId" element={<OrderDetailPage />} />
          
          <Route path="/customer/reservations" element={<ReservationListPage />} />
          <Route path="/customer/reservations/new" element={<ReservationCreatePage />} />
          <Route path="/customer/reservations/:reservationId" element={<ReservationDetailPage />} />
          <Route path="/customer/profile" element={<ProfilePage />} />
        </Route>
      </Route>

      {/* Admin Routes */}
      <Route element={<ProtectedRoute allowedRoles={['ROLE_ADMIN']} />}>
        <Route element={<AdminLayout />}>
          <Route path="/admin" element={<AdminHomePage />} />
          <Route path="/admin/users" element={<AdminUsersPage />} />
          <Route path="/admin/foods" element={<AdminFoodsPage />} />
          <Route path="/admin/categories" element={<AdminCategoriesPage />} />
          <Route path="/admin/coupons" element={<AdminCouponsPage />} />
          <Route path="/admin/reports" element={<AdminReportsPage />} />
        </Route>
      </Route>

      {/* Staff Routes */}
      <Route element={<ProtectedRoute allowedRoles={['ROLE_STAFF', 'ROLE_ADMIN']} />}>
        <Route element={<StaffLayout />}>
          <Route path="/staff" element={<StaffHomePage />} />
          <Route path="/staff/orders" element={<StaffOrdersPage />} />
          <Route path="/staff/tables" element={<StaffTablesPage />} />
        </Route>
      </Route>

      {/* Kitchen Routes */}
      <Route element={<ProtectedRoute allowedRoles={['ROLE_KITCHEN', 'ROLE_ADMIN']} />}>
        <Route element={<KitchenLayout />}>
          <Route path="/kitchen" element={<KitchenHomePage />} />
          <Route path="/kitchen/orders" element={<KitchenOrdersPage />} />
        </Route>
      </Route>

      {/* Catch All */}
      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
};
