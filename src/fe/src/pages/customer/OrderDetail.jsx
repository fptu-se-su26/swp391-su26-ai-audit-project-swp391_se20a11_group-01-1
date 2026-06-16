import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { getOrderById } from '../../services/orderService';
import Loading from '../../components/common/Loading';
import { formatPrice, formatDate } from '../../utils/helpers';

const STATUS_LABEL = {
  PENDING: { label: 'Chờ xác nhận', color: 'bg-yellow-100 text-yellow-600' },
  CONFIRMED: { label: 'Đã xác nhận', color: 'bg-blue-100 text-blue-600' },
  PREPARING: { label: 'Đang chuẩn bị', color: 'bg-orange-100 text-orange-600' },
  READY: { label: 'Sẵn sàng', color: 'bg-green-100 text-green-600' },
  DELIVERING: { label: 'Đang giao', color: 'bg-purple-100 text-purple-600' },
  COMPLETED: { label: 'Hoàn thành', color: 'bg-green-100 text-green-700' },
  CANCELLED: { label: 'Đã hủy', color: 'bg-red-100 text-red-500' },
};

const OrderDetail = () => {
  const { id } = useParams();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getOrderById(id).then(({ data }) => setOrder(data)).finally(() => setLoading(false));
  }, [id]);

  if (loading) return <Loading />;
  if (!order) return <div className="text-center py-20 text-gray-400">Không tìm thấy đơn hàng</div>;

  const statusInfo = STATUS_LABEL[order.status] || { label: order.status, color: 'bg-gray-100 text-gray-500' };

  return (
    <div className="max-w-3xl mx-auto px-4 py-10">
      <div className="flex items-center gap-3 mb-6">
        <Link to="/orders" className="text-gray-400 hover:text-orange-500">← Lịch sử đơn hàng</Link>
      </div>
      <div className="bg-white rounded-3xl shadow-sm p-8">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h1 className="text-2xl font-bold text-gray-800">Đơn hàng #{order.id}</h1>
            <p className="text-sm text-gray-400">{formatDate(order.createdAt)}</p>
          </div>
          <span className={`text-sm font-semibold px-3 py-1.5 rounded-full ${statusInfo.color}`}>
            {statusInfo.label}
          </span>
        </div>

        {/* Info */}
        <div className="grid grid-cols-2 gap-4 mb-6 text-sm">
          <div>
            <p className="text-gray-400">Loại đơn</p>
            <p className="font-medium">{order.orderType === 'ONLINE' ? '🚀 Giao hàng' : '🍽️ Ăn tại quán'}</p>
          </div>
          <div>
            <p className="text-gray-400">Thanh toán</p>
            <p className={`font-medium ${order.paymentStatus === 'PAID' ? 'text-green-500' : 'text-red-400'}`}>
              {order.paymentStatus === 'PAID' ? '✓ Đã thanh toán' : 'Chưa thanh toán'}
            </p>
          </div>
          {order.deliveryAddress && (
            <div className="col-span-2">
              <p className="text-gray-400">Địa chỉ giao hàng</p>
              <p className="font-medium">{order.deliveryAddress}</p>
            </div>
          )}
          {order.tableNumber && (
            <div>
              <p className="text-gray-400">Bàn</p>
              <p className="font-medium">{order.tableNumber}</p>
            </div>
          )}
        </div>

        {/* Items */}
        <div className="border-t pt-5 mb-5">
          <h3 className="font-semibold text-gray-700 mb-3">Món đã đặt</h3>
          <div className="space-y-3">
            {order.items?.map((item) => (
              <div key={item.id} className="flex items-center gap-3">
                <img src={item.foodImageUrl || 'https://placehold.co/48x48?text=F'}
                  alt={item.foodName} className="w-12 h-12 rounded-lg object-cover" />
                <div className="flex-1">
                  <p className="font-medium text-gray-800">{item.foodName}</p>
                  <p className="text-sm text-gray-400">x{item.quantity} × {formatPrice(item.unitPrice)}</p>
                </div>
                <p className="font-semibold text-orange-500">{formatPrice(item.subtotal)}</p>
              </div>
            ))}
          </div>
        </div>

        {/* Total */}
        <div className="border-t pt-4 space-y-2 text-sm">
          <div className="flex justify-between text-gray-500">
            <span>Tạm tính</span><span>{formatPrice(order.totalAmount)}</span>
          </div>
          {Number(order.discountAmount) > 0 && (
            <div className="flex justify-between text-green-500">
              <span>Giảm giá {order.couponCode && `(${order.couponCode})`}</span>
              <span>−{formatPrice(order.discountAmount)}</span>
            </div>
          )}
          <div className="flex justify-between font-bold text-lg text-gray-800 border-t pt-2">
            <span>Tổng cộng</span>
            <span className="text-orange-500">{formatPrice(order.finalAmount)}</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default OrderDetail;
