import React, { useState, useEffect, useCallback } from 'react';
import { useParams, Link } from 'react-router-dom';
import { orderApi } from '../../api/orderApi';
import { paymentApi } from '../../api/paymentApi';
import { invoiceApi } from '../../api/invoiceApi';
import { getApiErrorMessage } from '../../utils/getApiErrorMessage';
import { formatCurrency } from '../../utils/formatCurrency';
import { formatDateTime } from '../../utils/formatDateTime';
import { getOrderStatusLabel, getOrderStatusColor } from '../../utils/orderStatus';
import { getPaymentMethodLabel } from '../../utils/paymentMethod';
import { getPaymentStatusLabel, getPaymentStatusColor } from '../../utils/paymentStatus';
import type { OrderDetailResponse } from '../../types/order';
import type { PaymentResponse, PaymentMethod } from '../../types/payment';
import type { InvoiceDetailResponse } from '../../types/invoice';
import './OrderDetailPage.css';

export const OrderDetailPage: React.FC = () => {
  const { orderId } = useParams<{ orderId: string }>();
  const [order, setOrder] = useState<OrderDetailResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [cancelling, setCancelling] = useState(false);

  // Payment states
  const [payment, setPayment] = useState<PaymentResponse | null>(null);
  const [paymentLoading, setPaymentLoading] = useState(false);
  const [paymentSubmitting, setPaymentSubmitting] = useState(false);
  const [paymentMethod, setPaymentMethod] = useState<PaymentMethod>('CASH');
  const [paymentReference, setPaymentReference] = useState('');

  // Invoice states
  const [invoice, setInvoice] = useState<InvoiceDetailResponse | null>(null);
  const [invoiceLoading, setInvoiceLoading] = useState(false);
  const [invoiceSubmitting, setInvoiceSubmitting] = useState(false);

  const fetchOrder = useCallback(async () => {
    if (!orderId) return;
    try {
      const data = await orderApi.getOrderDetail(Number(orderId));
      setOrder(data);
    } catch {
      // ignore here as it's only for refresh
    }
  }, [orderId]);

  useEffect(() => {
    let mounted = true;
    const loadData = async () => {
      if (!orderId) return;
      setLoading(true);
      setError(null);
      setPaymentLoading(true);
      
      try {
        const orderData = await orderApi.getOrderDetail(Number(orderId));
        if (!mounted) return;
        setOrder(orderData);

        // Fetch payments
        let paidPayment: PaymentResponse | null = null;
        try {
          const payments = await paymentApi.getPaymentsByOrderId(Number(orderId));
          if (payments.length > 0) {
            paidPayment = payments.find(p => p.paymentStatus === 'PAID') || payments[payments.length - 1];
            if (mounted) setPayment(paidPayment);
          }
        } catch {
          // ignore payment fetch error, assume no payment
        } finally {
          if (mounted) setPaymentLoading(false);
        }

        // Fetch invoice if paid
        if (paidPayment && paidPayment.paymentStatus === 'PAID') {
          if (mounted) setInvoiceLoading(true);
          try {
            const inv = await invoiceApi.getInvoiceByPaymentId(paidPayment.id);
            if (mounted) setInvoice(inv);
          } catch (e: unknown) {
            // 404 is expected if not generated
            const err = e as { response?: { status?: number } };
            if (err?.response?.status !== 404) {
              console.error('Lỗi khi tải hóa đơn:', e);
            }
          } finally {
            if (mounted) setInvoiceLoading(false);
          }
        }

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
      await fetchOrder(); // reload
    } catch (err) {
      alert(getApiErrorMessage(err, 'Lỗi khi hủy đơn.'));
    } finally {
      setCancelling(false);
    }
  };

  const handleConfirmPayment = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!order) return;
    setPaymentSubmitting(true);
    try {
      const res = await paymentApi.confirmPayment(order.id, {
        paymentMethod,
        amount: order.totalAmount,
        ...(paymentReference.trim() ? { paymentReference: paymentReference.trim() } : {})
      });
      setPayment(res);
      alert('Xác nhận thanh toán thành công!');
      await fetchOrder(); // refresh order to get CONFIRMED status
    } catch (err) {
      alert(getApiErrorMessage(err, 'Lỗi khi thanh toán.'));
    } finally {
      setPaymentSubmitting(false);
    }
  };

  const handleGenerateInvoice = async () => {
    if (!payment) return;
    setInvoiceSubmitting(true);
    try {
      const res = await invoiceApi.generateInvoice(payment.id);
      alert('Tạo hóa đơn thành công!');
      // fetch full detail
      const detail = await invoiceApi.getInvoiceById(res.id);
      setInvoice(detail);
    } catch (err) {
      alert(getApiErrorMessage(err, 'Lỗi khi tạo hóa đơn.'));
    } finally {
      setInvoiceSubmitting(false);
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
  
  // Payment logic
  const canPay = order.orderStatus !== 'COMPLETED' && order.orderStatus !== 'CANCELLED';

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
          <h3>Chi tiết giá</h3>
          <p><strong>Tạm tính:</strong> {formatCurrency(order.subTotal)}</p>
          {order.discountAmount > 0 && (
            <p className="text-success">
              <strong>Giảm giá:</strong> -{formatCurrency(order.discountAmount)}
              {order.couponCode && ` (Mã: ${order.couponCode})`}
            </p>
          )}
          <p className="final-total"><strong>Tổng cộng:</strong> {formatCurrency(order.totalAmount)}</p>
        </div>

        <div className="info-card">
          <h3>Thanh toán & Hóa đơn</h3>
          {paymentLoading ? (
            <p className="text-muted">Đang tải thông tin thanh toán...</p>
          ) : payment ? (
            <div className="payment-info">
              <p>
                <strong>Trạng thái:</strong>{' '}
                <span className="status-badge" style={{ backgroundColor: getPaymentStatusColor(payment.paymentStatus) }}>
                  {getPaymentStatusLabel(payment.paymentStatus)}
                </span>
              </p>
              <p><strong>Phương thức:</strong> {getPaymentMethodLabel(payment.paymentMethod)}</p>
              {payment.transactionCode && <p><strong>Mã GD:</strong> {payment.transactionCode}</p>}
              <p><strong>Đã thanh toán:</strong> {formatCurrency(payment.amount)}</p>
              
              {/* Invoice Section */}
              <div className="invoice-section">
                <hr />
                {invoiceLoading ? (
                  <p className="text-muted">Đang tải hóa đơn...</p>
                ) : invoice ? (
                  <div className="invoice-info">
                    <p><strong>Hóa đơn:</strong> {invoice.invoiceNumber}</p>
                    <p><strong>Ngày xuất:</strong> {formatDateTime(invoice.issuedAt)}</p>
                  </div>
                ) : (
                  payment.paymentStatus === 'PAID' && (
                    <button 
                      className="btn-secondary btn-sm" 
                      onClick={handleGenerateInvoice}
                      disabled={invoiceSubmitting}
                    >
                      {invoiceSubmitting ? 'Đang tạo...' : 'Xuất hóa đơn'}
                    </button>
                  )
                )}
              </div>
            </div>
          ) : (
            canPay ? (
              <form onSubmit={handleConfirmPayment} className="payment-form">
                <div className="form-group-sm">
                  <label>Phương thức:</label>
                  <select 
                    value={paymentMethod} 
                    onChange={(e) => setPaymentMethod(e.target.value as PaymentMethod)}
                    className="select-sm"
                  >
                    <option value="CASH">Tiền mặt</option>
                    <option value="QR">Chuyển khoản QR</option>
                    <option value="ONLINE_SIMULATION">Trực tuyến (Mô phỏng)</option>
                  </select>
                </div>
                <div className="form-group-sm">
                  <label>Mã giao dịch (tùy chọn):</label>
                  <input 
                    type="text" 
                    value={paymentReference} 
                    onChange={e => setPaymentReference(e.target.value)} 
                    placeholder="VD: VNPAY123"
                    className="input-sm"
                  />
                </div>
                <button type="submit" className="btn-primary btn-sm btn-block" disabled={paymentSubmitting}>
                  {paymentSubmitting ? 'Đang xử lý...' : `Xác nhận thanh toán ${formatCurrency(order.totalAmount)}`}
                </button>
              </form>
            ) : (
               <p className="text-muted">Đơn hàng hiện không thể thanh toán.</p>
            )
          )}
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
