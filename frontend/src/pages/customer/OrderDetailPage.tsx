import React, { useState, useEffect, useCallback } from 'react';
import { useParams, Link } from 'react-router-dom';
import { orderApi } from '../../api/orderApi';
import { getApiErrorMessage } from '../../utils/getApiErrorMessage';
import { formatCurrency } from '../../utils/formatCurrency';
import { formatDateTime } from '../../utils/formatDateTime';
import { getOrderStatusLabel, getOrderStatusColor } from '../../utils/orderStatus';
import type { OrderDetailResponse } from '../../types/order';
import './OrderDetailPage.css';

export const OrderDetailPage: React.FC = () => {
  const { orderId } = useParams<{ orderId: string }>();
  const [order, setOrder] = useState<OrderDetailResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [cancelling, setCancelling] = useState(false);

  const fetchOrder = useCallback(async () => {
    if (!orderId) return;
    setLoading(true);
    setError(null);
    try {
      const data = await orderApi.getOrderDetail(Number(orderId));
      setOrder(data);
    } catch (err) {
      setError(getApiErrorMessage(err, 'Không thể tải chi tiết đơn hàng.'));
    } finally {
      setLoading(false);
    }
  }, [orderId]);

  useEffect(() => {
    let mounted = true;
    const loadData = async () => {
      if (!orderId) return;
      setLoading(true);
      setError(null);
      try {
        const data = await orderApi.getOrderDetail(Number(orderId));
        if (mounted) setOrder(data);
      } catch (err) {
        if (mounted) setError(getApiErrorMessage(err, 'Không thể tải chi tiết đơn hàng.'));
      } finally {
        if (mounted) setLoading(false);
      }
    };
    loadData();
    return () => { mounted = false; };
  }, [orderId]);

  const handleCancelOrder = async () => {
    if (!order) return;
    const reason = window.prompt('Nhập lý do hủy đơn:');
    if (reason === null) return; // user cancelled prompt
    
    if (!reason.trim()) {
      alert('Vui lòng nhập lý do hủy.');
      return;
    }

    setCancelling(true);
    try {
      await orderApi.cancelOrder(order.id, { reason: reason.trim() });
      alert('Đã hủy đơn hàng thành công.');
      fetchOrder(); // reload
    } catch (err) {
      alert(getApiErrorMessage(err, 'Lỗi khi hủy đơn.'));
    } finally {
      setCancelling(false);
    }
  };

  if (loading) return <div className="order-detail-state">Đang tải chi tiết đơn...</div>;
  
  if (error || !order) {
    return (
      <div className="order-detail-state text-error">
        <p>{error || 'Không tìm thấy đơn hàng.'}</p>
        <Link to="/customer/orders" className="btn-secondary">Quay lại danh sách</Link>
      </div>
    );
  }

  // Cancel logic: We only block COMPLETED and CANCELLED as per backend mapping logic
  const canCancel = order.orderStatus !== 'COMPLETED' && order.orderStatus !== 'CANCELLED';

  return (
    <div className="order-detail-page">
      <div className="order-header-row">
        <h2>Chi tiết đơn hàng #{order.id}</h2>
        <Link to="/customer/orders" className="btn-back">Trở về</Link>
      </div>

      <div className="order-info-cards">
        <div className="info-card">
          <h3>Thông tin chung</h3>
          <p><strong>Ngày đặt:</strong> {formatDateTime(order.createdAt)}</p>
          <p>
            <strong>Trạng thái:</strong>{' '}
            <span 
              className="status-badge"
              style={{ backgroundColor: getOrderStatusColor(order.orderStatus) }}
            >
              {getOrderStatusLabel(order.orderStatus)}
            </span>
          </p>
          <p><strong>Loại đơn:</strong> {order.orderType === 'DINE_IN' ? 'Dùng tại quán' : (order.orderType === 'TAKEAWAY' ? 'Mang về' : 'Giao hàng')}</p>
          {order.orderType === 'DINE_IN' && <p><strong>Bàn ID:</strong> {order.tableId || 'Chưa chọn'}</p>}
          {order.note && <p><strong>Ghi chú:</strong> {order.note}</p>}
        </div>

        <div className="info-card">
          <h3>Thanh toán</h3>
          <p><strong>Tạm tính:</strong> {formatCurrency(order.subTotal)}</p>
          {order.discountAmount > 0 && (
            <p className="text-success">
              <strong>Giảm giá:</strong> -{formatCurrency(order.discountAmount)}
              {order.couponCode && ` (Mã: ${order.couponCode})`}
            </p>
          )}
          <p className="final-total"><strong>Tổng cộng:</strong> {formatCurrency(order.totalAmount)}</p>
        </div>
      </div>

      <div className="order-items-section">
        <h3>Món ăn đã đặt</h3>
        <table className="items-table">
          <thead>
            <tr>
              <th>Tên món</th>
              <th>Đơn giá</th>
              <th>Số lượng</th>
              <th>Thành tiền</th>
            </tr>
          </thead>
          <tbody>
            {order.items.map(item => (
              <tr key={item.id}>
                <td>
                  {item.foodName}
                  {item.note && <div className="item-note">Ghi chú: {item.note}</div>}
                </td>
                <td>{formatCurrency(item.unitPrice)}</td>
                <td>{item.quantity}</td>
                <td>{formatCurrency(item.unitPrice * item.quantity)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {canCancel && (
        <div className="order-actions">
          <button 
            className="btn-cancel-order" 
            onClick={handleCancelOrder}
            disabled={cancelling}
          >
            {cancelling ? 'Đang xử lý...' : 'Hủy đơn hàng'}
          </button>
          <p className="cancel-note">Chỉ có thể hủy đơn khi nhà hàng chưa hoàn thành món.</p>
        </div>
      )}
    </div>
  );
};
