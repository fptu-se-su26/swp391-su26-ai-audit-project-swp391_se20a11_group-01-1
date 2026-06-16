
import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import API from '../services/api';
import './Login.css';

function ForgotPassword() {
  const [step, setStep] = useState(1);

  const [email, setEmail] = useState('');
  const [otp, setOtp] = useState('');
  const [newPass, setNewPass] = useState('');
  const [confirm, setConfirm] = useState('');

  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSendOtp = async (e) => {
    e.preventDefault();

    setError('');
    setMessage('');
    setOtp('');
    setNewPass('');
    setConfirm('');
    setLoading(true);

    try {
      const response = await API.post('/auth/forgot-password', {
        email: email.trim()
      });

      setMessage(response.data || 'Mã OTP đã được gửi tới email của bạn');
      setOtp('');
      setStep(2);
    } catch (error) {
      console.error('Forgot password error:', error);

      let errorMessage = 'Không thể gửi OTP';

      if (typeof error.response?.data === 'string') {
        errorMessage = error.response.data;
      } else if (error.response?.data?.message) {
        errorMessage = error.response.data.message;
      }

      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const handleOtpChange = (e) => {
    const value = e.target.value.replace(/\D/g, '');
    setOtp(value.slice(0, 6));
  };

  const handleResetPassword = async (e) => {
    e.preventDefault();

    setError('');
    setMessage('');

    if (otp.trim().length !== 6) {
      setError('Mã OTP phải gồm 6 số');
      return;
    }

    if (newPass.length < 6) {
      setError('Mật khẩu phải ít nhất 6 ký tự');
      return;
    }

    if (newPass !== confirm) {
      setError('Mật khẩu xác nhận không khớp');
      return;
    }

    setLoading(true);

    try {
      const response = await API.post('/auth/reset-password', {
        email: email.trim(),
        otp: otp.trim(),
        newPassword: newPass
      });

      setMessage(response.data || 'Đặt lại mật khẩu thành công');
      setStep(3);
    } catch (error) {
      console.error('Reset password error:', error);

      let errorMessage = 'Đặt lại mật khẩu thất bại';

      if (typeof error.response?.data === 'string') {
        errorMessage = error.response.data;
      } else if (error.response?.data?.message) {
        errorMessage = error.response.data.message;
      }

      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-card">
        <div className="login-logo">🔑</div>

        <h1 className="login-title">Quên mật khẩu</h1>

        {step === 1 && (
          <form onSubmit={handleSendOtp} className="login-form" autoComplete="off">
            <p className="login-subtitle">
              Nhập email tài khoản để nhận mã OTP
            </p>

            <div className="form-group">
              <label className="form-label">Email</label>

              <input
                className="form-input"
                type="email"
                name="forgot-email"
                placeholder="example@gmail.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                autoFocus
                autoComplete="email"
              />
            </div>

            {error && <div className="login-error">⚠️ {error}</div>}
            {message && <div className="save-success">✅ {message}</div>}

            <button type="submit" className="login-btn" disabled={loading}>
              {loading ? <span className="spinner"></span> : 'Gửi mã OTP'}
            </button>
          </form>
        )}

        {step === 2 && (
          <form onSubmit={handleResetPassword} className="login-form" autoComplete="off">
            <p className="login-subtitle">
              Nhập OTP đã gửi tới <strong>{email}</strong>
            </p>

            <div className="form-group">
              <label className="form-label">Mã OTP</label>

              <input
                className="form-input"
                type="text"
                name="forgot-otp"
                placeholder="Nhập mã OTP 6 số"
                value={otp}
                onChange={handleOtpChange}
                required
                autoFocus
                maxLength={6}
                inputMode="numeric"
                autoComplete="one-time-code"
              />
            </div>

            <div className="form-group">
              <label className="form-label">Mật khẩu mới</label>

              <input
                className="form-input"
                type="password"
                name="new-password"
                placeholder="Ít nhất 6 ký tự"
                value={newPass}
                onChange={(e) => setNewPass(e.target.value)}
                required
                autoComplete="new-password"
              />
            </div>

            <div className="form-group">
              <label className="form-label">Xác nhận mật khẩu mới</label>

              <input
                className="form-input"
                type="password"
                name="confirm-new-password"
                placeholder="Nhập lại mật khẩu mới"
                value={confirm}
                onChange={(e) => setConfirm(e.target.value)}
                required
                autoComplete="new-password"
              />
            </div>

            {error && <div className="login-error">⚠️ {error}</div>}
            {message && <div className="save-success">✅ {message}</div>}

            <button type="submit" className="login-btn" disabled={loading}>
              {loading ? <span className="spinner"></span> : 'Đặt lại mật khẩu'}
            </button>

            <button
              type="button"
              className="login-btn"
              style={{ marginTop: 8, background: '#718096' }}
              onClick={() => {
                setStep(1);
                setOtp('');
                setNewPass('');
                setConfirm('');
                setError('');
                setMessage('');
              }}
            >
              Đổi email
            </button>
          </form>
        )}

        {step === 3 && (
          <div style={{ textAlign: 'center', padding: '16px 0' }}>
            <div style={{ fontSize: 48, marginBottom: 12 }}>✅</div>

            <p style={{ marginBottom: 8, fontWeight: 700, fontSize: 16 }}>
              Đặt lại mật khẩu thành công!
            </p>

            <p style={{ marginBottom: 24, color: '#718096', fontSize: 13 }}>
              Bạn có thể đăng nhập bằng mật khẩu mới.
            </p>

            <Link
              to="/login"
              className="login-btn"
              style={{ display: 'block', textAlign: 'center' }}
            >
              Đăng nhập ngay
            </Link>
          </div>
        )}

        {step < 3 && (
          <p className="login-hint">
            <Link
              to="/login"
              style={{ color: '#e85d04', fontWeight: 600 }}
            >
              ← Quay lại đăng nhập
            </Link>
          </p>
        )}
      </div>
    </div>
  );
}

export default ForgotPassword;

