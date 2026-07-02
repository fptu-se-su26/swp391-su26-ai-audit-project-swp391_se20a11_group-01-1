
import React, { useEffect, useState } from 'react';
import API from '../../services/api';
import './StaffOrders.css';

const statusFlow = {
  PENDING: 'CONFIRMED',
  READY: 'COMPLETED'
};

const statusMap = {
  PENDING: {
    label: 'Chờ xác nhận',
    cls: 'status-pending'
  },
  CONFIRMED: {
    label: 'Đã xác nhận',
    cls: 'status-serving'
  },
  PREPARING: {
    label: 'Đang chế biến',
    cls: 'status-serving'
  },
  READY: {
    label: 'Sẵn sàng phục vụ',
    cls: 'status-serving'
  },
  COMPLETED: {
    label: 'Hoàn thành',
    cls: 'status-done'
  },
  CANCELLED: {
    label: 'Đã hủy',
    cls: 'status-cancelled'
  }
};

const FILTERS = [
  ['all', 'Tất cả'],
  ['PENDING', 'Chờ xác nhận'],
  ['CONFIRMED', 'Đã xác nhận'],
  ['PREPARING', 'Đang chế biến'],
  ['READY', 'Sẵn sàng'],
  ['COMPLETED', 'Hoàn thành'],
  ['CANCELLED', 'Đã hủy']
];

function InvoiceModal({ order, onClose }) {
  const serviceFee = Math.round(Number(order.totalAmount || 0) * 0.05);
  const grandTotal = Number(order.totalAmount || 0) + serviceFee;

  const formatMoney = (value) => {
    return Number(value || 0).toLocaleString('vi-VN');
  };

  const formatDateTime = (value) => {
    if (!value) return '';

    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
      return value;
    }

    return date.toLocaleString('vi-VN', {
      hour: '2-digit',
      minute: '2-digit',
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    });
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="invoice-modal card" onClick={(e) => e.stopPropagation()}>
        <div className="invoice-header">
          <h2>🍜 Cái Gì Cũng Không Có</h2>
          <p>123 Đường ABC, Quận 1, TP.HCM</p>
          <p>ĐT: 028-xxxx-xxxx</p>

          <div className="invoice-divider"></div>

          <h3>HÓA ĐƠN THANH TOÁN</h3>

          <p>
            Mã đơn: <strong>{order.orderCode || `#${order.orderId}`}</strong>
          </p>

          <p>
            Khách hàng: <strong>{order.username || 'Customer'}</strong>
          </p>

          <p>{formatDateTime(order.createdAt)}</p>
        </div>

        <div className="invoice-divider"></div>

        <table className="invoice-table">
          <thead>
            <tr>
              <th>Món</th>
              <th>SL</th>
              <th>Đơn giá</th>
              <th>T.Tiền</th>
            </tr>
          </thead>

          <tbody>
            {order.items?.map((item) => (
              <tr key={item.orderItemId}>
                <td>{item.foodName}</td>
                <td>{item.quantity}</td>
                <td>{formatMoney(item.unitPrice)}đ</td>
                <td>{formatMoney(item.subtotal)}đ</td>
              </tr>
            ))}
          </tbody>
        </table>

        <div className="invoice-divider"></div>

        <div className="invoice-totals">
          <div className="inv-row">
            <span>Tạm tính</span>
            <span>{formatMoney(order.totalAmount)}đ</span>
          </div>

          <div className="inv-row">
            <span>Phí dịch vụ dự kiến (5%)</span>
            <span>{formatMoney(serviceFee)}đ</span>
          </div>

          <div className="inv-row inv-grand">
            <span>TỔNG CỘNG</span>
            <strong>{formatMoney(grandTotal)}đ</strong>
          </div>
        </div>

        {order.note && (
          <>
            <div className="invoice-divider"></div>
            <p>
              <strong>Ghi chú:</strong> {order.note}
            </p>
          </>
        )}

        <div className="invoice-divider"></div>

        <p className="invoice-thanks">Cảm ơn quý khách! Hẹn gặp lại 🙏</p>

        <div className="invoice-actions">
          <button className="print-real-btn" onClick={() => window.print()}>
            🖨️ In hóa đơn
          </button>

          <button className="modal-cancel" onClick={onClose}>
            Đóng
          </button>
        </div>
      </div>
    </div>
  );
}

