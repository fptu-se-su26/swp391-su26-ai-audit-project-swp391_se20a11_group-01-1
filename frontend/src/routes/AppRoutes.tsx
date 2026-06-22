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

import { 
  CustomerHomePage, 
  AdminHomePage, 
  StaffHomePage, 
  KitchenHomePage 
} from '../pages/RolePages';

export const AppRoutes: React.FC = () => {
  return (
    <Routes>
      {/* Public Routes */}
      <Route element={<PublicLayout />}>
        <Route path="/" element={<HomePage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/unauthorized" element={<UnauthorizedPage />} />
      </Route>

      {/* Customer Routes */}
      <Route element={<ProtectedRoute allowedRoles={['ROLE_CUSTOMER']} />}>
        <Route element={<CustomerLayout />}>
          <Route path="/customer" element={<CustomerHomePage />} />
        </Route>
      </Route>

      {/* Admin Routes */}
      <Route element={<ProtectedRoute allowedRoles={['ROLE_ADMIN']} />}>
        <Route element={<AdminLayout />}>
          <Route path="/admin" element={<AdminHomePage />} />
        </Route>
      </Route>

      {/* Staff Routes */}
      <Route element={<ProtectedRoute allowedRoles={['ROLE_STAFF', 'ROLE_ADMIN']} />}>
        <Route element={<StaffLayout />}>
          <Route path="/staff" element={<StaffHomePage />} />
        </Route>
      </Route>

      {/* Kitchen Routes */}
      <Route element={<ProtectedRoute allowedRoles={['ROLE_KITCHEN', 'ROLE_ADMIN']} />}>
        <Route element={<KitchenLayout />}>
          <Route path="/kitchen" element={<KitchenHomePage />} />
        </Route>
      </Route>

      {/* Catch All */}
      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
};
