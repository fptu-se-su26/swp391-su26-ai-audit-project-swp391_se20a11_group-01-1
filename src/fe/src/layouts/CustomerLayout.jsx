import { useState } from 'react';
import { Outlet, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useCart } from '../context/CartContext';
import LiveChat from '../components/LiveChat';

export default function CustomerLayout() {
  const { user, logout } = useAuth();
  const { totalItems } = useCart();
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);

  const linkCls = ({ isActive }) =>
    `text-sm font-medium transition-colors ${isActive ? 'text-[#e85d04]' : 'text-gray-700 hover:text-[#e85d04]'}`;

  return (
    <div className="min-h-screen flex flex-col bg-gray-50">
      <header className="sticky top-0 z-40 bg-white shadow-sm">
        <div className="max-w-6xl mx-auto px-4 h-16 flex items-center justify-between">
          <div className="flex items-center gap-2 cursor-pointer" onClick={() => navigate('/customer/menu')}>
            <span className="text-2xl">🍜</span>
            <span className="font-extrabold text-gray-800 text-sm">Cái Gì Cũng Không Có</span>
          </div>

          <nav className="hidden md:flex items-center gap-7">
            <NavLink to="/customer/menu"        className={linkCls}>Thực đơn</NavLink>
            <NavLink to="/customer/reservation" className={linkCls}>Đặt bàn</NavLink>
            <NavLink to="/customer/orders"      className={linkCls}>Đơn hàng</NavLink>
          </nav>

          <div className="flex items-center gap-3">
            <NavLink to="/customer/cart" className="relative p-2 text-gray-600 hover:text-[#e85d04]">
              🛒
              {totalItems > 0 && (
                <span className="absolute -top-0.5 -right-0.5 bg-[#e85d04] text-white text-xs w-5 h-5 rounded-full flex items-center justify-center font-bold">
                  {totalItems}
                </span>
              )}
            </NavLink>

            <div className="relative hidden md:block">
              <button onClick={() => setMenuOpen(!menuOpen)} className="flex items-center gap-2">
                <div className="w-8 h-8 rounded-full bg-orange-100 text-orange-600 flex items-center justify-center font-bold text-sm">
                  {user?.fullName?.charAt(0) || 'K'}
                </div>
                <span className="text-sm font-medium text-gray-700">{user?.fullName}</span>
              </button>
              {menuOpen && (
                <div className="absolute right-0 mt-2 w-44 bg-white rounded-xl shadow-lg border py-1 z-50">
                  <button onClick={() => { navigate('/customer/profile'); setMenuOpen(false); }} className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-orange-50">👤 Hồ sơ của tôi</button>
                  <button onClick={() => { navigate('/customer/feedback'); setMenuOpen(false); }} className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-orange-50">⭐ Đánh giá</button>
                  <hr className="my-1" />
                  <button onClick={() => { logout(); navigate('/login'); }} className="w-full text-left px-4 py-2 text-sm text-red-500 hover:bg-red-50">🚪 Đăng xuất</button>
                </div>
              )}
            </div>

            <button className="md:hidden p-2" onClick={() => setMenuOpen(!menuOpen)}>
              <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
              </svg>
            </button>
          </div>
        </div>

        {menuOpen && (
          <div className="md:hidden border-t px-4 py-3 flex flex-col gap-3 bg-white">
            <NavLink to="/customer/menu"        className={linkCls} onClick={() => setMenuOpen(false)}>Thực đơn</NavLink>
            <NavLink to="/customer/reservation" className={linkCls} onClick={() => setMenuOpen(false)}>Đặt bàn</NavLink>
            <NavLink to="/customer/orders"      className={linkCls} onClick={() => setMenuOpen(false)}>Đơn hàng</NavLink>
            <NavLink to="/customer/profile"     className={linkCls} onClick={() => setMenuOpen(false)}>Hồ sơ</NavLink>
            <button onClick={() => { logout(); navigate('/login'); }} className="text-left text-sm text-red-500">Đăng xuất</button>
          </div>
        )}
      </header>

      <main className="flex-1"><Outlet /></main>
      <LiveChat />
    </div>
  );
}
