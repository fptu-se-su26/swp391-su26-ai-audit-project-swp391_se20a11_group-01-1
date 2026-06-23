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
export { StaffDashboardPage } from './staff/StaffDashboardPage';
export { StaffOrdersPage } from './staff/StaffOrdersPage';
export { StaffOrderCreatePage } from './staff/StaffOrderCreatePage';
export { StaffOrderDetailPage } from './staff/StaffOrderDetailPage';

// Kitchen Pages
export { KitchenQueuePage } from './kitchen/KitchenQueuePage';
export { KitchenOrderDetailPage } from './kitchen/KitchenOrderDetailPage';
