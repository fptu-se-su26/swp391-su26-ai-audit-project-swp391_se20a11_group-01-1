import { useState } from 'react';
import {
  LineChart, Line, BarChart, Bar,
  XAxis, YAxis, CartesianGrid, Tooltip,
  ResponsiveContainer, PieChart, Pie, Cell, Legend,
} from 'recharts';

const monthlyData = [
  { month: 'T1', revenue: 85000000,  orders: 312 },
  { month: 'T2', revenue: 92000000,  orders: 338 },
  { month: 'T3', revenue: 78000000,  orders: 287 },
  { month: 'T4', revenue: 105000000, orders: 385 },
  { month: 'T5', revenue: 118000000, orders: 433 },
  { month: 'T6', revenue: 132000000, orders: 484 },
];

const weeklyData = [
  { day: 'T2', revenue: 4200000 }, { day: 'T3', revenue: 3800000 },
  { day: 'T4', revenue: 5100000 }, { day: 'T5', revenue: 4700000 },
  { day: 'T6', revenue: 6200000 }, { day: 'T7', revenue: 7800000 },
  { day: 'CN', revenue: 8100000 },
];

const categoryData = [
  { name: 'Món chính', value: 52 }, { name: 'Đồ uống', value: 22 },
  { name: 'Khai vị',   value: 16 }, { name: 'Tráng miệng', value: 10 },
];

const topDishes = [
  { name: 'Lẩu bò nhúng dấm', orders: 178 },
  { name: 'Gà nướng muối ớt',  orders: 156 },
  { name: 'Mực chiên giòn',    orders: 142 },
  { name: 'Sườn nướng BBQ',    orders: 128 },
  { name: 'Bò lúc lắc',        orders: 115 },
  { name: 'Nhậu hải sản mix',  orders: 98  },
];

const COLORS = ['#e85d04', '#3182ce', '#38a169', '#d69e2e'];

