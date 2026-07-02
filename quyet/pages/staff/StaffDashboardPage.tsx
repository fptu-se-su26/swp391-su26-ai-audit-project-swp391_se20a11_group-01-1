import React from 'react';
import { NavLink } from 'react-router-dom';
import './StaffDashboardPage.css';

export const StaffDashboardPage: React.FC = () => {
  return (
    <div className="staff-dashboard-page">
      <div className="staff-header">
        <h2>Trang chủ Nhân viên</h2>
        <p>Chào mừng bạn trở lại! Dưới đây là các công cụ quản lý tại quầy.</p>
      </div>

      <div className="staff-modules-grid">
        <div className="staff-module-card">
          <h3>Quản lý Đơn hàng</h3>
          <p>Xem và cập nhật trạng thái đơn hàng hiện tại.</p>
          <NavLink to="/staff/orders" className="btn-staff-primary">Đi đến Đơn hàng</NavLink>
        </div>
        
        <div className="staff-module-card highlight">
          <h3>Tạo Đơn tại quầy</h3>
          <p>Tạo mới đơn hàng cho khách ăn tại bàn, mang về.</p>
          <NavLink to="/staff/orders/new" className="btn-staff-primary">+ Tạo đơn mới</NavLink>
        </div>
      </div>

      <div className="limitation-notice">
        <strong>Lưu ý nghiệp vụ:</strong> 
        <br/>Theo phân quyền hệ thống hiện tại, nhân viên không có quyền truy cập trực tiếp vào các module quản lý Bàn (Tables) và Đặt bàn (Reservations) của hệ thống quản trị. Bạn chỉ có quyền tạo và quản lý Đơn hàng.
      </div>
    </div>
  );
};
