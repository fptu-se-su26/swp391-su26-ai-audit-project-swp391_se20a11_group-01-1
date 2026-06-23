import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCart } from '../../hooks/useCart';
import { couponApi } from '../../api/couponApi';
import { getApiErrorMessage } from '../../utils/getApiErrorMessage';
import { formatCurrency } from '../../utils/formatCurrency';
import type { CouponValidationResponse } from '../../types/coupon';
import './CartPage.css';

export const CartPage: React.FC = () => {
  const navigate = useNavigate();
  const { cart, isLoading, error, updateQuantity, removeItem, clearCart, subtotal, loadCart } = useCart();
  
  const [couponCode, setCouponCode] = useState('');
  const [couponResult, setCouponResult] = useState<CouponValidationResponse | null>(null);
  const [couponError, setCouponError] = useState<string | null>(null);
  const [validatingCoupon, setValidatingCoupon] = useState(false);

  // Reset coupon preview if cart subtotal changes (to avoid stale discount)
  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setCouponResult(null);
    setCouponError(null);
  }, [subtotal]);

  const handleUpdateQuantity = async (itemId: number, newQuantity: number) => {
    if (newQuantity < 1) return; // Backend requirement
    try {
      await updateQuantity(itemId, newQuantity);
    } catch (err) {
      const e = err as Error;
      alert(e.message || 'Không thể cập nhật số lượng');
    }
  };

  const handleRemoveItem = async (itemId: number) => {
    if (window.confirm('Bạn có chắc muốn xóa món này khỏi giỏ hàng?')) {
      try {
        await removeItem(itemId);
      } catch (err) {
        const e = err as Error;
        alert(e.message || 'Không thể xóa món');
      }
    }
  };

  const handleClearCart = async () => {
    if (window.confirm('Bạn có chắc muốn xóa toàn bộ giỏ hàng?')) {
      try {
        await clearCart();
      } catch (err) {
        const e = err as Error;
        alert(e.message || 'Không thể xóa giỏ hàng');
      }
    }
  };

  const handleValidateCoupon = async () => {
    const code = couponCode.trim();
    if (!code) return;
    
    setValidatingCoupon(true);
    setCouponError(null);
    setCouponResult(null);
    try {
      const response = await couponApi.validateCoupon({ code, orderAmount: subtotal });
      setCouponResult(response);
    } catch (err) {
      setCouponError(getApiErrorMessage(err, 'Mã giảm giá không hợp lệ.'));
    } finally {
      setValidatingCoupon(false);
    }
  };

  if (isLoading && !cart) return <div className="cart-page-state">Đang tải giỏ hàng...</div>;
  if (error) return (
    <div className="cart-page-state error">
      <p>{error}</p>
      <button onClick={() => loadCart()} className="btn-retry">Thử lại</button>
    </div>
  );

  if (!cart || cart.items.length === 0) {
    return (
      <div className="cart-page-empty">
        <h2>Giỏ hàng của bạn</h2>
        <p>Giỏ hàng đang trống.</p>
        <button className="btn-continue" onClick={() => navigate('/customer/menu')}>Tiếp tục chọn món</button>
      </div>
    );
  }

  const finalAmount = couponResult?.valid ? couponResult.finalAmount : subtotal;

  return (
    <div className="cart-page">
      <h2>Giỏ hàng của bạn</h2>
      
      <div className="cart-layout">
        <div className="cart-items">
          <table className="cart-table">
            <thead>
              <tr>
                <th>Món ăn</th>
                <th>Đơn giá</th>
                <th>Số lượng</th>
                <th>Thành tiền</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {cart.items.map(item => (
                <tr key={item.id}>
                  <td className="item-info">
                    {item.foodImageUrl ? (
                      <img src={item.foodImageUrl} alt={item.foodName} className="item-image" />
                    ) : (
                      <div className="item-no-image">No IMG</div>
                    )}
                    <span className="item-name">{item.foodName}</span>
                  </td>
                  <td>{formatCurrency(item.unitPrice)}</td>
                  <td>
                    <div className="quantity-control">
                      <button 
                        onClick={() => handleUpdateQuantity(item.id, item.quantity - 1)}
                        disabled={item.quantity <= 1 || isLoading}
                      >-</button>
                      <span className="quantity-text">{item.quantity}</span>
                      <button 
                        onClick={() => handleUpdateQuantity(item.id, item.quantity + 1)}
                        disabled={isLoading}
                      >+</button>
                    </div>
                  </td>
                  <td className="item-subtotal">{formatCurrency(item.subTotal)}</td>
                  <td>
                    <button 
                      className="btn-remove" 
                      onClick={() => handleRemoveItem(item.id)}
                      disabled={isLoading}
                    >
                      Xóa
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          
          <div className="cart-actions-left">
            <button className="btn-clear" onClick={handleClearCart} disabled={isLoading}>
              Xóa toàn bộ
            </button>
            <button className="btn-continue" onClick={() => navigate('/customer/menu')}>
              Thêm món khác
            </button>
          </div>
        </div>

        <div className="cart-summary">
          <h3>Tổng đơn hàng</h3>
          <div className="summary-row">
            <span>Tạm tính:</span>
            <span>{formatCurrency(subtotal)}</span>
          </div>
          
          <div className="coupon-section">
            <h4>Mã giảm giá</h4>
            <div className="coupon-input-group">
              <input 
                type="text" 
                placeholder="Nhập mã coupon" 
                value={couponCode}
                onChange={(e) => setCouponCode(e.target.value)}
                disabled={isLoading || validatingCoupon}
              />
              <button 
                onClick={handleValidateCoupon} 
                disabled={!couponCode.trim() || validatingCoupon || isLoading}
              >
                {validatingCoupon ? 'Đang kiểm tra...' : 'Áp dụng'}
              </button>
            </div>
            {couponError && <div className="coupon-error">{couponError}</div>}
            {couponResult?.valid && (
              <div className="coupon-success">
                Mã giảm giá hợp lệ! Đã giảm: {formatCurrency(couponResult.discountAmount)}
              </div>
            )}
          </div>
          
          <div className="summary-row final-total">
            <span>Tổng cộng:</span>
            <span>{formatCurrency(finalAmount)}</span>
          </div>

          <button className="btn-checkout" disabled>
            Tiếp tục thanh toán — sẽ phát triển ở FE-05
          </button>
        </div>
      </div>
    </div>
  );
};
