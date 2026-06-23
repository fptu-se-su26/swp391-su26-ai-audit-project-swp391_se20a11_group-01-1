import React, { useState, useEffect } from 'react';
import { adminDashboardApi } from '../../api/adminDashboardApi';
import { getApiErrorMessage } from '../../utils/getApiErrorMessage';
import { formatCurrency } from '../../utils/formatCurrency';
import type { DashboardSummaryResponse } from '../../types/report';
import './AdminDashboardPage.css';

export const AdminDashboardPage: React.FC = () => {
  const [summary, setSummary] = useState<DashboardSummaryResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchSummary = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await adminDashboardApi.getSummary();
      setSummary(data);
    } catch (err) {
      setError(getApiErrorMessage(err, 'Lỗi khi tải thông tin tổng quan.'));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchSummary();
  }, []);

  if (loading) {
    return <div className="admin-page-state">Đang tải dữ liệu tổng quan...</div>;
  }

  if (error || !summary) {
    return (
      <div className="admin-page-state text-error">
        <p>{error || 'Không thể tải dữ liệu.'}</p>
        <button className="btn-secondary" onClick={fetchSummary}>Thử lại</button>
      </div>
    );
  }

  return (
    <div className="admin-dashboard-page">
      <div className="admin-header">
        <h2>Tổng quan hệ thống</h2>
      </div>

      <div className="dashboard-grid">
        <div className="summary-card highlight">
          <h3>Doanh thu hôm nay</h3>
          <p className="summary-value">{formatCurrency(summary.todayRevenue)}</p>
        </div>
        <div className="summary-card">
          <h3>Đơn hàng hôm nay</h3>
          <p className="summary-value">{summary.todayOrders}</p>
        </div>
        <div className="summary-card">
          <h3>Bàn trống hiện tại</h3>
          <p className="summary-value">{summary.availableTables}</p>
        </div>
      </div>

      <div className="dashboard-section">
        <h3>Thống kê doanh thu & Đơn hàng (Tổng cộng)</h3>
        <div className="dashboard-grid">
          <div className="summary-card">
            <h4>Tổng doanh thu</h4>
            <p className="summary-value">{formatCurrency(summary.totalRevenue)}</p>
          </div>
          <div className="summary-card">
            <h4>Tổng số đơn</h4>
            <p className="summary-value">{summary.totalOrders}</p>
          </div>
          <div className="summary-card">
            <h4>Đơn đã thanh toán</h4>
            <p className="summary-value text-success">{summary.paidOrders}</p>
          </div>
          <div className="summary-card">
            <h4>Đơn đang chờ</h4>
            <p className="summary-value text-warning">{summary.pendingOrders}</p>
          </div>
          <div className="summary-card">
            <h4>Đơn đã hủy</h4>
            <p className="summary-value text-danger">{summary.cancelledOrders}</p>
          </div>
        </div>
      </div>

      <div className="dashboard-section">
        <h3>Thống kê đặt bàn (Tổng cộng)</h3>
        <div className="dashboard-grid">
          <div className="summary-card">
            <h4>Tổng lượt đặt bàn</h4>
            <p className="summary-value">{summary.totalReservations}</p>
          </div>
          <div className="summary-card">
            <h4>Đang chờ xử lý</h4>
            <p className="summary-value text-warning">{summary.pendingReservations}</p>
          </div>
        </div>
      </div>
    </div>
  );
};
