import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { kitchenApi } from '../../api/kitchenApi';
import { getApiErrorMessage } from '../../utils/getApiErrorMessage';
import { formatDateTime } from '../../utils/formatDateTime';
import { getOrderStatusLabel, getOrderStatusBadgeClass } from '../../utils/orderStatus';
import type { KitchenOrderResponse } from '../../types/kitchen';
import type { OrderStatus } from '../../types/order';
import './KitchenQueuePage.css';

export const KitchenQueuePage: React.FC = () => {
  const [orders, setOrders] = useState<KitchenOrderResponse[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [updatingId, setUpdatingId] = useState<number | null>(null);

  const navigate = useNavigate();

  const fetchOrders = useCallback(async (showLoading = true) => {
    if (showLoading) setLoading(true);
    setError(null);
    try {
      const data = await kitchenApi.getActiveOrders();
      setOrders(data);
    } catch (err) {
      setError(getApiErrorMessage(err, 'Lỗi khi tải hàng đợi bếp.'));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchOrders(false);
  }, [fetchOrders]);

  const handleUpdateStatus = async (orderId: number, nextStatus: OrderStatus) => {
    if (updatingId) return;
    setUpdatingId(orderId);
    try {
      const updatedOrder = await kitchenApi.updateOrderStatus(orderId, { status: nextStatus });
      // Update in place or refresh
      setOrders(prev => {
        // If it's READY, some systems want to keep it in the queue until staff completes it.
        // The getActiveOrders API returns CONFIRMED, PREPARING, READY. 
        // So we just update the item.
        return prev.map(o => o.id === orderId ? updatedOrder : o);
      });
    } catch (err) {
      alert(getApiErrorMessage(err, 'Lỗi cập nhật trạng thái đơn.'));
    } finally {
      setUpdatingId(null);
    }
  };

  return (
    <div className="kitchen-queue-page">
      <div className="page-header">
        <h2>Hàng đợi Bếp</h2>
        <button onClick={() => fetchOrders(true)} className="btn-refresh" disabled={loading}>
          {loading ? 'Đang làm mới...' : 'Làm mới'}
        </button>
      </div>

      {error && <div className="error-message">{error}</div>}

      {loading && orders.length === 0 ? (
        <div className="loading-state">Đang tải hàng đợi...</div>
      ) : orders.length === 0 ? (
        <div className="empty-state">Hàng đợi trống. Tuyệt vời!</div>
      ) : (
        <div className="kitchen-grid">
          {[...orders].sort((a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()).map(order => (
            <div className={`kitchen-card ${order.orderStatus.toLowerCase()}`} key={order.id}>
              <div className="kitchen-card-header">
                <h3>Đơn #{order.id}</h3>
                <span className={`badge ${getOrderStatusBadgeClass(order.orderStatus)}`}>
                  {getOrderStatusLabel(order.orderStatus)}
                </span>
              </div>
              <div className="kitchen-card-meta">
                <p><strong>Loại:</strong> {order.orderType === 'DINE_IN' ? 'Tại bàn' : order.orderType === 'TAKEAWAY' ? 'Mang về' : 'Giao hàng'}</p>
                {order.tableId && <p><strong>Bàn:</strong> {order.tableId}</p>}
                <p><strong>Thời gian:</strong> {formatDateTime(order.createdAt)}</p>
              </div>

              {order.note && (
                <div className="kitchen-order-note">
                  <strong>Ghi chú đơn:</strong> {order.note}
                </div>
              )}

              <div className="kitchen-items-list">
                {order.items.map(item => (
                  <div key={item.id} className="kitchen-item">
                    <div className="item-main">
                      <span className="item-qty">{item.quantity}x</span>
                      <span className="item-name">{item.foodName}</span>
                    </div>
                    {item.note && <div className="item-note">Ghi chú: {item.note}</div>}
                  </div>
                ))}
              </div>

              <div className="kitchen-card-actions">
                <button 
                  className="btn-kitchen-view"
                  onClick={() => navigate(`/kitchen/orders/${order.id}`)}
                >
                  Chi tiết
                </button>
                
                {order.orderStatus === 'CONFIRMED' && (
                  <button 
                    className="btn-kitchen-action btn-prepare"
                    onClick={() => handleUpdateStatus(order.id, 'PREPARING')}
                    disabled={updatingId === order.id}
                  >
                    Bắt đầu làm
                  </button>
                )}

                {order.orderStatus === 'PREPARING' && (
                  <button 
                    className="btn-kitchen-action btn-ready"
                    onClick={() => handleUpdateStatus(order.id, 'READY')}
                    disabled={updatingId === order.id}
                  >
                    Xong (Sẵn sàng)
                  </button>
                )}
                
                {order.orderStatus === 'READY' && (
                  <p className="status-note">Chờ NV phục vụ</p>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
