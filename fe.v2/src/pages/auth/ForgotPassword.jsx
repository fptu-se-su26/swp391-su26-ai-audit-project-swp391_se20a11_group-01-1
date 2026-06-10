import { useState } from 'react';
import { Link } from 'react-router-dom';

const ForgotPassword = () => {
  const [email, setEmail] = useState('');
  const [sent, setSent] = useState(false);

  const handleSubmit = (e) => {
    e.preventDefault();
    // TODO: call authService.forgotPassword(email)
    setSent(true);
  };

  return (
    <div className="w-full max-w-md">
      <div className="bg-white rounded-3xl shadow-lg p-8">
        <div className="text-center mb-8">
          <div className="text-5xl mb-3">🔑</div>
          <h1 className="text-2xl font-bold text-gray-800">Quên mật khẩu</h1>
          <p className="text-gray-500 text-sm mt-1">Nhập email để nhận link đặt lại mật khẩu</p>
        </div>

        {sent ? (
          <div className="text-center">
            <p className="text-green-500 font-medium mb-4">✓ Email đã được gửi! Kiểm tra hộp thư của bạn.</p>
            <Link to="/login" className="text-orange-500 font-semibold hover:underline text-sm">Quay lại đăng nhập</Link>
          </div>
        ) : (
          <form onSubmit={handleSubmit} className="space-y-5">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1.5">Email</label>
              <input type="email" value={email} onChange={e => setEmail(e.target.value)}
                placeholder="you@example.com" required
                className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-orange-400 text-sm" />
            </div>
            <button type="submit" className="w-full bg-orange-500 hover:bg-orange-600 text-white font-semibold py-3 rounded-xl transition-colors">
              Gửi email
            </button>
            <p className="text-center text-sm">
              <Link to="/login" className="text-gray-500 hover:text-orange-500">← Quay lại đăng nhập</Link>
            </p>
          </form>
        )}
      </div>
    </div>
  );
};

export default ForgotPassword;
