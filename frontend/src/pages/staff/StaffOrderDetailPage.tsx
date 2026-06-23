import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { staffOrderApi } from '../../api/staffOrderApi';
import { getApiErrorMessage } from '../../utils/getApiErrorMessage';
import { formatCurrency } from '../../utils/formatCurrency';
import { formatDateTime } from '../../utils/formatDateTime';
import { getOrderStatusLabel, getOrderStatusBadgeClass } from '../../utils/orderStatus';
import type { StaffOrderResponse } from '../../types/staff';
import type { OrderStatus } from '../../types/order';
import './StaffOrderDetailPage.css';

export const StaffOrderDetailPage: React.FC = () => {
  const { orderId } = useParams<{ orderId: string }>();
  const navigate = useNavigate();

  const [order, setOrder] = useState<StaffOrderResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const fetchOrder = useCallback(async (showLoading = true) => {
    if (!orderId) return;
    if (showLoading) setLoading(true);
    setError(null);
    try {
      const data = await staffOrderApi.getOrderById(Number(orderId));
      setOrder(data);
    } catch (err) {
      setError(getApiErrorMessage(err, 'Lỗi khi tải chi tiết đơn hàng.'));
    } finally {
      setLoading(false);
    }
  }, [orderId]);

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchOrder(false);
  }, [fetchOrder]);

  const handleStatusChange = async (newStatus: OrderStatus) => {
    if (!order || submitting) return;
    
    // Confirm if cancelling or completing
    if (newStatus === 'CANCELLED') {
      if (!window.confirm('Bạn có chắc chắn muốn hủy đơn hàng này không?')) return;
    }
    
    setSubmitting(true);
    setError(null);
    try {
      const updatedOrder = await staffOrderApi.updateOrderStatus(order.id, { status: newStatus });
      setOrder(updatedOrder);
    } catch (err) {
      setError(getApiErrorMessage(err, 'Lỗi khi cập nhật trạng thái đơn hàng.'));
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <div className="staff-page-state">Đang tải thông tin đơn hàng...</div>;
  if (error && !order) return <div className="staff-page-state error">{error}</div>;
  if (!order) return <div className="staff-page-state">Không tìm thấy đơn hàng.</div>;

  return (
    <div className="staff-order-detail-page">
      <div className="detail-header">
        <button onClick={() => navigate('/staff/orders')} className="btn-back">
          &larr; Quay lại Danh sách
        </button>
        <button onClick={() => fetchOrder(true)} className="btn-secondary" disabled={loading} style={{ marginLeft: '1rem' }}>
          Tải lại
        </button>
        <h2>Chi tiết Đơn hàng #{order.id}</h2>
      </div>

      {error && <div className="error-message">{error}</div>}

      <div className="detail-grid">
        <div className="detail-card info-card">
          <h3>Thông tin chung</h3>
          <p><strong>Loại đơn:</strong> {order.orderType === 'DINE_IN' ? 'Tại bàn' : order.orderType === 'TAKEAWAY' ? 'Mang về' : 'Giao hàng'}</p>
          {order.tableId && <p><strong>Bàn:</strong> Bàn {order.tableId}</p>}
          <p>
            <strong>Trạng thái: </strong> 
            <span className={`badge ${getOrderStatusBadgeClass(order.orderStatus)}`}>
              {getOrderStatusLabel(order.orderStatus)}
            </span>
          </p>
          <p><strong>Thời gian tạo:</strong> {formatDateTime(order.createdAt)}</p>
          <p><strong>Cập nhật lần cuối:</strong> {formatDateTime(order.updatedAt)}</p>
          {order.note && (
            <div className="order-note-box">
              <strong>Ghi chú:</strong> {order.note}
            </div>
          )}
        </div>

        <div className="detail-card action-card">
          <h3>Thao tác nghiệp vụ</h3>
          <div className="action-buttons">
            {order.orderStatus === 'CONFIRMED' && (
              <>
                <button 
                  className="btn-action btn-prepare" 
                  onClick={() => handleStatusChange('PREPARING')}
                  disabled={submitting}
                >
                  Bắt đầu chuẩn bị
                </button>
                <button 
                  className="btn-action btn-cancel" 
                  onClick={() => handleStatusChange('CANCELLED')}
                  disabled={submitting}
                >
                  Hủy đơn
                </button>
              </>
            )}
            
            {order.orderStatus === 'READY' && (
              <button 
                className="btn-action btn-complete" 
                onClick={() => handleStatusChange('COMPLETED')}
                disabled={submitting}
              >
                Hoàn thành & Giao khách
              </button>
            )}
            
            {order.orderStatus !== 'CONFIRMED' && order.orderStatus !== 'READY' && (
              <p className="no-actions">Không có thao tác hợp lệ cho trạng thái hiện tại.</p>
            )}
          </div>
        </div>
      </div>

      <div className="detail-card items-card">
        <h3>Danh sách món ăn</h3>
        <div className="table-responsive">
          <table className="staff-table">
            <thead>
              <tr>
                <th>Tên món</th>
                <th>Ghi chú</th>
                <th className="text-right">Số lượng</th>
                <th className="text-right">Đơn giá</th>
                <th className="text-right">Thành tiền</th>
              </tr>
            </thead>
            <tbody>
              {order.items.map(item => (
                <tr key={item.id}>
                  <td><strong>{item.foodName}</strong></td>
                  <td>{item.note || '-'}</td>
                  <td className="text-right">{item.quantity}</td>
                  <td className="text-right">{formatCurrency(item.unitPrice)}</td>
                  <td className="text-right highlight-price">{formatCurrency(item.unitPrice * item.quantity)}</td>
                </tr>
              ))}
            </tbody>
            <tfoot>
              <tr>
                <td colSpan={4} className="text-right"><strong>Tạm tính:</strong></td>
                <td className="text-right highlight-price">{formatCurrency(order.subTotal)}</td>
              </tr>
              {order.discountAmount > 0 && (
                <tr>
                  <td colSpan={4} className="text-right"><strong>Giảm giá:</strong></td>
                  <td className="text-right highlight-price text-danger">-{formatCurrency(order.discountAmount)}</td>
                </tr>
              )}
              <tr>
                <td colSpan={4} className="text-right"><strong>Tổng cộng:</strong></td>
                <td className="text-right highlight-price total">{formatCurrency(order.totalAmount)}</td>
              </tr>
            </tfoot>
          </table>
        </div>
      </div>
    </div>
  );
};
