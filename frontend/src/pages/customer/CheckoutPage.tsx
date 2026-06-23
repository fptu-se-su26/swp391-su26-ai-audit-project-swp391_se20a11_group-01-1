import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCart } from '../../hooks/useCart';
import { tableApi } from '../../api/tableApi';
import { orderApi } from '../../api/orderApi';
import { couponApi } from '../../api/couponApi';
import { getApiErrorMessage } from '../../utils/getApiErrorMessage';
import { formatCurrency } from '../../utils/formatCurrency';
import type { TableResponse } from '../../types/table';
import type { OrderType, CheckoutRequest } from '../../types/order';
import type { CouponValidationResponse } from '../../types/coupon';
import './CheckoutPage.css';

export const CheckoutPage: React.FC = () => {
  const navigate = useNavigate();
  const { cart, isLoading: isCartLoading, subtotal, loadCart } = useCart();
  
  const [orderType, setOrderType] = useState<OrderType>('TAKEAWAY');
  const [tables, setTables] = useState<TableResponse[]>([]);
  const [selectedTableId, setSelectedTableId] = useState<number | ''>('');
  const [note, setNote] = useState('');
  
  const [couponCode, setCouponCode] = useState('');
  const [couponResult, setCouponResult] = useState<CouponValidationResponse | null>(null);
  const [couponError, setCouponError] = useState<string | null>(null);
  const [validatingCoupon, setValidatingCoupon] = useState(false);
  
  const [submitting, setSubmitting] = useState(false);
  const [tablesLoading, setTablesLoading] = useState(false);
  const [tablesError, setTablesError] = useState<string | null>(null);

  // If cart is empty, redirect back to menu/cart after loading check
  useEffect(() => {
    if (!isCartLoading && (!cart || cart.items.length === 0)) {
      alert('Giỏ hàng trống, không thể thanh toán.');
      navigate('/customer/cart');
    }
  }, [cart, isCartLoading, navigate]);

  // Fetch tables only when DINE_IN
  useEffect(() => {
    if (orderType === 'DINE_IN') {
      const fetchTables = async () => {
        setTablesLoading(true);
        setTablesError(null);
        try {
          const data = await tableApi.getAllTables();
          // Filter only AVAILABLE tables
          setTables(data.filter(t => t.status === 'AVAILABLE'));
        } catch (err) {
          setTablesError(getApiErrorMessage(err, 'Không thể tải danh sách bàn.'));
        } finally {
          setTablesLoading(false);
        }
      };
      fetchTables();
    }
  }, [orderType]);

  // Reset coupon preview if cart subtotal changes
  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setCouponResult(null);
    setCouponError(null);
  }, [subtotal]);

  const handleValidateCoupon = async () => {
    const code = couponCode.trim();
    if (!code) return;
    
    setValidatingCoupon(true);
    setCouponError(null);
    setCouponResult(null);
    try {
      const response = await couponApi.validateCoupon({ code, orderAmount: subtotal });
      setCouponResult(response);
    } catch (err: unknown) {
      setCouponError(getApiErrorMessage(err, 'Mã giảm giá không hợp lệ.'));
    } finally {
      setValidatingCoupon(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (orderType === 'DINE_IN' && !selectedTableId) {
      alert('Vui lòng chọn bàn.');
      return;
    }
    if (!cart || cart.items.length === 0) {
      alert('Giỏ hàng trống.');
      return;
    }

    setSubmitting(true);
    try {
      const payload: CheckoutRequest = {
        orderType,
        ...(orderType === 'DINE_IN' && selectedTableId ? { tableId: Number(selectedTableId) } : {}),
        ...(couponResult?.valid && couponResult.couponCode ? { couponCode: couponResult.couponCode.trim() } : {}),
        ...(note.trim() ? { note: note.trim() } : {})
      };

      const response = await orderApi.checkout(payload);

      if (!response.id) {
        throw new Error('Không nhận được mã đơn hàng từ hệ thống.');
      }

      alert('Đặt hàng thành công!');
      
      try {
        // Try to sync cart state, but ignore if it fails to not block UX
        await loadCart();
      } catch (e) {
        console.error('Không thể tải lại giỏ hàng', e);
      }
      
      navigate(`/customer/orders/${response.id}`, { replace: true });
    } catch (err: unknown) {
      alert(getApiErrorMessage(err, 'Lỗi khi đặt hàng.'));
      setSubmitting(false); // only re-enable if failed, if success we navigate away
    }
  };

  if (isCartLoading || (!cart && !isCartLoading)) return <div className="checkout-state">Đang tải...</div>;
  if (!cart || cart.items.length === 0) return null; // handled by useEffect

  const finalAmount = couponResult?.valid ? couponResult.finalAmount : subtotal;

  return (
    <div className="checkout-page">
      <h2>Thanh toán</h2>
      
      <form className="checkout-layout" onSubmit={handleSubmit}>
        <div className="checkout-form-section">
          <div className="form-group">
            <label>Loại đơn hàng</label>
            <div className="radio-group">
              <label>
                <input 
                  type="radio" 
                  name="orderType" 
                  value="TAKEAWAY" 
                  checked={orderType === 'TAKEAWAY'}
                  onChange={() => {
                    setOrderType('TAKEAWAY');
                    setSelectedTableId('');
                  }}
                /> Mang về (Takeaway)
              </label>
              <label>
                <input 
                  type="radio" 
                  name="orderType" 
                  value="DINE_IN" 
                  checked={orderType === 'DINE_IN'}
                  onChange={() => setOrderType('DINE_IN')}
                /> Dùng tại nhà hàng (Dine-in)
              </label>
            </div>
          </div>

          {orderType === 'DINE_IN' && (
            <div className="form-group">
              <label>Chọn bàn</label>
              {tablesLoading ? (
                <p>Đang tải danh sách bàn...</p>
              ) : tablesError ? (
                <p className="text-error">{tablesError}</p>
              ) : tables.length === 0 ? (
                <p className="text-warning">Hiện không có bàn nào trống.</p>
              ) : (
                <select 
                  value={selectedTableId} 
                  onChange={(e) => setSelectedTableId(e.target.value === '' ? '' : Number(e.target.value))}
                  required
                >
                  <option value="">-- Vui lòng chọn bàn --</option>
                  {tables.map(t => (
                    <option key={t.id} value={t.id}>
                      Bàn {t.tableNumber} (Sức chứa: {t.capacity}) - {t.location}
                    </option>
                  ))}
                </select>
              )}
            </div>
          )}

          <div className="form-group">
            <label>Ghi chú đơn hàng (Tùy chọn)</label>
            <textarea 
              rows={3} 
              placeholder="VD: Không hành, ít cay..."
              value={note}
              onChange={(e) => setNote(e.target.value)}
            />
          </div>
        </div>

        <div className="checkout-summary-section">
          <h3>Tóm tắt đơn hàng</h3>
          <div className="summary-items">
            {cart.items.map(item => (
              <div key={item.id} className="summary-item">
                <span className="summary-item-name">{item.quantity} x {item.foodName}</span>
                <span className="summary-item-price">{formatCurrency(item.subTotal)}</span>
              </div>
            ))}
          </div>

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
                disabled={validatingCoupon || submitting}
              />
              <button 
                type="button"
                onClick={handleValidateCoupon} 
                disabled={!couponCode.trim() || validatingCoupon || submitting}
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

          <button 
            type="submit" 
            className="btn-place-order" 
            disabled={submitting || (orderType === 'DINE_IN' && !selectedTableId) || (orderType === 'DINE_IN' && tables.length === 0)}
          >
            {submitting ? 'Đang xử lý...' : 'Xác nhận đặt hàng'}
          </button>
        </div>
      </form>
    </div>
  );
};
