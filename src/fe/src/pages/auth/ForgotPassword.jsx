import { useState } from 'react';
import { Link } from 'react-router-dom';

export default function ForgotPassword() {
  const [email, setEmail] = useState('');
  const [sent, setSent] = useState(false);

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-900 via-[#1a1a2e] to-gray-900 px-4 relative overflow-hidden">
      <div className="absolute top-0 left-0 w-32 h-32 border-l-4 border-t-4 border-[#e85d04]/30 rounded-tl-3xl"></div>
      <div className="absolute bottom-0 right-0 w-32 h-32 border-r-4 border-b-4 border-[#e85d04]/30 rounded-br-3xl"></div>
      <div className="w-full max-w-sm">
        <div className="bg-white/95 rounded-3xl shadow-2xl p-8">
          <div className="text-center mb-7">
            <div className="text-5xl mb-3">🔑</div>
            <h1 className="text-xl font-extrabold text-gray-800">Quên mật khẩu</h1>
            <p className="text-gray-500 text-sm mt-1">Nhập email để nhận link đặt lại</p>
          </div>
          {sent ? (
            <div className="text-center">
              <p className="text-green-500 font-semibold mb-4">✓ Email đã được gửi!</p>
              <Link to="/login" className="text-[#e85d04] font-bold hover:underline text-sm">← Quay lại đăng nhập</Link>
            </div>
          ) : (
            <form onSubmit={e => { e.preventDefault(); setSent(true); }} className="space-y-4">
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-1.5">Email</label>
                <input type="email" value={email} onChange={e => setEmail(e.target.value)} placeholder="you@example.com" required
                  className="w-full px-4 py-3 rounded-xl border border-gray-200 bg-gray-50 text-sm focus:outline-none focus:ring-2 focus:ring-[#e85d04]" />
              </div>
              <button type="submit" className="w-full bg-[#e85d04] hover:bg-[#c44d00] text-white font-bold py-3 rounded-xl text-sm">Gửi email</button>
              <p className="text-center text-sm"><Link to="/login" className="text-gray-400 hover:text-[#e85d04]">← Quay lại đăng nhập</Link></p>
            </form>
          )}
        </div>
      </div>
    </div>
  );
}
