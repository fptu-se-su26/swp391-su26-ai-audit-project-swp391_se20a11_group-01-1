import { useEffect, useState } from 'react';
import { getAllUsers } from '../../services/userService';
import { getAllFoods } from '../../services/foodService';
import { getAllCategories } from '../../services/categoryService';
import { getAllReviews } from '../../services/reviewService';

const StatCard = ({ icon, label, value, color }) => (
  <div className="bg-white rounded-2xl p-6 shadow-sm flex items-center gap-4">
    <div className={`w-14 h-14 rounded-xl flex items-center justify-center text-2xl ${color}`}>{icon}</div>
    <div>
      <p className="text-sm text-gray-500">{label}</p>
      <p className="text-3xl font-extrabold text-gray-800">{value}</p>
    </div>
  </div>
);

const Dashboard = () => {
  const [stats, setStats] = useState({ users: 0, foods: 0, categories: 0, reviews: 0 });

  useEffect(() => {
    Promise.allSettled([getAllUsers(), getAllFoods(), getAllCategories(), getAllReviews()])
      .then(([u, f, c, r]) => setStats({
        users: u.value?.data?.length || 0,
        foods: f.value?.data?.length || 0,
        categories: c.value?.data?.length || 0,
        reviews: r.value?.data?.length || 0,
      }));
  }, []);

  return (
    <div>
      <h2 className="text-2xl font-bold text-gray-800 mb-6">Dashboard</h2>
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        <StatCard icon="👥" label="Người dùng" value={stats.users} color="bg-blue-50" />
        <StatCard icon="🍔" label="Món ăn" value={stats.foods} color="bg-orange-50" />
        <StatCard icon="📂" label="Danh mục" value={stats.categories} color="bg-green-50" />
        <StatCard icon="⭐" label="Đánh giá" value={stats.reviews} color="bg-yellow-50" />
      </div>
      <div className="bg-white rounded-2xl p-6 shadow-sm text-center text-gray-400">
        <p className="text-4xl mb-2">📊</p>
        <p>Chọn mục quản lý từ sidebar bên trái</p>
      </div>
    </div>
  );
};

export default Dashboard;
