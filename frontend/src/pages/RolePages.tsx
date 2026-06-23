import React from 'react';
import { PlaceholderPage } from '../components/common/PlaceholderPage';

// Customer Pages
export const CustomerHomePage: React.FC = () => <PlaceholderPage title="Customer Dashboard" />;

// Admin Pages
export { AdminDashboardPage } from './admin/AdminDashboardPage';
export { AdminUsersPage } from './admin/AdminUsersPage';
export { AdminCategoriesPage } from './admin/AdminCategoriesPage';
export { AdminFoodsPage } from './admin/AdminFoodsPage';
export { AdminCouponsPage } from './admin/AdminCouponsPage';
export { AdminReportsPage } from './admin/AdminReportsPage';

// Staff Pages
export const StaffHomePage: React.FC = () => <PlaceholderPage title="Staff Dashboard" />;
export const StaffOrdersPage: React.FC = () => <PlaceholderPage title="Đơn tại quầy / Đơn hàng" />;
export const StaffTablesPage: React.FC = () => <PlaceholderPage title="Quản lý Bàn" />;

// Kitchen Pages
export const KitchenHomePage: React.FC = () => <PlaceholderPage title="Kitchen Dashboard" />;
export const KitchenOrdersPage: React.FC = () => <PlaceholderPage title="Đơn chờ / Đang chế biến / Món sẵn sàng" />;
