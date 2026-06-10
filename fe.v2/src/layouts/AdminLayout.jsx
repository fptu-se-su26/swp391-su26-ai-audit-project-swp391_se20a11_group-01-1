import { Outlet, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const links = [
  { to: '/admin/dashboard', label: 'Dashboard', icon: '📊' },
  { to: '/admin/users', label: 'Quản lý Users', icon: '👥' },
  { to: '/admin/foods', label: 'Quản lý Món ăn', icon: '🍔' },
  { to: '/admin/categories', label: 'Quản lý Danh mục', icon: '📂' },
  { to: '/admin/reviews', label: 'Quản lý Đánh giá', icon: '⭐' },
];

const AdminLayout = () => {
  const navigate = useNavigate();
  const { user, logout } = useAuth();

  const linkClass = ({ isActive }) =>
    `flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-medium transition-colors ${
      isActive ? 'bg-orange-500 text-white' : 'text-gray-600 hover:bg-orange-50 hover:text-orange-500'
    }`;

  return (
    <div className="min-h-screen flex bg-gray-100">
      <aside className="w-64 shrink-0 bg-white shadow-sm flex flex-col">
        <div className="h-16 flex items-center px-6 border-b gap-2">
          <span className="text-2xl">🍜</span>
          <span className="font-bold text-gray-800 text-lg">Admin</span>
        </div>
        <nav className="flex-1 p-4 space-y-1">
          {links.map((l) => (
            <NavLink key={l.to} to={l.to} className={linkClass}>
              <span>{l.icon}</span><span>{l.label}</span>
            </NavLink>
          ))}
        </nav>
        <div className="p-4 border-t space-y-1">
          <button onClick={() => navigate('/')} className="w-full flex items-center gap-3 px-4 py-2.5 rounded-xl text-sm text-gray-500 hover:bg-gray-100 transition-colors">
            🏠 Về trang chủ
          </button>
          <button onClick={() => { logout(); navigate('/login'); }} className="w-full flex items-center gap-3 px-4 py-2.5 rounded-xl text-sm text-red-400 hover:bg-red-50 transition-colors">
            🚪 Đăng xuất
          </button>
        </div>
      </aside>

      <div className="flex-1 flex flex-col overflow-hidden">
        <header className="h-16 bg-white shadow-sm flex items-center justify-between px-6">
          <h1 className="font-semibold text-gray-700 text-lg">Admin Panel</h1>
          <div className="flex items-center gap-2 text-sm text-gray-600">
            <div className="w-8 h-8 rounded-full bg-orange-100 text-orange-600 flex items-center justify-center font-bold text-xs">A</div>
            {user?.name}
          </div>
        </header>
        <main className="flex-1 overflow-auto p-6"><Outlet /></main>
      </div>
    </div>
  );
};

export default AdminLayout;
