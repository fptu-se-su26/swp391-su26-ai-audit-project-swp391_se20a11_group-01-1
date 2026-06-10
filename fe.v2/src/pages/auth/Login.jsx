import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const Login = () => {
  const { login, loading, error } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ email: '', password: '' });

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const data = await login(form);
      navigate(data.role === 'ADMIN' ? '/admin/dashboard' : '/');
    } catch { /* error shown via context */ }
  };

  return (
    <div className="w-full max-w-md">
      <div className="bg-white rounded-3xl shadow-lg p-8">
        <div className="text-center mb-8">
          <div className="text-5xl mb-3">🍜</div>
          <h1 className="text-2xl font-bold text-gray-800">Chào mừng trở lại</h1>
          <p className="text-gray-500 text-sm mt-1">Đăng nhập vào tài khoản của bạn</p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-5">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1.5">Email</label>
            <input type="email" value={form.email} onChange={e => setForm({...form, email: e.target.value})}
              placeholder="you@example.com" required
              className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-orange-400 text-sm" />
          </div>
          <div>
            <div className="flex justify-between mb-1.5">
              <label className="text-sm font-medium text-gray-700">Mật khẩu</label>
              <Link to="/forgot-password" className="text-xs text-orange-500 hover:underline">Quên mật khẩu?</Link>
            </div>
            <input type="password" value={form.password} onChange={e => setForm({...form, password: e.target.value})}
              placeholder="••••••••" required
              className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-orange-400 text-sm" />
          </div>

          {error && <p className="text-red-500 text-sm bg-red-50 px-4 py-2 rounded-xl">{error}</p>}

          <button type="submit" disabled={loading}
            className="w-full bg-orange-500 hover:bg-orange-600 text-white font-semibold py-3 rounded-xl transition-colors disabled:opacity-60">
            {loading ? 'Đang đăng nhập...' : 'Đăng nhập'}
          </button>
        </form>

        <p className="text-center text-sm text-gray-500 mt-6">
          Chưa có tài khoản?{' '}
          <Link to="/register" className="text-orange-500 font-semibold hover:underline">Đăng ký ngay</Link>
        </p>
      </div>
    </div>
  );
};

export default Login;
