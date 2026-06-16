import { Outlet, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function KitchenLayout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  return (
    <div className="min-h-screen bg-gray-900 flex flex-col">
      <header className="h-14 bg-gray-800 flex items-center justify-between px-6 border-b border-gray-700">
        <div className="flex items-center gap-3">
          <span className="text-xl">🍳</span>
          <span className="font-bold text-white">Kitchen Display</span>
        </div>
        <nav className="flex gap-2">
          <NavLink to="/kitchen/queue"
            className={({ isActive }) => `px-4 py-1.5 rounded-lg text-sm font-semibold transition-colors ${isActive ? 'bg-[#e85d04] text-white' : 'text-gray-300 hover:bg-gray-700'}`}>
            📋 Hàng chờ
          </NavLink>
          <NavLink to="/kitchen/history"
            className={({ isActive }) => `px-4 py-1.5 rounded-lg text-sm font-semibold transition-colors ${isActive ? 'bg-[#e85d04] text-white' : 'text-gray-300 hover:bg-gray-700'}`}>
            ✅ Hoàn thành
          </NavLink>
        </nav>
        <div className="flex items-center gap-3">
          <span className="text-sm text-gray-300">👤 {user?.fullName}</span>
          <button onClick={() => { logout(); navigate('/login'); }}
            className="text-xs text-gray-400 hover:text-white px-3 py-1.5 rounded-lg hover:bg-gray-700">
            🚪 Đăng xuất
          </button>
        </div>
      </header>
      <main className="flex-1 overflow-auto p-5"><Outlet /></main>
    </div>
  );
}
