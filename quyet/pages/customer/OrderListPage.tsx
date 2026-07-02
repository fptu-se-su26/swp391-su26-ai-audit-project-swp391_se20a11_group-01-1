import React, { useState, useEffect, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { orderApi } from '../../api/orderApi';
import { getApiErrorMessage } from '../../utils/getApiErrorMessage';
import { formatCurrency } from '../../utils/formatCurrency';
import { formatDateTime } from '../../utils/formatDateTime';
import { getOrderStatusLabel, getOrderStatusColor } from '../../utils/orderStatus';
import type { OrderResponse } from '../../types/order';
import './OrderListPage.css';

export const OrderListPage: React.FC = () => {
  const [orders, setOrders] = useState<OrderResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchOrders = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await orderApi.getMyOrders();
      // Sort newest first
      data.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
      setOrders(data);
    } catch (err) {
      setError(getApiErrorMessage(err, 'Không thể tải danh sách đơn hàng.'));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    let mounted = true;
    const loadData = async () => {
      setLoading(true);
      setError(null);
      try {
        const data = await orderApi.getMyOrders();
        if (mounted) {
          data.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
          setOrders(data);
        }
      } catch (err) {
        if (mounted) {
          setError(getApiErrorMessage(err, 'Không thể tải danh sách đơn hàng.'));
        }
      } finally {
        if (mounted) setLoading(false);
      }
    };
    loadData();
    return () => { mounted = false; };
  }, []);

  if (loading) return <div className="order-list-state">Đang tải danh sách đơn hàng...</div>;
  
  if (error) {
    return (
      <div className="order-list-state text-error">
        <p>{error}</p>
        <button onClick={fetchOrders} className="btn-retry">Thử lại</button>
      </div>
    );
  }

  if (orders.length === 0) {
    return (
      <div className="order-list-empty">
        <p>Bạn chưa có đơn hàng nào.</p>
        <Link to="/customer/menu" className="btn-primary">Đặt món ngay</Link>
      </div>
    );
  }

  return (
    <div className="order-list-page">
      <h2>Lịch sử đơn hàng</h2>
      <div className="orders-table-wrapper">
        <table className="orders-table">
          <thead>
            <tr>
              <th>Mã đơn</th>
              <th>Ngày đặt</th>
              <th>Loại đơn</th>
              <th>Tổng tiền</th>
              <th>Trạng thái</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {orders.map(order => (
              <tr key={order.id}>
                <td>#{order.id}</td>
                <td>{formatDateTime(order.createdAt)}</td>
                <td>{order.orderType === 'DINE_IN' ? 'Tại quán' : (order.orderType === 'TAKEAWAY' ? 'Mang về' : 'Giao hàng')}</td>
                <td>{formatCurrency(order.totalAmount)}</td>
                <td>
                  <span 
                    className="status-badge"
                    style={{ backgroundColor: getOrderStatusColor(order.orderStatus) }}
                  >
                    {getOrderStatusLabel(order.orderStatus)}
                  </span>
                </td>
                <td>
                  <Link to={`/customer/orders/${order.id}`} className="btn-detail">Xem chi tiết</Link>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};
