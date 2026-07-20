
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../../services/api';
import { useCart } from '../../context/CartContext';
import { useProfile } from '../../context/ProfileContext';
import { useAuth } from '../../context/AuthContext';
import './Cart.css';

const PAYMENT_METHODS = [
  { id: 'cod', icon: '💵', label: 'Tiền mặt tại bàn' },
  { id: 'qr',  icon: '📱', label: 'Quét mã QR' },
];

function Cart() {
  const { items, updateQty, removeItem, totalPrice, clearCart } = useCart();
  const { addSpend } = useProfile();
  const { user } = useAuth();

  const navigate = useNavigate();

  const [payMethod, setPayMethod] = useState('cod');
  const [step, setStep] = useState('cart'); // cart | payment | success
  
  const [step, setStep] = useState('cart'); // cart | success
  const [orderNote, setOrderNote] = useState('');
  const [orderLoading, setOrderLoading] = useState(false);
  const [orderError, setOrderError] = useState('');
  const [createdOrder, setCreatedOrder] = useState(null);

  const getItemId = (item) => {
    return item.foodId || item.id;
  };

  const getItemName = (item) => {
    return item.foodName || item.name || item.title || 'Món ăn';
  };

  const getItemImage = (item) => {
    return item.img || item.emoji || '🍽️';
  };

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

  const buildOrderItems = () => {
    return items.map((item) => ({
      foodId: getItemId(item),
      quantity: item.qty
    }));
  };

  const handleOrder = async () => {
    setOrderError('');

    if (!user?.userId) {
      setOrderError('Bạn cần đăng nhập để đặt hàng');
      return;
    }

    if (items.length === 0) {
      setOrderError('Giỏ hàng đang trống');
      return;
    }

    const orderItems = buildOrderItems();
    const invalidItem = orderItems.find((item) => !item.foodId || item.quantity <= 0);

    if (invalidItem) {
      setOrderError('Giỏ hàng có món không hợp lệ');
      return;
    }

    setOrderLoading(true);

    try {
      const noteParts = [];

      if (selectedPayment) {
        noteParts.push(`Payment method: ${selectedPayment.label}`);
      }

      const response = await API.post('/orders', {
      if (orderNote.trim()) noteParts.push(orderNote.trim());
      

      const response = await API.post('/orders', {
        userId: user.userId,
        note: noteParts.join(' | '),
        items: orderItems
      });
      
      if (activeVoucher) useVoucher(activeVoucher.code);
      addSpend(finalTotal);
      clearCart();

      setCreatedOrder(response.data);
      setStep('success');
    } catch (error) {
      console.error('Create order error:', error);
      setOrderError(
        getApiMessage(error.response?.data, 'Không thể đặt hàng. Vui lòng thử lại.')
      );
    } finally {
      setOrderLoading(false);
    }
  };

  // ── Empty cart ──────────────────────────────────────────
  if (items.length === 0 && step === 'cart') {
    return (
      <div className="cart-empty">
        <div className="cart-empty-icon">🛒</div>
        <h2>Giỏ hàng trống</h2>
        <p>Hãy thêm món ăn vào giỏ hàng</p>

        <button className="btn-primary" onClick={() => navigate('/customer/menu')}>
          Xem thực đơn
        </button>
      </div>
    );
  }

  // ── Success screen ──────────────────────────────────────
  if (step === 'success') {
    return (
      <div className="order-success">
        <div className="success-anim">✅</div>
        <h2>Đặt món thành công!</h2>
        <p>Cảm ơn bạn đã đặt món tại <strong>Cái Gì Cũng Không Có</strong></p>
        <div className="success-info-box">
          {createdOrder?.orderCode && (
            <div className="success-row">
              <span>🧾 Mã đơn</span>
              <strong>{createdOrder.orderCode}</strong>
            </div>
          )}
          <div className="success-row">
            <span>💰 Tổng tiền</span>
            <strong style={{ color: '#e85d04' }}>
              {createdOrder?.totalAmount != null
                ? `${Number(createdOrder.totalAmount).toLocaleString('vi-VN')}đ`
                : '—'}
            </strong>
          </div>
          <div className="success-row">
            <span>⏱ Thời gian chờ</span>
            <strong>~20-30 phút</strong>
          </div>
        </div>
        <p className="success-note">
          🔔 Nhân viên sẽ phục vụ bạn sớm nhất. Khi muốn thanh toán, vào mục <strong>Đơn hàng</strong> để thanh toán.
        </p>
        <div className="success-btns">
          <button className="btn-primary" onClick={() => navigate('/customer/orders')}>
            📋 Xem đơn hàng
          </button>
          <button className="btn-secondary" onClick={() => navigate('/customer/menu')}>
            🍽️ Đặt thêm
          </button>
        </div>
      </div>
    );
  }

  
  if (step === 'payment') {
    return (
      <div className="cart-page">
        <div className="cart-step-header">
          <button className="back-step-btn" onClick={() => setStep('cart')}>
            ← Quay lại
          </button>

          <h1 className="page-title">Thanh toán</h1>
        </div>

        <div className="cart-layout">
          <div className="payment-section card">
            <h3>Chọn phương thức thanh toán</h3>

            <div className="payment-methods">
              {PAYMENT_METHODS.map((method) => (
                <label
                  key={method.id}
                  className={`payment-option ${payMethod === method.id ? 'selected' : ''}`}
                >
                  <input
                    type="radio"
                    name="payment"
                    value={method.id}
                    checked={payMethod === method.id}
                    onChange={() => setPayMethod(method.id)}
                  />

                  <span className="pay-icon">{method.icon}</span>
                  <span className="pay-label">{method.label}</span>

                  {payMethod === method.id && <span className="pay-check">✓</span>}
                </label>
              ))}
            </div>

            {payMethod === 'card' && (
              <div className="card-form">
                <div className="form-group">
                  <label className="form-label">Số thẻ</label>

                  <input
                    className="form-input"
                    placeholder="1234 5678 9012 3456"
                    maxLength={19}
                  />
                </div>

                <div className="form-row-2">
                  <div className="form-group">
                    <label className="form-label">Ngày hết hạn</label>

                    <input className="form-input" placeholder="MM/YY" maxLength={5} />
                  </div>

                  <div className="form-group">
                    <label className="form-label">CVV</label>

                    <input
                      className="form-input"
                      placeholder="123"
                      maxLength={3}
                      type="password"
                    />
                  </div>
                </div>
              </div>
            )}

            {(payMethod === 'momo' || payMethod === 'zalopay' || payMethod === 'vnpay') && (
              <div className="qr-placeholder">
                <div className="qr-box">
                  <div className="qr-mock">
                    <div className="qr-inner">QR</div>
                  </div>

                  <p>Quét mã QR bằng app {PAYMENT_METHODS.find((m) => m.id === payMethod)?.label}</p>
                  <p className="qr-amount">Tạm tính: {totalPrice.toLocaleString('vi-VN')}đ</p>
                </div>
              </div>
            )}

            <div className="form-group" style={{ marginTop: 16 }}>
              <label className="form-label">📝 Ghi chú đơn hàng</label>

              <textarea
                className="form-input"
                rows={2}
                placeholder="Yêu cầu đặc biệt, dị ứng thực phẩm..."
                value={orderNote}
                onChange={(e) => setOrderNote(e.target.value)}
              />
            </div>
          </div>

          <div className="cart-summary card">
            <h2>Tóm tắt đơn hàng</h2>

            <div className="summary-items-mini">
              {items.map((item) => (
                <div key={getItemId(item)} className="summary-mini-row">
                  <span>
                    {getItemName(item)} × {item.qty}
                  </span>

                  <span>
                    {(item.price * item.qty).toLocaleString('vi-VN')}đ
                  </span>
                </div>
              ))}
            </div>

            <div className="summary-divider"></div>

            <div className="summary-row summary-total">
              <span>Tạm tính theo giỏ hàng</span>
              <span>{totalPrice.toLocaleString('vi-VN')}đ</span>
            </div>

            {orderError && <p className="voucher-msg error">❌ {orderError}</p>}

            <button className="order-btn" onClick={handleOrder} disabled={orderLoading}>
              {orderLoading
                ? 'Đang tạo đơn...'
                : payMethod === 'cod'
                  ? '✅ Xác nhận đặt hàng'
                  : '💳 Thanh toán ngay'}
            </button>
          </div>
        </div>
      </div>
    );
  }

 
  return (
    <div className="cart-page">
      <h1 className="page-title">Giỏ hàng</h1>

      <div className="cart-layout">
        <div className="cart-items">
          {items.map((item) => (
            <div key={getItemId(item)} className="cart-item card">
              <div className="cart-item-img">{getItemImage(item)}</div>

              <div className="cart-item-info">
                <h3>{getItemName(item)}</h3>

                <p className="cart-item-price">
                  {item.price.toLocaleString('vi-VN')}đ
                </p>
              </div>

              <div className="cart-qty">
                <button onClick={() => updateQty(getItemId(item), item.qty - 1)}>−</button>
                <span>{item.qty}</span>
                <button onClick={() => updateQty(getItemId(item), item.qty + 1)}>+</button>
              </div>

              <div className="cart-item-total">
                {(item.price * item.qty).toLocaleString('vi-VN')}đ
              </div>

              <button className="cart-remove" onClick={() => removeItem(getItemId(item))}>
                🗑️
              </button>
            </div>
          ))}
        </div>

        <div className="cart-summary card">
          <h2>Tóm tắt đơn hàng</h2>

          <div className="summary-divider"></div>

          <div className="summary-row summary-total">
            <span>Tạm tính theo giỏ hàng</span>
            <span>{totalPrice.toLocaleString('vi-VN')}đ</span>
          </div>

          <div className="form-group" style={{ marginTop: 8 }}>
            <label className="form-label">📝 Ghi chú</label>
            <textarea
              className="form-input"
              rows={2}
              placeholder="Yêu cầu đặc biệt, dị ứng thực phẩm..."
              value={orderNote}
              onChange={(e) => setOrderNote(e.target.value)}
            />
          </div>

          {orderError && <p className="voucher-msg error">❌ {orderError}</p>}

          <button className="order-btn" onClick={handleOrder} disabled={orderLoading}>
            {orderLoading ? 'Đang đặt món...' : '🍽️ Đặt món ngay'}
          </button>

          <button className="back-btn" onClick={() => navigate('/customer/menu')}>
            ← Tiếp tục mua
          </button>
        </div>
      </div>
    </div>
  );
}

export default Cart;