function StaffOrders() {
  const [orders, setOrders] = useState([]);
  const [filter, setFilter] = useState('all');
  const [showInvoice, setShowInvoice] = useState(null);

  const [loading, setLoading] = useState(true);
  const [actionLoadingId, setActionLoadingId] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchOrders();
  }, []);

  const getApiMessage = (data, fallback) => {
    if (!data) return fallback;

    if (typeof data === 'string') {
      return data;
    }

    if (typeof data === 'object') {
      return data.message || data.error || data.detail || fallback;
    }

    return fallback;
  };

  const fetchOrders = async () => {
    setLoading(true);
    setError('');

    try {
      const response = await API.get('/orders');
      setOrders(response.data || []);
    } catch (error) {
      console.error('Fetch staff orders error:', error);

      setError(
        getApiMessage(
          error.response?.data,
          'Không thể tải danh sách đơn hàng'
        )
      );
    } finally {
      setLoading(false);
    }
  };

  const updateOrderStatus = async (orderId, status) => {
    setActionLoadingId(orderId);
    setError('');

    try {
      const response = await API.put(`/orders/${orderId}/status`, {
        status
      });

      setOrders((prev) =>
        prev.map((order) =>
          order.orderId === orderId ? response.data : order
        )
      );
    } catch (error) {
      console.error('Update order status error:', error);

      setError(
        getApiMessage(
          error.response?.data,
          'Không thể cập nhật trạng thái đơn hàng'
        )
      );
    } finally {
      setActionLoadingId(null);
    }
  };

  const cancelOrder = async (orderId) => {
    const confirmCancel = window.confirm('Bạn có chắc muốn hủy đơn này không?');

    if (!confirmCancel) {
      return;
    }

    setActionLoadingId(orderId);
    setError('');

    try {
      await API.delete(`/orders/${orderId}`);

      setOrders((prev) =>
        prev.map((order) =>
          order.orderId === orderId
            ? {
                ...order,
                status: 'CANCELLED'
              }
            : order
        )
      );
    } catch (error) {
      console.error('Cancel order error:', error);

      setError(
        getApiMessage(
          error.response?.data,
          'Không thể hủy đơn hàng'
        )
      );
    } finally {
      setActionLoadingId(null);
    }
  };

  const formatMoney = (value) => {
    return Number(value || 0).toLocaleString('vi-VN');
  };

  const formatTime = (value) => {
    if (!value) return '';

    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
      return value;
    }

    return date.toLocaleTimeString('vi-VN', {
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const getStatusInfo = (status) => {
    return (
      statusMap[status] || {
        label: status || 'Không xác định',
        cls: 'status-pending'
      }
    );
  };

  const getActionLabel = (status) => {
    if (status === 'PENDING') {
      return '✓ Xác nhận';
    }

    if (status === 'READY') {
      return '✓ Hoàn thành';
    }

    return '✓ Cập nhật';
  };

  const filteredOrders =
    filter === 'all'
      ? orders
      : orders.filter((order) => order.status === filter);

  if (loading) {
    return (
      <div className="staff-orders">
        <div className="page-header">
          <h1 className="page-title">Đơn hàng</h1>
        </div>

        <div className="card" style={{ padding: 24, textAlign: 'center' }}>
          ⏳ Đang tải đơn hàng...
        </div>
      </div>
    );
  }

  return (
    <div className="staff-orders">
      <div className="page-header">
        <h1 className="page-title">Đơn hàng</h1>

        <button className="btn-primary" onClick={fetchOrders}>
          🔄 Làm mới
        </button>
      </div>

      <div className="filter-tabs" style={{ marginBottom: 16 }}>
        {FILTERS.map(([value, label]) => (
          <button
            key={value}
            className={`filter-tab ${filter === value ? 'active' : ''}`}
            onClick={() => setFilter(value)}
          >
            {label}
          </button>
        ))}
      </div>

      {error && (
        <div className="card" style={{ padding: 16, marginBottom: 16, color: '#dc2626' }}>
          ⚠️ {error}
        </div>
      )}

      {filteredOrders.length === 0 ? (
        <div className="card" style={{ padding: 24, textAlign: 'center' }}>
          📋 Không có đơn hàng nào
        </div>
      ) : (
        <div className="staff-orders-list">
          {filteredOrders.map((order) => {
            const statusInfo = getStatusInfo(order.status);
            const nextStatus = statusFlow[order.status];
            const isActionLoading = actionLoadingId === order.orderId;

            return (
              <div key={order.orderId} className="staff-order-card card">
                <div className="sorder-left">
                  <span className="sorder-id">
                    {order.orderCode || `#${order.orderId}`}
                  </span>

                  <span className="sorder-table">
                    👤 {order.username || 'Customer'}
                  </span>

                  <span className="sorder-time">
                    🕐 {formatTime(order.createdAt)}
                  </span>

                  {order.email && (
                    <span className="sorder-waiter">
                      ✉️ {order.email}
                    </span>
                  )}
                </div>

                <div className="sorder-items">
                  {order.items?.map((item) => (
                    <span key={item.orderItemId} className="sorder-tag">
                      {item.emoji ? `${item.emoji} ` : ''}
                      {item.foodName} × {item.quantity}
                    </span>
                  ))}

                  {order.note && (
                    <span className="sorder-tag">
                      📝 {order.note}
                    </span>
                  )}
                </div>

                <div className="sorder-right">
                  <span className="sorder-total">
                    {formatMoney(order.totalAmount)}đ
                  </span>

                  <span className={`status-badge ${statusInfo.cls}`}>
                    {statusInfo.label}
                  </span>

                  <div className="sorder-actions">
                    {nextStatus && (
                      <button
                        className="advance-btn"
                        disabled={isActionLoading}
                        onClick={() => updateOrderStatus(order.orderId, nextStatus)}
                      >
                        {isActionLoading ? 'Đang xử lý...' : getActionLabel(order.status)}
                      </button>
                    )}

                    {order.status !== 'COMPLETED' && order.status !== 'CANCELLED' && (
                      <button
                        className="cancel-order-btn"
                        disabled={isActionLoading}
                        onClick={() => cancelOrder(order.orderId)}
                      >
                        ✕
                      </button>
                    )}

                    <button className="print-btn" onClick={() => setShowInvoice(order)}>
                      🖨️ In HĐ
                    </button>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {showInvoice && (
        <InvoiceModal
          order={showInvoice}
          onClose={() => setShowInvoice(null)}
        />
      )}
    </div>
  );
}

export default StaffOrders;

