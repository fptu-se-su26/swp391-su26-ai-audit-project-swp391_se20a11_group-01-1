import { Outlet, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const navItems = [
  { path: '/staff/tables',       icon: '🪑', label: 'Quản lý bàn' },
  { path: '/staff/reservations', icon: '📅', label: 'Đặt bàn trước' },
  { path: '/staff/orders',       icon: '📋', label: 'Đơn hàng' },
  { path: '/staff/customers',    icon: '👤', label: 'Khách hàng' },
];

export default function StaffLayout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  return (
    <div className="flex min-h-screen bg-[#f5f6fa]">
      <aside className="w-52 bg-[#1a1a2e] flex flex-col shrink-0">
        <div className="h-16 flex items-center px-5 border-b border-white/10 gap-2">
          <span className="text-2xl">🍜</span>
          <span className="font-bold text-white text-sm">Staff Panel</span>
        </div>
        <nav className="flex-1 py-3 space-y-0.5">
          {navItems.map(item => (
            <NavLink key={item.path} to={item.path}
              className={({ isActive }) =>
                `flex items-center gap-3 px-5 py-2.5 text-sm font-medium transition-colors ${isActive ? 'bg-[#e85d04] text-white' : 'text-gray-400 hover:bg-white/10 hover:text-white'}`}>
              <span>{item.icon}</span><span>{item.label}</span>
            </NavLink>
          ))}
        </nav>
        <div className="p-4 border-t border-white/10">
          <div className="flex items-center gap-2 mb-3">
            <div className="w-8 h-8 rounded-full bg-[#e85d04] text-white flex items-center justify-center font-bold text-xs">
              {user?.fullName?.charAt(0) || 'S'}
            </div>
            <div className="text-xs">
              <p className="font-semibold text-white">{user?.fullName}</p>
              <p className="text-gray-400">Nhân viên</p>
            </div>
          </div>
          <button onClick={() => { logout(); navigate('/login'); }}
            className="flex items-center gap-2 w-full px-3 py-2 text-xs text-gray-400 hover:text-white hover:bg-white/10 rounded-lg">
            🚪 Đăng xuất
          </button>
        </div>
      </aside>
      <main className="flex-1 overflow-auto p-6"><Outlet /></main>
    </div>
  );
}
