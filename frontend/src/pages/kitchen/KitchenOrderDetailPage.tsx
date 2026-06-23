import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { kitchenApi } from '../../api/kitchenApi';
import { getApiErrorMessage } from '../../utils/getApiErrorMessage';
import { formatDateTime } from '../../utils/formatDateTime';
import { getOrderStatusLabel, getOrderStatusBadgeClass } from '../../utils/orderStatus';
import type { KitchenOrderResponse } from '../../types/kitchen';
import type { OrderStatus } from '../../types/order';
import './KitchenOrderDetailPage.css';

export const KitchenOrderDetailPage: React.FC = () => {
  const { orderId } = useParams<{ orderId: string }>();
  const navigate = useNavigate();

  const [order, setOrder] = useState<KitchenOrderResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const fetchOrder = useCallback(async (showLoading = true) => {
    if (!orderId) return;
    if (showLoading) setLoading(true);
    setError(null);
    try {
      const data = await kitchenApi.getOrderById(Number(orderId));
      setOrder(data);
    } catch (err) {
      setError(getApiErrorMessage(err, 'Lỗi khi tải chi tiết đơn bếp.'));
    } finally {
      setLoading(false);
    }
  }, [orderId]);

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchOrder(false);
  }, [fetchOrder]);

  const handleUpdateStatus = async (nextStatus: OrderStatus) => {
    if (!order || submitting) return;
    setSubmitting(true);
    setError(null);
    try {
      const updatedOrder = await kitchenApi.updateOrderStatus(order.id, { status: nextStatus });
      setOrder(updatedOrder);
      if (nextStatus === 'READY') {
        // Có thể navigate về queue luôn, hoặc ở lại
        navigate('/kitchen/queue');
      }
    } catch (err) {
      setError(getApiErrorMessage(err, 'Lỗi cập nhật trạng thái đơn.'));
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <div className="kitchen-page-state">Đang tải chi tiết đơn...</div>;
  if (error && !order) return <div className="kitchen-page-state error">{error}</div>;
  if (!order) return <div className="kitchen-page-state">Không tìm thấy thông tin đơn.</div>;

  return (
    <div className="kitchen-detail-page">
      <div className="detail-header">
        <button onClick={() => navigate('/kitchen/queue')} className="btn-back">
          &larr; Quay lại Hàng đợi
        </button>
        <button onClick={() => fetchOrder(true)} className="btn-secondary" disabled={loading} style={{ marginLeft: '1rem' }}>
          Tải lại
        </button>
        <div className="title-row">
          <h2>Chi tiết Món - Đơn #{order.id}</h2>
          <span className={`badge ${getOrderStatusBadgeClass(order.orderStatus)}`}>
            {getOrderStatusLabel(order.orderStatus)}
          </span>
        </div>
      </div>

      {error && <div className="error-message">{error}</div>}

      <div className="kitchen-meta-card">
        <div className="meta-info">
          <p><strong>Loại đơn:</strong> {order.orderType === 'DINE_IN' ? 'Tại bàn' : order.orderType === 'TAKEAWAY' ? 'Mang về' : 'Giao hàng'}</p>
          {order.tableId && <p><strong>Bàn:</strong> {order.tableId}</p>}
          <p><strong>Thời gian tạo:</strong> {formatDateTime(order.createdAt)}</p>
        </div>
        {order.note && (
          <div className="kitchen-note-large">
            <strong>GHI CHÚ TOÀN ĐƠN:</strong> {order.note}
          </div>
        )}
      </div>

      <div className="kitchen-items-large-list">
        <h3>Danh sách cần chế biến</h3>
        {order.items.map(item => (
          <div key={item.id} className="kitchen-large-item">
            <div className="item-qty-box">{item.quantity}</div>
            <div className="item-details">
              <div className="item-name-large">{item.foodName}</div>
              {item.note && <div className="item-note-large">Lưu ý: {item.note}</div>}
            </div>
          </div>
        ))}
      </div>

      <div className="kitchen-action-bar">
        {order.orderStatus === 'CONFIRMED' && (
          <button 
            className="btn-kitchen-action-large btn-prepare"
            onClick={() => handleUpdateStatus('PREPARING')}
            disabled={submitting}
          >
            Bắt đầu làm
          </button>
        )}

        {order.orderStatus === 'PREPARING' && (
          <button 
            className="btn-kitchen-action-large btn-ready"
            onClick={() => handleUpdateStatus('READY')}
            disabled={submitting}
          >
            Xong tất cả (Sẵn sàng)
          </button>
        )}

        {order.orderStatus === 'READY' && (
          <p className="status-note">Đơn đã sẵn sàng. Chờ nhân viên lấy.</p>
        )}
        
        {order.orderStatus !== 'CONFIRMED' && order.orderStatus !== 'PREPARING' && order.orderStatus !== 'READY' && (
          <p className="status-note">Không thể thao tác trên trạng thái {order.orderStatus}.</p>
        )}
      </div>
    </div>
  );
};
