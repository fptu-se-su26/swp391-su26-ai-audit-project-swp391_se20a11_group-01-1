import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCart } from '../../context/CartContext';
import { createOrder } from '../../services/orderService';
import { validateCoupon } from '../../services/couponService';
import { formatPrice } from '../../utils/helpers';

const PAYMENT_METHODS = [
  { id: 'cod',     icon: '💵', label: 'Thanh toán tại bàn' },
  { id: 'card',    icon: '💳', label: 'Thẻ tín dụng / Ghi nợ' },
  { id: 'momo',    icon: '🟣', label: 'Ví MoMo' },
  { id: 'zalopay', icon: '🔵', label: 'ZaloPay' },
  { id: 'vnpay',   icon: '🔴', label: 'VNPay QR' },
];

export default function Cart() {
  const { items, updateQuantity, removeItem, clearCart, coupon, setCoupon, totalAmount, discountAmount, finalAmount } = useCart();
  const navigate = useNavigate();

  const [step, setStep]           = useState('cart'); // cart | payment | success
  const [payMethod, setPayMethod] = useState('cod');
  const [orderNote, setOrderNote] = useState('');
  const [orderType, setOrderType] = useState('ONLINE');
  const [address, setAddress]     = useState('');
  const [couponCode, setCouponCode] = useState('');
  const [couponMsg, setCouponMsg] = useState(null);
  const [loading, setLoading]     = useState(false);

  const serviceFee = Math.round(totalAmount * 0.05);
  const grand      = finalAmount + serviceFee;

  const handleApplyCoupon = async () => {
    try {
      const { data } = await validateCoupon(couponCode, totalAmount);
      setCoupon(data);
      setCouponMsg({ ok: true, text: `✅ Áp dụng ${data.code} thành công!` });
    } catch (err) {
      setCouponMsg({ ok: false, text: `❌ ${err.response?.data?.message || 'Mã giảm giá không hợp lệ'}` });
    }
    setTimeout(() => setCouponMsg(null), 3000);
  };

  const handleOrder = async () => {
    setLoading(true);
    try {
      const { data } = await createOrder({
        orderType,
        items: items.map(i => ({ foodId: i.food.id, quantity: i.quantity })),
        deliveryAddress: orderType === 'ONLINE' ? address : null,
        couponCode: coupon?.code || null,
        note: orderNote,
      });
      clearCart();
      setStep('success');
    } catch (err) {
      alert(err.response?.data?.message || 'Đặt hàng thất bại');
    } finally { setLoading(false); }
  };

  if (items.length === 0 && step === 'cart') {
    return (
      <div className="max-w-lg mx-auto px-4 py-20 text-center text-gray-400">
        <div className="text-7xl mb-5">🛒</div>
        <h2 className="text-2xl font-bold text-gray-700 mb-2">Giỏ hàng trống</h2>
        <p className="mb-6">Hãy thêm món ăn vào giỏ hàng</p>
        <button onClick={() => navigate('/customer/menu')} className="bg-[#e85d04] hover:bg-[#c44d00] text-white font-bold px-6 py-3 rounded-xl">Xem thực đơn</button>
      </div>
    );
  }

  if (step === 'success') {
    const method = PAYMENT_METHODS.find(m => m.id === payMethod);
    return (
      <div className="max-w-lg mx-auto px-4 py-16 text-center">
        <div className="text-7xl mb-5">✅</div>
        <h2 className="text-2xl font-bold text-gray-800 mb-2">Đặt hàng thành công!</h2>
        <p className="text-gray-500 mb-6">Cảm ơn bạn đã đặt món tại <strong>Cái Gì Cũng Không Có</strong></p>
        <div className="bg-gray-50 rounded-2xl p-5 mb-6 text-left space-y-3">
          <div className="flex justify-between text-sm"><span className="text-gray-500">💳 Thanh toán</span><strong>{method?.icon} {method?.label}</strong></div>
          <div className="flex justify-between text-sm"><span className="text-gray-500">💰 Tổng tiền</span><strong className="text-[#e85d04]">{formatPrice(grand)}</strong></div>
          <div className="flex justify-between text-sm"><span className="text-gray-500">⏱ Thời gian chờ</span><strong>~20-30 phút</strong></div>
        </div>
        <div className="flex gap-3">
          <button onClick={() => navigate('/customer/orders')} className="flex-1 bg-[#e85d04] hover:bg-[#c44d00] text-white font-bold py-3 rounded-xl text-sm">📋 Xem đơn hàng</button>
          <button onClick={() => navigate('/customer/menu')} className="flex-1 bg-gray-100 hover:bg-gray-200 text-gray-700 font-bold py-3 rounded-xl text-sm">🍽️ Đặt thêm</button>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-5xl mx-auto px-4 py-8">
      {step === 'payment' && (
        <button onClick={() => setStep('cart')} className="flex items-center gap-1 text-sm text-gray-500 hover:text-gray-800 mb-4">← Quay lại giỏ hàng</button>
      )}
      <h1 className="text-2xl font-bold text-gray-800 mb-6">{step === 'cart' ? 'Giỏ hàng' : 'Thanh toán'}</h1>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Left */}
        <div className="lg:col-span-2 space-y-4">
          {step === 'cart' ? (
            <>
              {items.map(item => (
                <div key={item.food.id} className="bg-white rounded-2xl p-4 shadow-sm flex gap-4">
                  <div className="w-20 h-20 rounded-xl overflow-hidden bg-gray-100 shrink-0">
                    {item.food.imageUrl ? <img src={item.food.imageUrl} alt={item.food.name} className="w-full h-full object-cover" /> : <div className="w-full h-full flex items-center justify-center text-3xl">🍽️</div>}
                  </div>
                  <div className="flex-1">
                    <h3 className="font-semibold text-gray-800">{item.food.name}</h3>
                    <p className="text-[#e85d04] font-bold text-sm">{formatPrice(item.food.price)}</p>
                  </div>
                  <div className="flex items-center gap-2">
                    <button onClick={() => updateQuantity(item.food.id, item.quantity - 1)} className="w-8 h-8 rounded-full border flex items-center justify-center hover:bg-gray-100">−</button>
                    <span className="w-6 text-center font-semibold">{item.quantity}</span>
                    <button onClick={() => updateQuantity(item.food.id, item.quantity + 1)} className="w-8 h-8 rounded-full border flex items-center justify-center hover:bg-gray-100">+</button>
                  </div>
                  <div className="text-right">
                    <p className="font-bold text-gray-800">{formatPrice(Number(item.food.price) * item.quantity)}</p>
                    <button onClick={() => removeItem(item.food.id)} className="text-xs text-red-400 hover:text-red-600 mt-1">🗑️</button>
                  </div>
                </div>
              ))}
            </>
          ) : (
            <div className="bg-white rounded-2xl p-5 shadow-sm">
              <h3 className="font-bold text-gray-700 mb-4">Chọn phương thức thanh toán</h3>
              <div className="space-y-2 mb-5">
                {PAYMENT_METHODS.map(m => (
                  <label key={m.id} className={`flex items-center gap-3 p-3 rounded-xl border cursor-pointer transition ${payMethod === m.id ? 'border-[#e85d04] bg-orange-50' : 'border-gray-200 hover:border-gray-300'}`}>
                    <input type="radio" name="payment" value={m.id} checked={payMethod === m.id} onChange={() => setPayMethod(m.id)} className="accent-[#e85d04]" />
                    <span className="text-xl">{m.icon}</span>
                    <span className="text-sm font-medium text-gray-700">{m.label}</span>
                    {payMethod === m.id && <span className="ml-auto text-[#e85d04] font-bold">✓</span>}
                  </label>
                ))}
              </div>
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-1.5">📝 Ghi chú đơn hàng</label>
                <textarea rows={2} value={orderNote} onChange={e => setOrderNote(e.target.value)}
                  placeholder="Yêu cầu đặc biệt..." className="w-full px-3 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-[#e85d04] resize-none" />
              </div>
            </div>
          )}
        </div>

        {/* Summary */}
        <div className="space-y-4">
          {/* Order type */}
          {step === 'cart' && (
            <div className="bg-white rounded-2xl p-5 shadow-sm">
              <h3 className="font-bold text-gray-700 mb-3">Hình thức</h3>
              {[{ value:'ONLINE', label:'🚀 Giao hàng tận nơi' }, { value:'DINE_IN', label:'🍽️ Ăn tại quán' }].map(t => (
                <label key={t.value} className={`flex items-center gap-2 p-3 rounded-xl border cursor-pointer mb-2 transition ${orderType === t.value ? 'border-[#e85d04] bg-orange-50' : 'border-gray-200'}`}>
                  <input type="radio" value={t.value} checked={orderType === t.value} onChange={() => setOrderType(t.value)} className="accent-[#e85d04]" />
                  <span className="text-sm font-medium">{t.label}</span>
                </label>
              ))}
              {orderType === 'ONLINE' && (
                <input type="text" value={address} onChange={e => setAddress(e.target.value)}
                  placeholder="Địa chỉ giao hàng..." required
                  className="w-full mt-2 px-3 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-[#e85d04]" />
              )}
            </div>
          )}

          {/* Coupon */}
          {step === 'cart' && (
            <div className="bg-white rounded-2xl p-5 shadow-sm">
              <h3 className="font-bold text-gray-700 mb-3">Mã voucher</h3>
              {coupon ? (
                <div className="flex items-center justify-between bg-green-50 border border-green-200 rounded-xl px-3 py-2">
                  <span className="text-green-600 font-semibold text-sm">✓ {coupon.code}</span>
                  <button onClick={() => { setCoupon(null); setCouponCode(''); }} className="text-xs text-gray-400 hover:text-red-400">✕</button>
                </div>
              ) : (
                <div className="flex gap-2">
                  <input value={couponCode} onChange={e => setCouponCode(e.target.value.toUpperCase())}
                    placeholder="Nhập mã..." className="flex-1 px-3 py-2 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-1 focus:ring-[#e85d04]" />
                  <button onClick={handleApplyCoupon} className="bg-[#e85d04] text-white px-3 py-2 rounded-xl text-sm font-semibold hover:bg-[#c44d00]">Áp dụng</button>
                </div>
              )}
              {couponMsg && <p className={`text-xs mt-2 ${couponMsg.ok ? 'text-green-600' : 'text-red-500'}`}>{couponMsg.text}</p>}
            </div>
          )}

          {/* Total */}
          <div className="bg-white rounded-2xl p-5 shadow-sm">
            <h3 className="font-bold text-gray-700 mb-3">Tóm tắt</h3>
            <div className="space-y-2 text-sm mb-4">
              <div className="flex justify-between text-gray-500"><span>Tạm tính</span><span>{formatPrice(totalAmount)}</span></div>
              <div className="flex justify-between text-gray-500"><span>Phí dịch vụ (5%)</span><span>{formatPrice(serviceFee)}</span></div>
              {discountAmount > 0 && <div className="flex justify-between text-green-500"><span>🎁 Giảm giá</span><span>−{formatPrice(discountAmount)}</span></div>}
              <div className="flex justify-between font-extrabold text-lg border-t pt-2 text-gray-800">
                <span>Tổng cộng</span><span className="text-[#e85d04]">{formatPrice(grand)}</span>
              </div>
            </div>
            {step === 'cart' ? (
              <button onClick={() => setStep('payment')} className="w-full bg-[#e85d04] hover:bg-[#c44d00] text-white font-bold py-3 rounded-xl text-sm">💳 Tiến hành thanh toán</button>
            ) : (
              <button onClick={handleOrder} disabled={loading} className="w-full bg-[#e85d04] hover:bg-[#c44d00] disabled:opacity-60 text-white font-bold py-3 rounded-xl text-sm flex items-center justify-center gap-2">
                {loading ? <span className="spinner"></span> : (payMethod === 'cod' ? '✅ Xác nhận đặt hàng' : '💳 Thanh toán ngay')}
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
