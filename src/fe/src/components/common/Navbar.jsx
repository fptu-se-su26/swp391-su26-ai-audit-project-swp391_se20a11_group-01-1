import { useState } from 'react';
import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useCart } from '../../context/CartContext';
import { getInitials } from '../../utils/helpers';

const Navbar = () => {
  const [menuOpen, setMenuOpen] = useState(false);
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const { user, logout, isAdmin } = useAuth();
  const { totalItems } = useCart();
  const navigate = useNavigate();

  const linkClass = ({ isActive }) =>
    `text-sm font-medium transition-colors ${isActive ? 'text-orange-500' : 'text-gray-700 hover:text-orange-500'}`;

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <header className="sticky top-0 z-50 bg-white shadow-sm">
      <div className="max-w-6xl mx-auto px-4 h-16 flex items-center justify-between">
        <Link to="/" className="flex items-center gap-2">
          <span className="text-2xl">🍜</span>
          <span className="text-xl font-bold text-orange-500">FoodApp</span>
        </Link>

        <nav className="hidden md:flex items-center gap-8">
          <NavLink to="/" className={linkClass} end>Trang chủ</NavLink>
          <NavLink to="/menu" className={linkClass}>Thực đơn</NavLink>
          <NavLink to="/reviews" className={linkClass}>Đánh giá</NavLink>
          <NavLink to="/reservation" className={linkClass}>Đặt bàn</NavLink>
          {isAdmin() && (
            <NavLink to="/admin" className={linkClass}>Admin</NavLink>
          )}
        </nav>

        <div className="hidden md:flex items-center gap-3">
          {/* Cart */}
          <Link to="/cart" className="relative p-2 text-gray-600 hover:text-orange-500 transition-colors">
            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 3h2l.4 2M7 13h10l4-8H5.4m0 0L7 13m0 0l-2 5h12M7 13L5.4 5M10 21a1 1 0 100-2 1 1 0 000 2zm8 0a1 1 0 100-2 1 1 0 000 2z" />
            </svg>
            {totalItems > 0 && (
              <span className="absolute -top-1 -right-1 bg-orange-500 text-white text-xs w-5 h-5 rounded-full flex items-center justify-center font-bold">
                {totalItems}
              </span>
            )}
          </Link>
          {user ? (
            <div className="relative">
              <button
                onClick={() => setDropdownOpen(!dropdownOpen)}
                className="flex items-center gap-2 focus:outline-none"
              >
                <div className="w-9 h-9 rounded-full bg-orange-100 text-orange-600 flex items-center justify-center font-bold text-sm">
                  {getInitials(user.fullName)}
                </div>
                <span className="text-sm font-medium text-gray-700">{user.fullName}</span>
              </button>
              {dropdownOpen && (
                <div className="absolute right-0 mt-2 w-44 bg-white rounded-xl shadow-lg border py-1 z-50">
                  <Link to="/profile" onClick={() => setDropdownOpen(false)} className="block px-4 py-2 text-sm text-gray-700 hover:bg-orange-50">Hồ sơ</Link>
                  <Link to="/orders" onClick={() => setDropdownOpen(false)} className="block px-4 py-2 text-sm text-gray-700 hover:bg-orange-50">Đơn hàng</Link>
                  <Link to="/reservation" onClick={() => setDropdownOpen(false)} className="block px-4 py-2 text-sm text-gray-700 hover:bg-orange-50">Đặt bàn</Link>
                  {isAdmin() && <Link to="/admin" onClick={() => setDropdownOpen(false)} className="block px-4 py-2 text-sm text-gray-700 hover:bg-orange-50">Quản trị</Link>}
                  <hr className="my-1" />
                  <button onClick={handleLogout} className="w-full text-left px-4 py-2 text-sm text-red-500 hover:bg-red-50">Đăng xuất</button>
                </div>
              )}
            </div>
          ) : (
            <>
              <button onClick={() => navigate('/login')} className="text-sm font-medium text-gray-700 hover:text-orange-500 transition-colors">Đăng nhập</button>
              <button onClick={() => navigate('/register')} className="text-sm font-medium bg-orange-500 text-white px-4 py-2 rounded-full hover:bg-orange-600 transition-colors">Đăng ký</button>
            </>
          )}
        </div>

        <button className="md:hidden p-2 rounded-md text-gray-600 hover:bg-gray-100" onClick={() => setMenuOpen(!menuOpen)}>
          <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            {menuOpen
              ? <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              : <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />}
          </svg>
        </button>
      </div>

      {menuOpen && (
        <div className="md:hidden bg-white border-t px-4 py-4 flex flex-col gap-3">
          <NavLink to="/" className={linkClass} onClick={() => setMenuOpen(false)} end>Trang chủ</NavLink>
          <NavLink to="/menu" className={linkClass} onClick={() => setMenuOpen(false)}>Thực đơn</NavLink>
          <NavLink to="/reviews" className={linkClass} onClick={() => setMenuOpen(false)}>Đánh giá</NavLink>
          <hr />
          {user ? (
            <>
              <NavLink to="/profile" className={linkClass} onClick={() => setMenuOpen(false)}>Hồ sơ</NavLink>
              <button onClick={() => { handleLogout(); setMenuOpen(false); }} className="text-left text-sm text-red-500">Đăng xuất</button>
            </>
          ) : (
            <>
              <NavLink to="/login" className={linkClass} onClick={() => setMenuOpen(false)}>Đăng nhập</NavLink>
              <NavLink to="/register" className={linkClass} onClick={() => setMenuOpen(false)}>Đăng ký</NavLink>
            </>
          )}
        </div>
      )}
    </header>
  );
};

export default Navbar;
