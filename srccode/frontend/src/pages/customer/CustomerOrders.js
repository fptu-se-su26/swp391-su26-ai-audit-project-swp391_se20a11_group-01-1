import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import './CustomerOrders.css';

const statusMap = {
  PENDING:   { label: 'Chờ xác nhận',      cls: 'status-pending',   icon: '⏳' },
  CONFIRMED: { label: 'Đã xác nhận',        cls: 'status-serving',   icon: '✅' },
  PREPARING: { label: 'Đang chế biến',      cls: 'status-serving',   icon: '👨‍🍳' },
  READY:     { label: 'Sẵn sàng phục vụ',   cls: 'status-serving',   icon: '🍽️' },
  COMPLETED: { label: 'Hoàn thành',          cls: 'status-done',      icon: '✅' },
  CANCELLED: { label: 'Đã hủy',             cls: 'status-cancelled', icon: '❌' },
};

// Những trạng thái chưa thanh toán (đang active)
const UNPAID_STATUSES = ['PENDING', 'CONFIRMED', 'PREPARING', 'READY'];

function PaymentModal({ order, onClose, onPaid }) {
  const [method, setMethod] = useState('cod');
  const [loading, setLoading] = useState(false);
  const total = Number(order.totalAmount || 0);

  const handleConfirm = async () => {
    setLoading(true);
    // TODO: gọi API cập nhật trạng thái thanh toán khi backend có
    await new Promise(r => setTimeout(r, 600)); // giả lập delay
    setLoading(false);
    onPaid(order.orderId, method);
    onClose();
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="payment-modal card" onClick={e => e.stopPropagation()}>
        <h2 className="pay-modal-title">💳 Thanh toán đơn hàng</h2>
        <p className="pay-modal-order">
          {order.orderCode || `#${order.orderId}`}
        </p>
        <p className="pay-modal-total">
          {total.toLocaleString('vi-VN')}<span>đ</span>
        </p>

        <div className="pay-methods">
          <label className={`pay-method-option ${method === 'cod' ? 'selected' : ''}`}>
            <input type="radio" name="pay" value="cod"
              checked={method === 'cod'} onChange={() => setMethod('cod')} />
            <span className="pay-method-icon">💵</span>
            <div>
              <p className="pay-method-name">Tiền mặt</p>
              <p className="pay-method-desc">Thanh toán trực tiếp tại bàn</p>
            </div>
            {method === 'cod' && <span className="pay-check">✓</span>}
          </label>

          <label className={`pay-method-option ${method === 'qr' ? 'selected' : ''}`}>
            <input type="radio" name="pay" value="qr"
              checked={method === 'qr'} onChange={() => setMethod('qr')} />
            <span className="pay-method-icon">📱</span>
            <div>
              <p className="pay-method-name">Quét mã QR</p>
              <p className="pay-method-desc">MoMo, ZaloPay, VNPay, Banking</p>
            </div>
            {method === 'qr' && <span className="pay-check">✓</span>}
          </label>
        </div>

        {method === 'qr' && (
          <div className="qr-display">
            <div className="qr-mock-box">
              <div className="qr-mock-inner">QR</div>
            </div>
            <p className="qr-hint">Quét mã bằng app ngân hàng hoặc ví điện tử</p>
            <p className="qr-amount">{total.toLocaleString('vi-VN')}đ</p>
          </div>
        )}

        {method === 'cod' && (
          <div className="cod-note">
            <p>📢 Vui lòng gọi nhân viên để thanh toán trực tiếp tại bàn.</p>
          </div>
        )}

        <div className="pay-modal-btns">
          <button className="btn-primary pay-confirm-btn"
            onClick={handleConfirm} disabled={loading}>
            {loading ? 'Đang xử lý...' : '✅ Xác nhận thanh toán'}
          </button>
          <button className="btn-cancel" onClick={onClose}>Hủy</button>
        </div>
      </div>
    </div>
  );
}

