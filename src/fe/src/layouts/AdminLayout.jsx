import { useState } from 'react';
import { Outlet, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const navItems = [
  { path: '/admin/dashboard',    icon: '📊', label: 'Dashboard' },
  { path: '/admin/tables',       icon: '🪑', label: 'Quản lý bàn' },
  { path: '/admin/foods',        icon: '🍽️', label: 'Thực đơn' },
  { path: '/admin/orders',       icon: '📋', label: 'Đơn hàng' },
  { path: '/admin/reservations', icon: '📅', label: 'Đặt bàn' },
  { path: '/admin/reports',      icon: '📈', label: 'Báo cáo' },
  { path: '/admin/reviews',      icon: '💬', label: 'Phản hồi' },
  { path: '/admin/users',        icon: '🔐', label: 'Tài khoản' },
  { path: '/admin/coupons',      icon: '🎁', label: 'Voucher' },
  { path: '/admin/categories',   icon: '📂', label: 'Danh mục' },
];

export default function AdminLayout() {
  const [open, setOpen] = useState(true);
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  return (
    <div className="flex min-h-screen bg-[#f5f6fa]">
      {/* Sidebar */}
      <aside className={`${open ? 'w-56' : 'w-16'} bg-[#1a1a2e] flex flex-col transition-all duration-200 shrink-0`}>
        {/* Logo */}
        <div className="h-16 flex items-center px-4 border-b border-white/10">
          <span className="text-2xl">🍜</span>
          {open && <span className="ml-2 font-bold text-white text-sm leading-tight">Cái Gì Cũng<br/>Không Có</span>}
        </div>

        {/* Nav */}
        <nav className="flex-1 py-3 space-y-0.5 overflow-y-auto">
          {navItems.map((item) => (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) =>
                `flex items-center gap-3 px-4 py-2.5 text-sm font-medium transition-colors rounded-none
                ${isActive
                  ? 'bg-[#e85d04] text-white'
                  : 'text-gray-400 hover:bg-white/10 hover:text-white'}`
              }
            >
              <span className="text-base shrink-0">{item.icon}</span>
              {open && <span>{item.label}</span>}
            </NavLink>
          ))}
        </nav>

        {/* Footer */}
        <div className="p-3 border-t border-white/10">
          <button
            onClick={() => { logout(); navigate('/login'); }}
            className="flex items-center gap-3 w-full px-3 py-2 text-sm text-gray-400 hover:text-white hover:bg-white/10 rounded-lg transition-colors"
          >
            <span>🚪</span>
            {open && <span>Đăng xuất</span>}
          </button>
        </div>
      </aside>

      {/* Main */}
      <div className="flex-1 flex flex-col overflow-hidden">
        {/* Header */}
        <header className="h-16 bg-white shadow-sm flex items-center justify-between px-6 shrink-0">
          <button
            onClick={() => setOpen(!open)}
            className="text-gray-500 hover:text-gray-800 text-xl font-bold"
          >
            ☰
          </button>

          <div className="flex items-center gap-4">
            <span className="text-sm text-gray-500">
              {new Date().toLocaleDateString('vi-VN', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}
            </span>
            <div className="relative">
              <span className="text-xl cursor-pointer">🔔</span>
              <span className="absolute -top-1 -right-1 bg-[#e85d04] text-white text-xs w-4 h-4 rounded-full flex items-center justify-center">3</span>
            </div>
            <div className="flex items-center gap-2">
              <div className="w-9 h-9 rounded-full bg-[#e85d04] text-white flex items-center justify-center font-bold text-sm">
                {user?.fullName?.charAt(0) || 'A'}
              </div>
              <div className="text-sm leading-tight">
                <p className="font-semibold text-gray-800">{user?.fullName || 'Admin'}</p>
                <p className="text-gray-400 text-xs">Quản lý</p>
              </div>
            </div>
          </div>
        </header>

        {/* Content */}
        <main className="flex-1 overflow-auto p-6">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
