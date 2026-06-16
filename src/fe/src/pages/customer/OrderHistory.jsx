import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getMyOrders } from '../../services/orderService';
import Loading from '../../components/common/Loading';
import { formatPrice, formatDate } from '../../utils/helpers';

const STATUS_LABEL = {
  PENDING: { label: 'Chờ xác nhận', color: 'text-yellow-500' },
  CONFIRMED: { label: 'Đã xác nhận', color: 'text-blue-500' },
  PREPARING: { label: 'Đang chuẩn bị', color: 'text-orange-500' },
  READY: { label: 'Sẵn sàng', color: 'text-green-500' },
  DELIVERING: { label: 'Đang giao', color: 'text-purple-500' },
  COMPLETED: { label: 'Hoàn thành', color: 'text-green-600' },
  CANCELLED: { label: 'Đã hủy', color: 'text-red-400' },
};

const OrderHistory = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getMyOrders().then(({ data }) => setOrders(data)).finally(() => setLoading(false));
  }, []);

  if (loading) return <Loading />;

  return (
    <div className="max-w-3xl mx-auto px-4 py-10">
      <h1 className="text-3xl font-bold text-gray-800 mb-8">Lịch sử đơn hàng</h1>
      {orders.length === 0 ? (
        <div className="text-center py-16 text-gray-400">
          <div className="text-6xl mb-4">📋</div>
          <p className="text-lg font-medium">Chưa có đơn hàng nào</p>
          <Link to="/menu" className="mt-4 inline-block bg-orange-500 text-white px-6 py-3 rounded-xl font-semibold hover:bg-orange-600">
            Đặt ngay
          </Link>
        </div>
      ) : (
        <div className="space-y-4">
          {orders.map((order) => {
            const s = STATUS_LABEL[order.status] || { label: order.status, color: 'text-gray-500' };
            return (
              <Link key={order.id} to={`/orders/${order.id}`}
                className="block bg-white rounded-2xl p-5 shadow-sm hover:shadow-md transition-shadow">
                <div className="flex items-center justify-between mb-3">
                  <div>
                    <span className="font-bold text-gray-800">Đơn #{order.id}</span>
                    <span className="ml-3 text-xs text-gray-400">{formatDate(order.createdAt)}</span>
                  </div>
                  <span className={`text-sm font-semibold ${s.color}`}>{s.label}</span>
                </div>
                <div className="flex items-center justify-between text-sm">
                  <span className="text-gray-500">
                    {order.orderType === 'ONLINE' ? '🚀 Giao hàng' : '🍽️ Ăn tại quán'} •{' '}
                    {order.items?.length} món
                  </span>
                  <span className="font-bold text-orange-500">{formatPrice(order.finalAmount)}</span>
                </div>
              </Link>
            );
          })}
        </div>
      )}
    </div>
  );
};

export default OrderHistory;
