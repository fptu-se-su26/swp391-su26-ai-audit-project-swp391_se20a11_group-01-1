import { useEffect, useState } from 'react';
import { getAllOrders, updateOrderStatus, confirmPayment } from '../../services/orderService';
import { formatPrice, formatDate } from '../../utils/helpers';

const STATUS_OPTS = ['PENDING','CONFIRMED','PREPARING','READY','DELIVERING','COMPLETED','CANCELLED'];
const STATUS_COLOR = {
  PENDING:   'bg-yellow-100 text-yellow-700',
  CONFIRMED: 'bg-blue-100 text-blue-600',
  PREPARING: 'bg-orange-100 text-orange-600',
  READY:     'bg-green-100 text-green-600',
  DELIVERING:'bg-purple-100 text-purple-600',
  COMPLETED: 'bg-green-200 text-green-700',
  CANCELLED: 'bg-red-100 text-red-500',
};
const STATUS_LABEL = {
  PENDING:'Chờ xác nhận', CONFIRMED:'Đã xác nhận', PREPARING:'Đang chuẩn bị',
  READY:'Sẵn sàng', DELIVERING:'Đang giao', COMPLETED:'Hoàn thành', CANCELLED:'Đã hủy',
};

export default function OrderManagement() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [filter, setFilter] = useState('all');
  const [expanded, setExpanded] = useState(null);
  const [tab, setTab] = useState('orders'); // orders | report

  const load = () => {
    setLoading(true);
    getAllOrders().then(({ data }) => setOrders(data)).catch(() => {}).finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  const handleStatus = async (id, status) => { await updateOrderStatus(id, status); load(); };
  const handlePayment = async (id) => { await confirmPayment(id); load(); };

  const filtered = orders.filter(o => {
    const matchFilter = filter === 'all' || o.status === filter;
    const matchSearch = String(o.id).includes(search) || o.userFullName?.toLowerCase().includes(search.toLowerCase());
    return matchFilter && matchSearch;
  });

  // Report stats
  const totalRevenue = orders.filter(o => o.paymentStatus === 'PAID').reduce((s, o) => s + Number(o.finalAmount || 0), 0);
  const doneOrders   = orders.filter(o => o.status === 'COMPLETED').length;
  const cancelOrders = orders.filter(o => o.status === 'CANCELLED').length;

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-800">Đơn hàng</h1>
        <div className="flex gap-2">
          {[['orders','📋 Danh sách'],['report','📊 Báo cáo']].map(([v,l]) => (
            <button key={v} onClick={() => setTab(v)}
              className={`px-4 py-2 rounded-xl text-sm font-semibold border transition-colors ${tab === v ? 'bg-[#e85d04] text-white border-[#e85d04]' : 'bg-white text-gray-600 border-gray-200 hover:border-[#e85d04]'}`}>
              {l}
            </button>
          ))}
        </div>
      </div>

      {/* Report tab */}
      {tab === 'report' && (
        <div>
          <div className="grid grid-cols-2 lg:grid-cols-5 gap-4 mb-6">
            {[
              { label: 'Tổng doanh thu', val: formatPrice(totalRevenue), color: '#e85d04' },
              { label: 'Tổng đơn',       val: orders.length,             color: '#3182ce' },
              { label: 'Hoàn thành',     val: doneOrders,                color: '#38a169' },
              { label: 'Đã hủy',         val: cancelOrders,              color: '#e53e3e' },
              { label: 'TB / đơn',       val: doneOrders > 0 ? formatPrice(Math.round(totalRevenue / doneOrders)) : '—', color: '#805ad5' },
            ].map((s, i) => (
              <div key={i} className="bg-white rounded-xl p-5 shadow-sm text-center">
                <p className="text-xs text-gray-500 mb-1">{s.label}</p>
                <h3 className="text-xl font-extrabold" style={{ color: s.color }}>{s.val}</h3>
              </div>
            ))}
          </div>
          <div className="bg-white rounded-2xl shadow-sm overflow-hidden">
            <div className="px-6 py-4 border-b font-bold text-sm text-gray-700">👤 Hiệu suất nhân viên</div>
            <table className="w-full text-sm">
              <thead>
                <tr className="bg-gray-50 text-left text-gray-500 text-xs uppercase">
                  <th className="px-5 py-3">Khách hàng</th>
                  <th className="px-5 py-3">Số đơn</th>
                  <th className="px-5 py-3">Doanh thu</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-50">
                {Object.entries(
                  orders.reduce((acc, o) => {
                    const key = o.userFullName || 'Ẩn danh';
                    if (!acc[key]) acc[key] = { orders: 0, revenue: 0 };
                    acc[key].orders++;
                    if (o.paymentStatus === 'PAID') acc[key].revenue += Number(o.finalAmount || 0);
                    return acc;
                  }, {})
                ).slice(0, 10).map(([name, s]) => (
                  <tr key={name} className="hover:bg-gray-50">
                    <td className="px-5 py-3 font-semibold">{name}</td>
                    <td className="px-5 py-3">{s.orders} đơn</td>
                    <td className="px-5 py-3 text-[#e85d04] font-bold">{formatPrice(s.revenue)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Orders tab */}
      {tab === 'orders' && (
        <>
          <div className="flex flex-wrap gap-3 mb-4">
            <input className="px-4 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-[#e85d04] bg-white min-w-56"
              placeholder="🔍 Tìm theo ID hoặc tên..." value={search} onChange={e => setSearch(e.target.value)} />
            <div className="flex gap-2 flex-wrap">
              {[['all','Tất cả'], ...STATUS_OPTS.map(s => [s, STATUS_LABEL[s]])].map(([v, l]) => (
                <button key={v} onClick={() => setFilter(v)}
                  className={`px-3 py-2 rounded-full text-xs font-semibold border transition-colors ${filter === v ? 'bg-[#e85d04] text-white border-[#e85d04]' : 'bg-white text-gray-600 border-gray-200 hover:border-[#e85d04]'}`}>
                  {l}
                </button>
              ))}
            </div>
          </div>

          <div className="space-y-3">
            {filtered.map(order => (
              <div key={order.id} className="bg-white rounded-2xl shadow-sm overflow-hidden">
                <div className="flex items-center gap-4 px-5 py-4 cursor-pointer" onClick={() => setExpanded(expanded === order.id ? null : order.id)}>
                  <div className="shrink-0">
                    <p className="font-bold text-[#e85d04]">#{order.id}</p>
                    <p className="text-xs text-gray-400">{formatDate(order.createdAt)}</p>
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="font-semibold text-gray-800 truncate">{order.userFullName}</p>
                    <p className="text-xs text-gray-400">{order.orderType === 'ONLINE' ? '🚀 Giao hàng' : '🍽️ Ăn tại quán'} • {order.items?.length || 0} món</p>
                  </div>
                  <div className="text-right shrink-0">
                    <p className="font-bold text-gray-800">{formatPrice(order.finalAmount)}</p>
                    <span className={`text-xs font-semibold px-2 py-0.5 rounded-full ${STATUS_COLOR[order.status]}`}>{STATUS_LABEL[order.status]}</span>
                  </div>
                  <div className="shrink-0">
                    {order.paymentStatus === 'PAID'
                      ? <span className="text-xs text-green-500 font-semibold">✓ Đã TT</span>
                      : <button onClick={e => { e.stopPropagation(); handlePayment(order.id); }} className="text-xs text-blue-500 hover:text-blue-700 font-semibold">Xác nhận TT</button>}
                  </div>
                  <span className="text-gray-400 text-xs">{expanded === order.id ? '▲' : '▼'}</span>
                </div>

                {expanded === order.id && (
                  <div className="border-t px-5 py-4">
                    {order.items?.map((item, i) => (
                      <div key={i} className="flex justify-between text-sm py-1 border-b border-gray-50 last:border-0">
                        <span>{item.foodName} × {item.quantity}</span>
                        <span className="text-[#e85d04] font-semibold">{formatPrice(item.subtotal)}</span>
                      </div>
                    ))}
                    {order.deliveryAddress && <p className="text-sm text-gray-500 mt-3">📍 {order.deliveryAddress}</p>}
                    <div className="flex items-center gap-3 mt-4">
                      <span className="text-sm font-medium text-gray-600">Cập nhật trạng thái:</span>
                      <select value={order.status} onChange={e => handleStatus(order.id, e.target.value)}
                        className="text-sm border rounded-lg px-3 py-1.5 focus:outline-none focus:ring-1 focus:ring-[#e85d04]">
                        {STATUS_OPTS.map(s => <option key={s} value={s}>{STATUS_LABEL[s]}</option>)}
                      </select>
                    </div>
                  </div>
                )}
              </div>
            ))}
            {filtered.length === 0 && (
              <div className="text-center py-12 text-gray-400 bg-white rounded-2xl">
                <p className="text-4xl mb-2">📋</p><p>Không có đơn hàng</p>
              </div>
            )}
          </div>
        </>
      )}
    </div>
  );
}
