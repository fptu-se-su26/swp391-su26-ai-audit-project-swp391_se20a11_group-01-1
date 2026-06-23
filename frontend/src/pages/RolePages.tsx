import React from 'react';
import { PlaceholderPage } from '../components/common/PlaceholderPage';

// Customer Pages
export const CustomerHomePage: React.FC = () => <PlaceholderPage title="Customer Dashboard" />;

// Admin Pages
export const AdminHomePage: React.FC = () => <PlaceholderPage title="Admin Dashboard" />;
export const AdminUsersPage: React.FC = () => <PlaceholderPage title="Quản lý Người dùng" />;
export const AdminFoodsPage: React.FC = () => <PlaceholderPage title="Quản lý Món ăn" />;
export const AdminCategoriesPage: React.FC = () => <PlaceholderPage title="Quản lý Danh mục" />;
export const AdminCouponsPage: React.FC = () => <PlaceholderPage title="Quản lý Coupon" />;
export const AdminReportsPage: React.FC = () => <PlaceholderPage title="Báo cáo Doanh thu" />;

// Staff Pages
export const StaffHomePage: React.FC = () => <PlaceholderPage title="Staff Dashboard" />;
export const StaffOrdersPage: React.FC = () => <PlaceholderPage title="Đơn tại quầy / Đơn hàng" />;
export const StaffTablesPage: React.FC = () => <PlaceholderPage title="Quản lý Bàn" />;

// Kitchen Pages
export const KitchenHomePage: React.FC = () => <PlaceholderPage title="Kitchen Dashboard" />;
export const KitchenOrdersPage: React.FC = () => <PlaceholderPage title="Đơn chờ / Đang chế biến / Món sẵn sàng" />;
