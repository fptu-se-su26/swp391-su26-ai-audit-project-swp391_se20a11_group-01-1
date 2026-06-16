import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getMyOrders } from '../../services/orderService';
import { formatPrice, formatDate } from '../../utils/helpers';

const STATUS_MAP = {
  PENDING:   { label: 'Chờ xác nhận', cls: 'bg-yellow-100 text-yellow-700', icon: '⏳' },
  CONFIRMED: { label: 'Đã xác nhận',  cls: 'bg-blue-100 text-blue-600',    icon: '✅' },
  PREPARING: { label: 'Đang chuẩn bị',cls: 'bg-orange-100 text-orange-600',icon: '🔥' },
  READY:     { label: 'Sẵn sàng',     cls: 'bg-green-100 text-green-600',  icon: '🍽️' },
  DELIVERING:{ label: 'Đang giao',    cls: 'bg-purple-100 text-purple-600',icon: '🚀' },
  COMPLETED: { label: 'Hoàn thành',   cls: 'bg-green-200 text-green-700',  icon: '✅' },
  CANCELLED: { label: 'Đã hủy',       cls: 'bg-red-100 text-red-500',      icon: '❌' },
};

export default function CustomerOrders() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [expanded, setExpanded] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    getMyOrders().then(({ data }) => setOrders(data)).finally(() => setLoading(false));
  }, []);

  if (!loading && orders.length === 0) {
    return (
      <div className="max-w-lg mx-auto px-4 py-20 text-center text-gray-400">
        <div className="text-7xl mb-5">📋</div>
        <h2 className="text-2xl font-bold text-gray-700 mb-2">Bạn chưa có đơn hàng nào</h2>
        <button onClick={() => navigate('/customer/menu')} className="mt-4 bg-[#e85d04] hover:bg-[#c44d00] text-white font-bold px-6 py-3 rounded-xl text-sm">Xem thực đơn</button>
      </div>
    );
  }

  return (
    <div className="max-w-3xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold text-gray-800 mb-6">Lịch sử đơn hàng</h1>
      <div className="space-y-4">
        {orders.map(order => {
          const s = STATUS_MAP[order.status] || { label: order.status, cls: 'bg-gray-100 text-gray-500', icon: '?' };
          return (
            <div key={order.id} className="bg-white rounded-2xl shadow-sm overflow-hidden">
              <div className="flex items-center gap-4 px-5 py-4 cursor-pointer" onClick={() => setExpanded(expanded === order.id ? null : order.id)}>
                <div>
                  <p className="font-bold text-[#e85d04]">{order.id}</p>
                  <p className="text-xs text-gray-400">{formatDate(order.createdAt)}</p>
                </div>
                <div className="flex-1">
                  <span className={`text-xs font-semibold px-2.5 py-1 rounded-full ${s.cls}`}>{s.icon} {s.label}</span>
                </div>
                <div className="text-right">
                  <p className="font-bold text-gray-800">{formatPrice(order.finalAmount)}</p>
                  <p className="text-xs text-gray-400">{order.orderType === 'ONLINE' ? '🚀 Giao hàng' : '🍽️ Ăn tại quán'}</p>
                </div>
                <span className="text-gray-400 text-xs">{expanded === order.id ? '▲' : '▼'}</span>
              </div>

              {expanded === order.id && (
                <div className="border-t px-5 py-4">
                  <div className="space-y-2 mb-3">
                    {order.items?.map((item, i) => (
                      <div key={i} className="flex justify-between text-sm">
                        <span className="text-gray-700">{item.foodName} × {item.quantity}</span>
                        <span className="text-[#e85d04] font-semibold">{formatPrice(item.subtotal)}</span>
                      </div>
                    ))}
                  </div>
                  <div className="flex justify-between font-bold text-sm border-t pt-2">
                    <span>Tổng cộng</span><span className="text-[#e85d04]">{formatPrice(order.finalAmount)}</span>
                  </div>
                  {order.status === 'COMPLETED' && (
                    <button onClick={() => navigate('/customer/feedback')} className="mt-3 bg-orange-50 hover:bg-orange-100 text-[#e85d04] font-semibold text-sm px-4 py-2 rounded-xl">
                      ⭐ Đánh giá đơn hàng
                    </button>
                  )}
                </div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
}