function CustomerOrders() {
  const [orders, setOrders] = useState([]);
  const [expanded, setExpanded] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [payingOrder, setPayingOrder] = useState(null); // order đang mở modal

  const navigate = useNavigate();
  const { user } = useAuth();

  useEffect(() => {
    fetchCustomerOrders();
  }, [user]);

  const getApiMessage = (data, fallback) => {
    if (!data) return fallback;
    if (typeof data === 'string') return data;
    if (typeof data === 'object') return data.message || data.error || data.detail || fallback;
    return fallback;
  };

  const fetchCustomerOrders = async () => {
    if (!user?.userId) {
      setLoading(false);
      setError('Bạn cần đăng nhập để xem đơn hàng');
      return;
    }
    setLoading(true);
    setError('');
    try {
      const response = await API.get(`/orders/customer/${user.userId}`);
      setOrders(response.data || []);
    } catch (err) {
      setError(getApiMessage(err.response?.data, 'Không thể tải danh sách đơn hàng'));
    } finally {
      setLoading(false);
    }
  };

  const handlePaid = (orderId, method) => {
    // Cập nhật local state — sau này gọi API
    setOrders(prev => prev.map(o =>
      o.orderId === orderId ? { ...o, status: 'COMPLETED', paidMethod: method } : o
    ));
  };

  const formatDateTime = (value) => {
    if (!value) return '';
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) return value;
    return date.toLocaleString('vi-VN', {
      hour: '2-digit', minute: '2-digit',
      day: '2-digit', month: '2-digit', year: 'numeric'
    });
  };

  const formatMoney = (value) => Number(value || 0).toLocaleString('vi-VN');

  const getStatusInfo = (status) =>
    statusMap[status] || { label: status || 'Không xác định', cls: 'status-pending', icon: 'ℹ️' };

  // Tìm các order chưa thanh toán
  const unpaidOrders = orders.filter(o => UNPAID_STATUSES.includes(o.status));
  const totalUnpaid = unpaidOrders.reduce((sum, o) => sum + Number(o.totalAmount || 0), 0);

  if (loading) {
    return (
      <div className="cust-orders">
        <h1 className="page-title">Lịch sử đơn hàng</h1>
        <div className="no-orders">
          <div style={{ fontSize: 48, marginBottom: 12 }}>⏳</div>
          <p>Đang tải đơn hàng...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="cust-orders">
        <h1 className="page-title">Lịch sử đơn hàng</h1>
        <div className="no-orders">
          <div style={{ fontSize: 48, marginBottom: 12 }}>⚠️</div>
          <p>{error}</p>
          <button className="btn-primary" onClick={fetchCustomerOrders} style={{ marginTop: 16 }}>
            Thử lại
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="cust-orders">
      <h1 className="page-title">Lịch sử đơn hàng</h1>

      {/* Banner thanh toán — chỉ hiện khi có order chưa trả */}
      {unpaidOrders.length > 0 && (
        <div className="payment-banner">
          <div className="payment-banner-left">
            <span className="payment-banner-icon">🔔</span>
            <div>
              <p className="payment-banner-title">
                Bạn có {unpaidOrders.length} đơn hàng chưa thanh toán
              </p>
              <p className="payment-banner-amount">
                Tổng: <strong>{totalUnpaid.toLocaleString('vi-VN')}đ</strong>
              </p>
            </div>
          </div>
          <button
            className="payment-banner-btn"
            onClick={() => setPayingOrder(unpaidOrders[0])}
          >
            💳 Thanh toán ngay
          </button>
        </div>
      )}

      {orders.length === 0 ? (
        <div className="no-orders">
          <div style={{ fontSize: 48, marginBottom: 12 }}>📋</div>
          <p>Bạn chưa có đơn hàng nào</p>
          <button className="btn-primary" onClick={() => navigate('/customer/menu')}
            style={{ marginTop: 16 }}>
            Xem thực đơn
          </button>
        </div>
      ) : (
        <div className="orders-list">
          {orders.map((order) => {
            const statusInfo = getStatusInfo(order.status);
            const isUnpaid = UNPAID_STATUSES.includes(order.status);

            return (
              <div key={order.orderId} className="cust-order-card card">
                <div className="cust-order-header"
                  onClick={() => setExpanded(expanded === order.orderId ? null : order.orderId)}>
                  <div>
                    <span className="cust-order-id">
                      {order.orderCode || `#${order.orderId}`}
                    </span>
                    <span className="cust-order-date">{formatDateTime(order.createdAt)}</span>
                  </div>
                  <div className="cust-order-right">
                    <span className={`status-badge ${statusInfo.cls}`}>
                      {statusInfo.icon} {statusInfo.label}
                    </span>
                    <span className="cust-order-total">{formatMoney(order.totalAmount)}đ</span>
                    {/* Nút thanh toán nhỏ kế bên từng đơn */}
                    {isUnpaid && (
                      <button
                        className="pay-inline-btn"
                        onClick={(e) => { e.stopPropagation(); setPayingOrder(order); }}
                      >
                        💳 TT
                      </button>
                    )}
                    <span className="expand-icon">
                      {expanded === order.orderId ? '▲' : '▼'}
                    </span>
                  </div>
                </div>

                {expanded === order.orderId && (
                  <div className="cust-order-detail">
                    <div className="order-items-list">
                      {order.items?.map((item) => (
                        <div key={item.orderItemId} className="order-item-row">
                          <span>
                            {item.emoji ? `${item.emoji} ` : ''}
                            {item.foodName} × {item.quantity}
                          </span>
                          <span>{formatMoney(item.subtotal)}đ</span>
                        </div>
                      ))}
                      {order.note && (
                        <div className="order-item-row">
                          <span>📝 Ghi chú</span>
                          <span>{order.note}</span>
                        </div>
                      )}
                      <div className="order-item-row order-total-row">
                        <span>Tổng cộng</span>
                        <strong>{formatMoney(order.totalAmount)}đ</strong>
                      </div>
                    </div>

                    {isUnpaid && (
                      <button className="pay-detail-btn"
                        onClick={() => setPayingOrder(order)}>
                        💳 Thanh toán đơn này
                      </button>
                    )}

                    {order.status === 'COMPLETED' && (
                      <button className="review-btn"
                        onClick={() => navigate('/customer/feedback')}>
                        ⭐ Đánh giá đơn hàng
                      </button>
                    )}
                  </div>
                )}
              </div>
            );
          })}
        </div>
      )}

      {/* Modal thanh toán */}
      {payingOrder && (
        <PaymentModal
          order={payingOrder}
          onClose={() => setPayingOrder(null)}
          onPaid={handlePaid}
        />
      )}
    </div>
  );
}

export default CustomerOrders;
