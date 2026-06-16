import { useEffect, useState } from 'react';
import { getAllCoupons, createCoupon, deleteCoupon } from '../../services/couponService';
import { formatPrice } from '../../utils/helpers';

export default function CouponManagement() {
  const [coupons, setCoupons] = useState([]);
  const [showAdd, setShowAdd] = useState(false);
  const [form, setForm] = useState({ code: '', discountPercent: '', discountAmount: '', minOrderAmount: '', maxUses: '', expiresAt: '' });

  const load = () => getAllCoupons().then(({ data }) => setCoupons(data)).catch(() => {});
  useEffect(() => { load(); }, []);

  const toggleActive = (id) => setCoupons(prev => prev.map(v => v.id === id ? {...v, isActive: !v.isActive} : v));

  const handleAdd = async (e) => {
    e.preventDefault();
    await createCoupon({
      code: form.code,
      discountPercent: form.discountPercent ? Number(form.discountPercent) : null,
      discountAmount:  form.discountAmount  ? Number(form.discountAmount)  : null,
      minOrderAmount:  form.minOrderAmount  ? Number(form.minOrderAmount)  : null,
      maxUses:         form.maxUses         ? Number(form.maxUses)         : null,
      expiresAt:       form.expiresAt || null,
    });
    setShowAdd(false);
    setForm({ code: '', discountPercent: '', discountAmount: '', minOrderAmount: '', maxUses: '', expiresAt: '' });
    load();
  };

  const handleDelete = async (id) => {
    if (!confirm('Xóa voucher này?')) return;
    await deleteCoupon(id);
    load();
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-800">Quản lý mã giảm giá</h1>
        <button onClick={() => setShowAdd(true)} className="bg-[#e85d04] hover:bg-[#c44d00] text-white text-sm font-semibold px-4 py-2.5 rounded-xl">
          + Tạo voucher
        </button>
      </div>

      {/* Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
        {coupons.map(v => (
          <div key={v.id} className={`bg-white rounded-2xl p-5 shadow-sm ${!v.isActive ? 'opacity-60' : ''}`}>
            <div className="flex items-center justify-between mb-2">
              <span className="font-extrabold text-lg text-gray-800">{v.code}</span>
              {/* Toggle */}
              <label className="relative inline-flex cursor-pointer">
                <input type="checkbox" checked={v.isActive} onChange={() => toggleActive(v.id)} className="sr-only" />
                <div className={`w-10 h-5 rounded-full transition-colors ${v.isActive ? 'bg-[#e85d04]' : 'bg-gray-300'}`}>
                  <div className={`w-4 h-4 bg-white rounded-full shadow absolute top-0.5 transition-transform ${v.isActive ? 'translate-x-5' : 'translate-x-0.5'}`}></div>
                </div>
              </label>
            </div>

            <p className="text-3xl font-extrabold text-[#e85d04] mb-3">
              {v.discountPercent ? `-${v.discountPercent}%` : v.discountAmount ? `-${formatPrice(v.discountAmount)}` : '—'}
            </p>

            <div className="space-y-1 text-xs text-gray-500 mb-3">
              {v.minOrderAmount && <p>Đơn tối thiểu: {formatPrice(v.minOrderAmount)}</p>}
              <p>Đã dùng: {v.usedCount}/{v.maxUses ?? '∞'}</p>
              {v.expiresAt && <p>HSD: {new Date(v.expiresAt).toLocaleDateString('vi-VN')}</p>}
            </div>

            {v.maxUses && (
              <div className="w-full bg-gray-100 rounded-full h-1.5 mb-3">
                <div className="bg-[#e85d04] h-1.5 rounded-full" style={{ width: `${Math.min((v.usedCount / v.maxUses) * 100, 100)}%` }}></div>
              </div>
            )}

            <button onClick={() => handleDelete(v.id)}
              className="text-xs text-red-400 hover:text-red-600 font-medium">
              🗑️ Xóa
            </button>
          </div>
        ))}
      </div>

      {/* Add modal */}
      {showAdd && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4" onClick={() => setShowAdd(false)}>
          <div className="bg-white rounded-2xl p-6 w-full max-w-md shadow-xl" onClick={e => e.stopPropagation()}>
            <h2 className="text-lg font-bold text-gray-800 mb-4">Tạo voucher mới</h2>
            <form onSubmit={handleAdd} className="space-y-4">
              <div className="grid grid-cols-2 gap-3">
                <div className="col-span-2">
                  <label className="block text-sm font-semibold text-gray-700 mb-1.5">Mã voucher</label>
                  <input className="w-full px-3 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-[#e85d04] uppercase"
                    placeholder="VD: SUMMER30" value={form.code}
                    onChange={e => setForm({...form, code: e.target.value.toUpperCase()})} required />
                </div>
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-1.5">Giảm % (để trống nếu dùng tiền)</label>
                  <input type="number" min="1" max="100" className="w-full px-3 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-[#e85d04]"
                    placeholder="10" value={form.discountPercent}
                    onChange={e => setForm({...form, discountPercent: e.target.value, discountAmount: ''})} />
                </div>
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-1.5">Giảm tiền (đ)</label>
                  <input type="number" className="w-full px-3 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-[#e85d04]"
                    placeholder="50000" value={form.discountAmount}
                    onChange={e => setForm({...form, discountAmount: e.target.value, discountPercent: ''})} />
                </div>
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-1.5">Đơn tối thiểu (đ)</label>
                  <input type="number" className="w-full px-3 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-[#e85d04]"
                    placeholder="200000" value={form.minOrderAmount}
                    onChange={e => setForm({...form, minOrderAmount: e.target.value})} />
                </div>
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-1.5">Số lượng tối đa</label>
                  <input type="number" className="w-full px-3 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-[#e85d04]"
                    placeholder="100" value={form.maxUses}
                    onChange={e => setForm({...form, maxUses: e.target.value})} />
                </div>
                <div className="col-span-2">
                  <label className="block text-sm font-semibold text-gray-700 mb-1.5">Hạn sử dụng</label>
                  <input type="datetime-local" className="w-full px-3 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-[#e85d04]"
                    value={form.expiresAt} onChange={e => setForm({...form, expiresAt: e.target.value})} />
                </div>
              </div>
              <div className="flex gap-3">
                <button type="submit" className="flex-1 bg-[#e85d04] hover:bg-[#c44d00] text-white font-semibold py-2.5 rounded-xl text-sm">Tạo voucher</button>
                <button type="button" onClick={() => setShowAdd(false)} className="flex-1 bg-gray-100 hover:bg-gray-200 text-gray-700 font-semibold py-2.5 rounded-xl text-sm">Hủy</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