export default function Reports() {
  const [period, setPeriod] = useState('month');
  const data = period === 'week' ? weeklyData : monthlyData;
  const xKey = period === 'week' ? 'day' : 'month';

  const totalRevenue = monthlyData.reduce((s, d) => s + d.revenue, 0);
  const totalOrders  = monthlyData.reduce((s, d) => s + d.orders, 0);
  const avgOrder     = Math.round(totalRevenue / totalOrders);

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-800">Báo cáo & Thống kê</h1>
        <div className="flex gap-2">
          {[['week','Tuần'],['month','Tháng'],['year','Năm']].map(([v, l]) => (
            <button key={v} onClick={() => setPeriod(v)}
              className={`px-4 py-2 rounded-full text-sm font-semibold border transition-colors ${period === v ? 'bg-[#e85d04] text-white border-[#e85d04]' : 'bg-white text-gray-600 border-gray-200 hover:border-[#e85d04]'}`}>
              {l}
            </button>
          ))}
        </div>
      </div>

      {/* KPI */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-5">
        {[
          { label: '💰 Tổng doanh thu', val: `${(totalRevenue/1000000).toFixed(0)}M đ`, sub: '↑ 18% so với kỳ trước' },
          { label: '📋 Tổng đơn hàng',  val: totalOrders.toLocaleString('vi-VN'), sub: '↑ 12% so với kỳ trước' },
          { label: '💵 Trung bình / đơn', val: `${avgOrder.toLocaleString('vi-VN')}đ`, sub: '↑ 5% so với kỳ trước' },
        ].map((s, i) => (
          <div key={i} className="bg-white rounded-xl p-5 shadow-sm">
            <p className="text-sm text-gray-500">{s.label}</p>
            <h2 className="text-2xl font-extrabold text-[#e85d04] mt-1">{s.val}</h2>
            <p className="text-xs text-green-500 mt-1">{s.sub}</p>
          </div>
        ))}
      </div>

      {/* Charts row 1 */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-5 mb-5">
        <div className="bg-white rounded-xl p-5 shadow-sm">
          <h2 className="text-sm font-bold text-gray-700 mb-4">📈 Doanh thu theo {period === 'week' ? 'tuần' : 'tháng'}</h2>
          <ResponsiveContainer width="100%" height={220}>
            <LineChart data={data}>
              <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
              <XAxis dataKey={xKey} tick={{ fontSize: 12 }} />
              <YAxis tick={{ fontSize: 12 }} tickFormatter={v => `${(v/1000000).toFixed(0)}M`} />
              <Tooltip formatter={v => `${v?.toLocaleString('vi-VN')}đ`} />
              <Line type="monotone" dataKey="revenue" stroke="#e85d04" strokeWidth={2} dot={{ r: 4 }} name="Doanh thu" />
            </LineChart>
          </ResponsiveContainer>
        </div>

        <div className="bg-white rounded-xl p-5 shadow-sm">
          <h2 className="text-sm font-bold text-gray-700 mb-4">🥧 Tỷ lệ danh mục</h2>
          <ResponsiveContainer width="100%" height={220}>
            <PieChart>
              <Pie data={categoryData} cx="50%" cy="50%" outerRadius={75} dataKey="value"
                label={({ name, value }) => `${value}%`}>
                {categoryData.map((_, i) => <Cell key={i} fill={COLORS[i % COLORS.length]} />)}
              </Pie>
              <Legend />
              <Tooltip formatter={v => `${v}%`} />
            </PieChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Charts row 2 */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-5 mb-5">
        <div className="bg-white rounded-xl p-5 shadow-sm">
          <h2 className="text-sm font-bold text-gray-700 mb-4">🏆 Món bán chạy nhất</h2>
          <ResponsiveContainer width="100%" height={220}>
            <BarChart data={topDishes} layout="vertical">
              <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
              <XAxis type="number" tick={{ fontSize: 11 }} />
              <YAxis dataKey="name" type="category" tick={{ fontSize: 11 }} width={160} />
              <Tooltip />
              <Bar dataKey="orders" fill="#e85d04" radius={[0, 4, 4, 0]} name="Số đơn" />
            </BarChart>
          </ResponsiveContainer>
        </div>

        <div className="bg-white rounded-xl p-5 shadow-sm">
          <h2 className="text-sm font-bold text-gray-700 mb-4">📦 Số đơn hàng theo tháng</h2>
          <ResponsiveContainer width="100%" height={220}>
            <BarChart data={monthlyData}>
              <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
              <XAxis dataKey="month" tick={{ fontSize: 12 }} />
              <YAxis tick={{ fontSize: 12 }} />
              <Tooltip />
              <Bar dataKey="orders" fill="#3182ce" radius={[4, 4, 0, 0]} name="Đơn hàng" />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Top dishes table */}
      <div className="bg-white rounded-2xl shadow-sm overflow-hidden">
        <div className="px-6 py-4 border-b font-bold text-sm text-gray-700">📋 Chi tiết món bán chạy</div>
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-gray-50 text-left text-gray-500 text-xs uppercase tracking-wider">
              <th className="px-5 py-3">#</th><th className="px-5 py-3">Tên món</th>
              <th className="px-5 py-3">Số đơn</th><th className="px-5 py-3">Tỷ lệ</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-50">
            {topDishes.map((dish, i) => (
              <tr key={i} className="hover:bg-gray-50">
                <td className="px-5 py-3 font-bold text-[#e85d04]">#{i + 1}</td>
                <td className="px-5 py-3">{dish.name}</td>
                <td className="px-5 py-3">{dish.orders}</td>
                <td className="px-5 py-3">
                  <div className="flex items-center gap-2">
                    <div className="flex-1 h-1.5 bg-gray-100 rounded-full">
                      <div className="h-1.5 bg-[#e85d04] rounded-full" style={{ width: `${Math.round(dish.orders / topDishes[0].orders * 100)}%` }}></div>
                    </div>
                    <span className="text-xs text-gray-400 w-8">{Math.round(dish.orders / topDishes[0].orders * 100)}%</span>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
