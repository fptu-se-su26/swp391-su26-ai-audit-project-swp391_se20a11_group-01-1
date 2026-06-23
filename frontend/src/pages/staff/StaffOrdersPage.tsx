import React, { useState, useEffect } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { staffOrderApi } from '../../api/staffOrderApi';
import { getApiErrorMessage } from '../../utils/getApiErrorMessage';
import { formatCurrency } from '../../utils/formatCurrency';
import { formatDateTime } from '../../utils/formatDateTime';
import { getOrderStatusLabel, getOrderStatusBadgeClass } from '../../utils/orderStatus';
import type { StaffOrderResponse } from '../../types/staff';
import type { OrderStatus } from '../../types/order';
import './StaffOrdersPage.css';

export const StaffOrdersPage: React.FC = () => {
  const [orders, setOrders] = useState<StaffOrderResponse[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [filterStatus, setFilterStatus] = useState<OrderStatus | 'ALL'>('ALL');
  
  const navigate = useNavigate();

  const fetchOrders = useCallback(async (showLoading = true) => {
    if (showLoading) setLoading(true);
    setError(null);
    try {
      const data = await staffOrderApi.getAllOrders();
      // Sort by latest first
      data.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
      setOrders(data);
    } catch (err) {
      setError(getApiErrorMessage(err, 'Lỗi khi tải danh sách đơn hàng.'));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchOrders(false);
  }, [fetchOrders]);

  const filteredOrders = filterStatus === 'ALL' 
    ? orders 
    : orders.filter(o => o.orderStatus === filterStatus);

  return (
    <div className="staff-orders-page">
      <div className="page-header">
        <h2>Quản lý Đơn hàng</h2>
        <button onClick={() => fetchOrders(true)} className="btn-refresh" disabled={loading}>
          Làm mới
        </button>
      </div>

      <div className="orders-actions">
        <NavLink to="/staff/orders/new" className="btn-create-order">+ Tạo đơn mới</NavLink>
        <div className="filter-group">
          <label>Trạng thái:</label>
          <select 
            value={filterStatus} 
            onChange={(e) => setFilterStatus(e.target.value as OrderStatus | 'ALL')}
          >
            <option value="ALL">Tất cả</option>
            <option value="PENDING">Chờ xác nhận</option>
            <option value="PENDING_PAYMENT">Chờ thanh toán</option>
            <option value="CONFIRMED">Đã xác nhận</option>
            <option value="PREPARING">Đang chuẩn bị</option>
            <option value="READY">Sẵn sàng</option>
            <option value="COMPLETED">Hoàn thành</option>
            <option value="CANCELLED">Đã hủy</option>
          </select>
        </div>
      </div>

      {error && <div className="error-message">{error}</div>}

      {loading && orders.length === 0 ? (
        <div className="loading-state">Đang tải đơn hàng...</div>
      ) : filteredOrders.length === 0 ? (
        <div className="empty-state">Không có đơn hàng nào khớp với điều kiện.</div>
      ) : (
        <div className="table-responsive">
          <table className="staff-table">
            <thead>
              <tr>
                <th>Mã ĐH</th>
                <th>Loại đơn</th>
                <th>Bàn</th>
                <th>Tổng tiền</th>
                <th>Trạng thái</th>
                <th>Thời gian tạo</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              {filteredOrders.map(order => (
                <tr key={order.id}>
                  <td>#{order.id}</td>
                  <td>{order.orderType === 'DINE_IN' ? 'Tại bàn' : order.orderType === 'TAKEAWAY' ? 'Mang về' : 'Giao hàng'}</td>
                  <td>{order.tableId ? `Bàn ${order.tableId}` : '-'}</td>
                  <td className="highlight-price">{formatCurrency(order.totalAmount)}</td>
                  <td>
                    <span className={`badge ${getOrderStatusBadgeClass(order.orderStatus)}`}>
                      {getOrderStatusLabel(order.orderStatus)}
                    </span>
                  </td>
                  <td>{formatDateTime(order.createdAt)}</td>
                  <td>
                    <button 
                      className="btn-view"
                      onClick={() => navigate(`/staff/orders/${order.id}`)}
                    >
                      Chi tiết
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};
