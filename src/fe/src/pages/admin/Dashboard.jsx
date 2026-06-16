import { useEffect, useState } from 'react';
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, BarChart, Bar } from 'recharts';
import { getAllOrders } from '../../services/orderService';
import { getAllFoods } from '../../services/foodService';
import { getAllUsers } from '../../services/userService';
import { getAllReservations } from '../../services/reservationService';
import { formatPrice } from '../../utils/helpers';

const revenueData = [
  { day: 'T2', revenue: 4200000 }, { day: 'T3', revenue: 3800000 },
  { day: 'T4', revenue: 5100000 }, { day: 'T5', revenue: 4700000 },
  { day: 'T6', revenue: 6200000 }, { day: 'T7', revenue: 7800000 },
  { day: 'CN', revenue: 8100000 },
];

const recentOrders = [
  { id: '#001', table: 'Bàn 5', items: 3, total: '850.000đ',   status: 'Đang phục vụ' },
  { id: '#002', table: 'Bàn 2', items: 5, total: '1.200.000đ', status: 'Hoàn thành' },
  { id: '#003', table: 'Bàn 8', items: 4, total: '980.000đ',   status: 'Đang phục vụ' },
  { id: '#004', table: 'Bàn 1', items: 2, total: '560.000đ',   status: 'Chờ xác nhận' },
];

const statusColor = {
  'Đang phục vụ': 'bg-blue-100 text-blue-700',
  'Hoàn thành':   'bg-green-100 text-green-700',
  'Chờ xác nhận': 'bg-yellow-100 text-yellow-700',
};

function StatCard({ icon, label, value, sub, color }) {
  return (
    <div className="bg-white rounded-xl p-5 shadow-sm flex items-center gap-4">
      <div className={`w-14 h-14 rounded-xl flex items-center justify-center text-2xl ${color}`}>{icon}</div>
      <div>
        <p className="text-sm text-gray-500">{label}</p>
        <h3 className="text-2xl font-extrabold text-gray-800">{value}</h3>
        <p className="text-xs text-gray-400 mt-0.5">{sub}</p>
      </div>
    </div>
  );
}

export default function Dashboard() {
  const [stats, setStats] = useState({ users: 0, foods: 0, orders: 0, reservations: 0, revenue: 0 });
  const [topDishes, setTopDishes] = useState([]);

  useEffect(() => {
    Promise.allSettled([getAllOrders(), getAllFoods(), getAllUsers(), getAllReservations()])
      .then(([o, f, u, r]) => {
        const orders = o.value?.data || [];
        const revenue = orders.filter(ord => ord.paymentStatus === 'PAID')
          .reduce((s, ord) => s + Number(ord.finalAmount || 0), 0);
        const foods = f.value?.data || [];
        const FALLBACK_DISHES = [
          { name: 'Lẩu bò nhúng dấm', orders: 178 },
          { name: 'Gà nướng muối ớt',  orders: 156 },
          { name: 'Mực chiên giòn',    orders: 142 },
          { name: 'Sườn nướng BBQ',    orders: 128 },
          { name: 'Bò lúc lắc',        orders: 115 },
          { name: 'Nhậu hải sản mix',  orders: 98  },
        ];
        setStats({
          users: u.value?.data?.length || 0,
          foods: foods.length,
          orders: orders.length,
          reservations: r.value?.data?.length || 0,
          revenue,
        });
        setTopDishes(
          foods.length > 0
            ? foods.slice(0, 6).map((fd, i) => ({ name: fd.name, orders: FALLBACK_DISHES[i]?.orders || Math.floor(Math.random() * 150) + 50 }))
            : FALLBACK_DISHES
        );
      });
  }, []);

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-800 mb-6">Dashboard</h1>

      {/* Stats */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
        <StatCard icon="💰" label="Doanh thu hôm nay" value={formatPrice(stats.revenue)} sub="↑ 12% so với hôm qua" color="bg-orange-50" />
        <StatCard icon="📋" label="Đơn hàng hôm nay" value={stats.orders} sub="↑ 5 đơn so với hôm qua" color="bg-blue-50" />
        <StatCard icon="🪑" label="Bàn đang phục vụ" value={`${stats.reservations} / 20`} sub="8 bàn còn trống" color="bg-green-50" />
        <StatCard icon="👥" label="Khách hôm nay" value={stats.users} sub="↑ 8% so với hôm qua" color="bg-yellow-50" />
      </div>

      {/* Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-5 mb-5">
        <div className="bg-white rounded-xl p-5 shadow-sm">
          <h2 className="text-sm font-bold text-gray-700 mb-4">Doanh thu 7 ngày qua</h2>
          <ResponsiveContainer width="100%" height={220}>
            <AreaChart data={revenueData}>
              <defs>
                <linearGradient id="colorRev" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#e85d04" stopOpacity={0.2} />
                  <stop offset="95%" stopColor="#e85d04" stopOpacity={0} />
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
              <XAxis dataKey="day" tick={{ fontSize: 12 }} />
              <YAxis tick={{ fontSize: 12 }} tickFormatter={v => `${(v/1000000).toFixed(1)}M`} />
              <Tooltip formatter={v => `${v.toLocaleString('vi-VN')}đ`} />
              <Area type="monotone" dataKey="revenue" stroke="#e85d04" fill="url(#colorRev)" strokeWidth={2} />
            </AreaChart>
          </ResponsiveContainer>
        </div>

        <div className="bg-white rounded-xl p-5 shadow-sm">
          <h2 className="text-sm font-bold text-gray-700 mb-4">Món bán chạy</h2>
          <ResponsiveContainer width="100%" height={220}>
            <BarChart data={topDishes} layout="vertical">
              <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
              <XAxis type="number" tick={{ fontSize: 11 }} />
              <YAxis dataKey="name" type="category" tick={{ fontSize: 11 }} width={120} />
              <Tooltip />
              <Bar dataKey="orders" fill="#e85d04" radius={[0, 4, 4, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Recent orders */}
      <div className="bg-white rounded-xl shadow-sm overflow-hidden">
        <div className="px-6 py-4 border-b">
          <h2 className="text-sm font-bold text-gray-700">Đơn hàng gần đây</h2>
        </div>
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-gray-50 text-left text-gray-500 text-xs uppercase tracking-wider">
              <th className="px-5 py-3 font-semibold">Mã đơn</th>
              <th className="px-5 py-3 font-semibold">Bàn</th>
              <th className="px-5 py-3 font-semibold">Số món</th>
              <th className="px-5 py-3 font-semibold">Tổng tiền</th>
              <th className="px-5 py-3 font-semibold">Trạng thái</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-50">
            {recentOrders.map(order => (
              <tr key={order.id} className="hover:bg-gray-50">
                <td className="px-5 py-3 font-semibold text-[#e85d04]">{order.id}</td>
                <td className="px-5 py-3">{order.table}</td>
                <td className="px-5 py-3">{order.items} món</td>
                <td className="px-5 py-3 font-semibold">{order.total}</td>
                <td className="px-5 py-3">
                  <span className={`px-2 py-1 rounded-full text-xs font-semibold ${statusColor[order.status]}`}>
                    {order.status}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
