
import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Login.css';

function Login() {
  const { login } = useAuth();

  const [form, setForm] = useState({
    email: '',
    password: ''
  });

  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [showPass, setShowPass] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();

    setError('');
    setLoading(true);

    const result = await login(form.email, form.password);

    if (result.success) {
      const role = result.role;

      const redirectMap = {
        ADMIN: '/dashboard',
        STAFF: '/staff/tables',
        KITCHEN: '/kitchen/queue',
        CUSTOMER: '/customer/menu'
      };

      window.location.href = redirectMap[role] || '/customer/menu';
    } else {
      setError(result.message || 'Email hoặc mật khẩu không đúng');
    }

    setLoading(false);
  };

  return (
    <div className="login-page">
      <div className="login-card">
        <div className="login-logo">🍜</div>

        <h1 className="login-title">Cái Gì Cũng Không Có</h1>

        <p className="login-subtitle">
          Đăng nhập vào hệ thống quản lý
        </p>

        <form onSubmit={handleSubmit} className="login-form">
          <div className="form-group">
            <label className="form-label">Email</label>

            <input
              className="form-input"
              type="email"
              placeholder="Nhập email"
              value={form.email}
              onChange={(e) =>
                setForm({
                  ...form,
                  email: e.target.value
                })
              }
              required
              autoFocus
            />
          </div>

          <div className="form-group">
            <label className="form-label">Mật khẩu</label>

            <div className="input-wrapper">
              <input
                className="form-input"
                type={showPass ? 'text' : 'password'}
                placeholder="Nhập mật khẩu"
                value={form.password}
                onChange={(e) =>
                  setForm({
                    ...form,
                    password: e.target.value
                  })
                }
                required
              />

              <button
                type="button"
                className="toggle-pass"
                onClick={() => setShowPass(!showPass)}
              >
                {showPass ? '🙈' : '👁️'}
              </button>
            </div>
          </div>

          {error && (
            <div className="login-error">
              ⚠️ {error}
            </div>
          )}

          <button
            type="submit"
            className="login-btn"
            disabled={loading}
          >
            {loading ? <span className="spinner"></span> : 'Đăng nhập'}
          </button>

          <div className="login-links">
            <Link
              to="/forgot-password"
              style={{
                color: '#e85d04',
                fontSize: 13
              }}
            >
              Quên mật khẩu?
            </Link>
          </div>
        </form>

        <p className="login-hint">
          Chưa có tài khoản?{' '}
          <Link
            to="/register"
            style={{
              color: '#e85d04',
              fontWeight: 600
            }}
          >
            Đăng ký ngay
          </Link>
        </p>
      </div>
    </div>
  );
}

export default Login;

