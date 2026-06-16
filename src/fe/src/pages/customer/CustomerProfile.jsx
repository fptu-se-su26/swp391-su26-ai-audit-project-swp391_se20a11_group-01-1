import { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { updateUser } from '../../services/userService';
import { getInitials } from '../../utils/helpers';

const TIERS = [
  { label: 'Bạc',       min: 500000,  color: '#a0aec0', code: 'CGKC5',  discount: '5%' },
  { label: 'Vàng',      min: 1500000, color: '#d69e2e', code: 'CGKC10', discount: '10%' },
  { label: 'Bạch Kim',  min: 3000000, color: '#805ad5', code: 'CGKC15', discount: '15%' },
  { label: 'Kim Cương', min: 6000000, color: '#3182ce', code: 'CGKC20', discount: '20%' },
];

export default function CustomerProfile() {
  const { user } = useAuth();
  const [form, setForm] = useState({ fullName: user?.fullName || '', phone: user?.phone || '', address: user?.address || '' });
  const [editing, setEditing] = useState(false);
  const [saved, setSaved] = useState(false);
  const [loading, setLoading] = useState(false);

  const totalSpend = 0;
  const tier = TIERS.slice().reverse().find(t => totalSpend >= t.min);
  const nextTier = TIERS.find(t => totalSpend < t.min);

  const handleSave = async () => {
    setLoading(true);
    try {
      await updateUser(user?.id, form);
      setSaved(true); setEditing(false);
      setTimeout(() => setSaved(false), 2500);
    } catch { /* ignore */ } finally { setLoading(false); }
  };

  return (
    <div className="max-w-4xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold text-gray-800 mb-6">Hồ sơ của tôi</h1>
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Profile card */}
        <div className="space-y-4">
          <div className="bg-white rounded-2xl p-6 shadow-sm text-center">
            <div className="w-20 h-20 rounded-full bg-orange-100 text-orange-500 flex items-center justify-center text-3xl font-bold mx-auto mb-3">
              {getInitials(user?.fullName)}
            </div>
            <h2 className="font-bold text-gray-800 text-lg">{user?.fullName}</h2>
            <p className="text-sm text-gray-400">{user?.email}</p>
            {tier && (
              <span className="inline-block mt-2 text-xs font-bold px-3 py-1 rounded-full text-white" style={{ background: tier.color }}>
                ⭐ {tier.label}
              </span>
            )}
          </div>

          {/* Tier */}
          <div className="bg-white rounded-2xl p-5 shadow-sm">
            <h3 className="font-bold text-gray-700 mb-4">🏆 Hạng thành viên</h3>
            {nextTier && (
              <div className="mb-3">
                <div className="flex justify-between text-xs text-gray-400 mb-1">
                  <span>Tiến độ đến <strong>{nextTier.label}</strong></span>
                  <span>{totalSpend.toLocaleString('vi-VN')}đ / {nextTier.min.toLocaleString('vi-VN')}đ</span>
                </div>
                <div className="w-full bg-gray-100 rounded-full h-2">
                  <div className="bg-[#e85d04] h-2 rounded-full" style={{ width: `${Math.min((totalSpend/nextTier.min)*100,100)}%` }}></div>
                </div>
              </div>
            )}
            <div className="space-y-2">
              {TIERS.map(t => (
                <div key={t.code} className={`flex items-center gap-2 text-sm ${totalSpend >= t.min ? '' : 'opacity-40'}`}>
                  <span className="w-3 h-3 rounded-full shrink-0" style={{ background: t.color }}></span>
                  <span className="flex-1 font-medium">{t.label}</span>
                  <span className="text-xs text-gray-400">≥ {t.min.toLocaleString('vi-VN')}đ</span>
                  <span className="text-xs font-bold" style={{ color: t.color }}>-{t.discount}</span>
                  {totalSpend >= t.min ? <span className="text-green-500 text-xs">✓</span> : <span className="text-xs">🔒</span>}
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Edit form */}
        <div className="lg:col-span-2 bg-white rounded-2xl p-6 shadow-sm">
          <div className="flex items-center justify-between mb-5">
            <h3 className="font-bold text-gray-700">Thông tin cá nhân</h3>
            {saved && <span className="text-sm text-green-500 font-semibold">✓ Đã lưu thành công</span>}
          </div>
          <div className="space-y-4">
            <div>
              <label className="block text-sm font-semibold text-gray-600 mb-1.5">Email</label>
              <input disabled value={user?.email || ''} className="w-full px-4 py-3 rounded-xl border border-gray-100 bg-gray-50 text-gray-500 text-sm cursor-not-allowed" />
            </div>
            {[
              { label: 'Họ và tên', key: 'fullName', type: 'text' },
              { label: 'Số điện thoại', key: 'phone', type: 'tel' },
              { label: 'Địa chỉ', key: 'address', type: 'text' },
            ].map(f => (
              <div key={f.key}>
                <label className="block text-sm font-semibold text-gray-600 mb-1.5">{f.label}</label>
                <input type={f.type} value={form[f.key]} disabled={!editing}
                  onChange={e => setForm({...form, [f.key]: e.target.value})}
                  className={`w-full px-4 py-3 rounded-xl border text-sm transition ${editing ? 'border-orange-300 focus:outline-none focus:ring-2 focus:ring-[#e85d04] bg-white' : 'border-gray-100 bg-gray-50 text-gray-600 cursor-not-allowed'}`} />
              </div>
            ))}
          </div>
          <div className="flex gap-3 mt-6">
            {editing ? (
              <>
                <button onClick={handleSave} disabled={loading} className="bg-[#e85d04] hover:bg-[#c44d00] text-white font-bold px-6 py-2.5 rounded-xl text-sm disabled:opacity-60">
                  {loading ? 'Đang lưu...' : 'Lưu thay đổi'}
                </button>
                <button onClick={() => setEditing(false)} className="bg-gray-100 text-gray-600 font-semibold px-5 py-2.5 rounded-xl text-sm hover:bg-gray-200">Hủy</button>
              </>
            ) : (
              <button onClick={() => setEditing(true)} className="bg-[#e85d04] hover:bg-[#c44d00] text-white font-bold px-6 py-2.5 rounded-xl text-sm">✏️ Chỉnh sửa</button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
