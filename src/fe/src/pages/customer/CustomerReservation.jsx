import { useEffect, useState } from 'react';
import { createReservation, getMyReservations, cancelReservation } from '../../services/reservationService';
import { getAvailableTables } from '../../services/tableService';
import { getAllFoods } from '../../services/foodService';
import { getAllCategories } from '../../services/categoryService';
import { formatPrice } from '../../utils/helpers';

const TIME_SLOTS = ['11:00','11:30','12:00','12:30','13:00','17:00','17:30','18:00','18:30','19:00','19:30','20:00','20:30','21:00'];

const STATUS_MAP = {
  PENDING:   { label: 'Chờ xác nhận', cls: 'bg-yellow-100 text-yellow-700', icon: '⏳' },
  CONFIRMED: { label: 'Đã xác nhận',  cls: 'bg-blue-100 text-blue-600',    icon: '✅' },
  COMPLETED: { label: 'Hoàn thành',   cls: 'bg-green-100 text-green-700',  icon: '🪑' },
  CANCELLED: { label: 'Đã hủy',       cls: 'bg-red-100 text-red-500',      icon: '❌' },
};

export default function CustomerReservation() {
  const [step, setStep] = useState(1);
  const [tables, setTables] = useState([]);
  const [foods, setFoods] = useState([]);
  const [categories, setCategories] = useState([]);
  const [reservations, setReservations] = useState([]);
  const [tab, setTab] = useState('new');
  const [form, setForm] = useState({ date: '', time: '', guests: 2, guestName: '', guestPhone: '', note: '', tableId: '' });
  const [preOrder, setPreOrder] = useState({});
  const [activeCat, setActiveCat] = useState('Tất cả');
  const [loading, setLoading] = useState(false);
  const [submitted, setSubmitted] = useState(false);

  useEffect(() => {
    getAvailableTables().then(({ data }) => setTables(data)).catch(() => {});
    getAllFoods().then(({ data }) => setFoods(data)).catch(() => {});
    getAllCategories().then(({ data }) => setCategories(data)).catch(() => {});
    loadReservations();
  }, []);

  const loadReservations = () => getMyReservations().then(({ data }) => setReservations(data)).catch(() => {});

  const updateQty = (id, delta) => {
    setPreOrder(prev => {
      const qty = (prev[id] || 0) + delta;
      if (qty <= 0) { const n = {...prev}; delete n[id]; return n; }
      return { ...prev, [id]: qty };
    });
  };

  const orderedItems = foods.filter(f => preOrder[f.id]);
  const preOrderTotal = orderedItems.reduce((s, f) => s + Number(f.price) * preOrder[f.id], 0);
  const filteredFoods = activeCat === 'Tất cả' ? foods : foods.filter(f => f.categoryName === activeCat);
  const allCats = ['Tất cả', ...categories.map(c => c.name)];

  const handleConfirm = async () => {
    setLoading(true);
    try {
      await createReservation({
        guestName: form.guestName, guestPhone: form.guestPhone,
        guestCount: Number(form.guests),
        reservedAt: `${form.date}T${form.time}:00`,
        tableId: form.tableId ? Number(form.tableId) : null,
        note: form.note,
      });
      setSubmitted(true);
      loadReservations();
    } catch (err) {
      alert(err.response?.data?.message || 'Đặt bàn thất bại');
    } finally { setLoading(false); }
  };

  const handleCancel = async (id) => {
    if (!confirm('Hủy đặt bàn này?')) return;
    await cancelReservation(id);
    loadReservations();
  };

  if (submitted) {
    return (
      <div className="max-w-lg mx-auto px-4 py-20 text-center">
        <div className="text-7xl mb-5">🎉</div>
        <h2 className="text-2xl font-bold text-gray-800 mb-2">Đặt bàn thành công!</h2>
        <p className="text-gray-500 mb-6">Chúng tôi sẽ xác nhận lại qua điện thoại trong vài phút.</p>
        <div className="bg-gray-50 rounded-2xl p-5 text-left space-y-2 mb-6 text-sm">
          <div className="flex justify-between"><span className="text-gray-500">📅 Ngày:</span><strong>{form.date}</strong></div>
          <div className="flex justify-between"><span className="text-gray-500">🕐 Giờ:</span><strong>{form.time}</strong></div>
          <div className="flex justify-between"><span className="text-gray-500">👥 Số người:</span><strong>{form.guests} người</strong></div>
          {orderedItems.length > 0 && <div className="flex justify-between"><span className="text-gray-500">🍽️ Pre-order:</span><strong>{orderedItems.length} món</strong></div>}
        </div>
        <button onClick={() => { setSubmitted(false); setStep(1); setPreOrder({}); setForm({ date:'',time:'',guests:2,guestName:'',guestPhone:'',note:'',tableId:'' }); }}
          className="bg-[#e85d04] hover:bg-[#c44d00] text-white font-bold px-6 py-3 rounded-xl text-sm">Đặt bàn khác</button>
      </div>
    );
  }

  return (
    <div className="max-w-5xl mx-auto px-4 py-8">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-800">Đặt bàn trước</h1>
        <div className="flex gap-2">
          {[['new','Đặt bàn mới'],['history','Lịch sử đặt bàn']].map(([v,l]) => (
            <button key={v} onClick={() => setTab(v)}
              className={`px-4 py-2 rounded-xl text-sm font-semibold border transition-colors ${tab === v ? 'bg-[#e85d04] text-white border-[#e85d04]' : 'bg-white text-gray-600 border-gray-200'}`}>
              {l}
            </button>
          ))}
        </div>
      </div>

      {tab === 'history' ? (
        <div className="space-y-3">
          {reservations.length === 0 ? (
            <div className="text-center py-12 text-gray-400 bg-white rounded-2xl"><p className="text-5xl mb-3">📅</p><p>Chưa có lịch đặt bàn</p></div>
          ) : reservations.map(r => {
            const s = STATUS_MAP[r.status] || { label: r.status, cls: 'bg-gray-100 text-gray-500', icon: '?' };
            return (
              <div key={r.id} className="bg-white rounded-2xl p-5 shadow-sm">
                <div className="flex items-center justify-between mb-2">
                  <span className="font-bold text-gray-800">#{r.id} — {r.guestName}</span>
                  <span className={`text-xs font-semibold px-2.5 py-1 rounded-full ${s.cls}`}>{s.icon} {s.label}</span>
                </div>
                <div className="text-sm text-gray-500 space-y-0.5">
                  <p>📅 {new Date(r.reservedAt).toLocaleString('vi-VN')}</p>
                  <p>👥 {r.guestCount} người • 📞 {r.guestPhone}</p>
                  {r.tableNumber && <p>🪑 Bàn {r.tableNumber}</p>}
                </div>
                {(r.status === 'PENDING' || r.status === 'CONFIRMED') && (
                  <button onClick={() => handleCancel(r.id)} className="mt-3 text-sm text-red-400 hover:text-red-600 font-medium">Hủy đặt bàn</button>
                )}
              </div>
            );
          })}
        </div>
      ) : (
        <>
          {/* Step indicator */}
          <div className="flex items-center gap-3 mb-8">
            {['Thông tin', 'Chọn món', 'Xác nhận'].map((s, i) => (
              <div key={i} className="flex items-center gap-2">
                <div className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-bold transition-colors ${step === i+1 ? 'bg-[#e85d04] text-white' : step > i+1 ? 'bg-green-500 text-white' : 'bg-gray-200 text-gray-500'}`}>
                  {step > i+1 ? '✓' : i+1}
                </div>
                <span className={`text-sm font-medium ${step === i+1 ? 'text-[#e85d04]' : 'text-gray-400'}`}>{s}</span>
                {i < 2 && <div className="flex-1 h-px bg-gray-200 w-8"></div>}
              </div>
            ))}
          </div>

          {/* Step 1 */}
          {step === 1 && (
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
              <div className="lg:col-span-2 bg-white rounded-2xl p-6 shadow-sm">
                <div className="grid grid-cols-2 gap-4 mb-4">
                  <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-1.5">📅 Ngày đặt bàn</label>
                    <input type="date" value={form.date} min={new Date().toISOString().split('T')[0]}
                      onChange={e => setForm({...form, date: e.target.value})} required
                      className="w-full px-3 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-[#e85d04]" />
                  </div>
                  <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-1.5">👥 Số người</label>
                    <select value={form.guests} onChange={e => setForm({...form, guests: e.target.value})}
                      className="w-full px-3 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-[#e85d04]">
                      {[1,2,3,4,5,6,7,8,9,10].map(n => <option key={n} value={n}>{n} người</option>)}
                    </select>
                  </div>
                </div>
                <div className="mb-4">
                  <label className="block text-sm font-semibold text-gray-700 mb-2">🕐 Chọn giờ</label>
                  <div className="flex flex-wrap gap-2">
                    {TIME_SLOTS.map(t => (
                      <button key={t} type="button" onClick={() => setForm({...form, time: t})}
                        className={`px-3 py-2 rounded-xl text-sm font-semibold border transition-colors ${form.time === t ? 'bg-[#e85d04] text-white border-[#e85d04]' : 'bg-white text-gray-600 border-gray-200 hover:border-[#e85d04]'}`}>
                        {t}
                      </button>
                    ))}
                  </div>
                </div>
                <div className="grid grid-cols-2 gap-4 mb-4">
                  <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-1.5">👤 Họ tên</label>
                    <input type="text" value={form.guestName} onChange={e => setForm({...form, guestName: e.target.value})}
                      placeholder="Nguyễn Văn A" required
                      className="w-full px-3 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-[#e85d04]" />
                  </div>
                  <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-1.5">📞 Số điện thoại</label>
                    <input type="tel" value={form.guestPhone} onChange={e => setForm({...form, guestPhone: e.target.value})}
                      placeholder="09xx-xxx-xxx" required
                      className="w-full px-3 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-[#e85d04]" />
                  </div>
                </div>
                <div className="mb-4">
                  <label className="block text-sm font-semibold text-gray-700 mb-1.5">🪑 Chọn bàn (tuỳ chọn)</label>
                  <select value={form.tableId} onChange={e => setForm({...form, tableId: e.target.value})}
                    className="w-full px-3 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-[#e85d04]">
                    <option value="">-- Hệ thống tự sắp xếp --</option>
                    {tables.map(t => <option key={t.id} value={t.id}>Bàn {t.tableNumber} ({t.capacity} người) — {t.location}</option>)}
                  </select>
                </div>
                <div className="mb-5">
                  <label className="block text-sm font-semibold text-gray-700 mb-1.5">📝 Ghi chú</label>
                  <textarea rows={2} value={form.note} onChange={e => setForm({...form, note: e.target.value})}
                    placeholder="Yêu cầu đặc biệt..."
                    className="w-full px-3 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-[#e85d04] resize-none" />
                </div>
                <button onClick={() => setStep(2)} disabled={!form.date || !form.time || !form.guestName || !form.guestPhone}
                  className="w-full bg-[#e85d04] hover:bg-[#c44d00] disabled:opacity-50 text-white font-bold py-3 rounded-xl text-sm">
                  Tiếp theo: Chọn món →
                </button>
              </div>
              <div className="space-y-4">
                <div className="bg-white rounded-2xl p-5 shadow-sm text-sm space-y-2">
                  <h3 className="font-bold text-gray-700 mb-3">📍 Thông tin nhà hàng</h3>
                  <p>🏠 123 Đường ABC, Quận 1, TP.HCM</p>
                  <p>📞 028-xxxx-xxxx</p>
                  <p>🕐 Mở cửa: 10:00 - 24:00</p>
                </div>
                <div className="bg-orange-50 border border-orange-100 rounded-2xl p-5 text-sm">
                  <h3 className="font-bold text-orange-700 mb-3">📋 Lưu ý</h3>
                  <ul className="space-y-1 text-orange-600">
                    <li>• Vui lòng đến đúng giờ đã đặt</li>
                    <li>• Bàn giữ trong 15 phút</li>
                    <li>• Liên hệ trước nếu cần hủy</li>
                  </ul>
                </div>
              </div>
            </div>
          )}

          {/* Step 2 */}
          {step === 2 && (
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
              <div className="lg:col-span-2">
                <div className="flex gap-2 flex-wrap mb-4">
                  {allCats.map(cat => (
                    <button key={cat} onClick={() => setActiveCat(cat)}
                      className={`px-3 py-1.5 rounded-full text-xs font-semibold border transition-colors ${activeCat === cat ? 'bg-[#e85d04] text-white border-[#e85d04]' : 'bg-white text-gray-600 border-gray-200'}`}>
                      {cat}
                    </button>
                  ))}
                </div>
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 max-h-96 overflow-y-auto">
                  {filteredFoods.filter(f => f.status === 'AVAILABLE').map(food => (
                    <div key={food.id} className="bg-white rounded-2xl p-4 shadow-sm flex items-center gap-3">
                      <div className="w-14 h-14 rounded-xl bg-gray-100 shrink-0 overflow-hidden">
                        {food.imageUrl ? <img src={food.imageUrl} alt={food.name} className="w-full h-full object-cover" /> : <div className="w-full h-full flex items-center justify-center text-2xl">🍽️</div>}
                      </div>
                      <div className="flex-1 min-w-0">
                        <h4 className="text-sm font-semibold text-gray-800 truncate">{food.name}</h4>
                        <p className="text-xs text-[#e85d04] font-bold">{formatPrice(food.price)}</p>
                      </div>
                      <div className="flex items-center gap-1">
                        {preOrder[food.id] ? (
                          <>
                            <button onClick={() => updateQty(food.id, -1)} className="w-6 h-6 rounded-full border flex items-center justify-center text-xs">−</button>
                            <span className="w-5 text-center text-sm font-semibold">{preOrder[food.id]}</span>
                            <button onClick={() => updateQty(food.id, 1)} className="w-6 h-6 rounded-full border flex items-center justify-center text-xs">+</button>
                          </>
                        ) : (
                          <button onClick={() => updateQty(food.id, 1)} className="text-xs bg-orange-100 hover:bg-[#e85d04] hover:text-white text-[#e85d04] font-semibold px-3 py-1.5 rounded-full">+ Thêm</button>
                        )}
                      </div>
                    </div>
                  ))}
                </div>
              </div>
              <div className="bg-white rounded-2xl p-5 shadow-sm">
                <h3 className="font-bold text-gray-700 mb-3">🛒 Món đã chọn</h3>
                {orderedItems.length === 0 ? (
                  <p className="text-sm text-gray-400 text-center py-6">Chưa chọn món nào<br /><span className="text-xs">Bạn có thể bỏ qua</span></p>
                ) : (
                  <div className="space-y-2 mb-3">
                    {orderedItems.map(f => (
                      <div key={f.id} className="flex justify-between text-sm">
                        <span>{f.name} ×{preOrder[f.id]}</span>
                        <span className="text-[#e85d04] font-semibold">{formatPrice(Number(f.price) * preOrder[f.id])}</span>
                      </div>
                    ))}
                    <div className="flex justify-between font-bold text-sm border-t pt-2">
                      <span>Tổng dự kiến</span><span className="text-[#e85d04]">{formatPrice(preOrderTotal)}</span>
                    </div>
                  </div>
                )}
                <button onClick={() => setStep(3)} className="w-full bg-[#e85d04] hover:bg-[#c44d00] text-white font-bold py-3 rounded-xl text-sm mb-2">Tiếp theo: Xác nhận →</button>
                <button onClick={() => setStep(1)} className="w-full bg-gray-100 text-gray-600 font-semibold py-2.5 rounded-xl text-sm">← Quay lại</button>
              </div>
            </div>
          )}

          {/* Step 3 */}
          {step === 3 && (
            <div className="max-w-lg">
              <div className="bg-white rounded-2xl p-6 shadow-sm">
                <h3 className="font-bold text-gray-700 mb-4">📋 Xác nhận đặt bàn</h3>
                <div className="space-y-2 text-sm mb-5">
                  {[['📅 Ngày', form.date], ['🕐 Giờ', form.time], ['👥 Số người', `${form.guests} người`], ['👤 Tên', form.guestName], ['📞 SĐT', form.guestPhone], ...(form.note ? [['📝 Ghi chú', form.note]] : [])].map(([k,v]) => (
                    <div key={k} className="flex justify-between"><span className="text-gray-500">{k}</span><strong>{v}</strong></div>
                  ))}
                </div>
                {orderedItems.length > 0 && (
                  <div className="border-t pt-4 mb-5">
                    <h4 className="font-semibold text-gray-700 mb-2 text-sm">🍽️ Món đặt trước</h4>
                    {orderedItems.map(f => (
                      <div key={f.id} className="flex justify-between text-sm mb-1">
                        <span>{f.name} ×{preOrder[f.id]}</span>
                        <span className="text-[#e85d04]">{formatPrice(Number(f.price) * preOrder[f.id])}</span>
                      </div>
                    ))}
                    <div className="flex justify-between font-bold text-sm border-t pt-2 mt-2">
                      <span>Tổng dự kiến</span><span className="text-[#e85d04]">{formatPrice(preOrderTotal)}</span>
                    </div>
                  </div>
                )}
                <button onClick={handleConfirm} disabled={loading}
                  className="w-full bg-[#e85d04] hover:bg-[#c44d00] disabled:opacity-60 text-white font-bold py-3 rounded-xl text-sm mb-2 flex items-center justify-center gap-2">
                  {loading ? <span className="spinner"></span> : '✅ Xác nhận đặt bàn'}
                </button>
                <button onClick={() => setStep(2)} className="w-full bg-gray-100 text-gray-600 font-semibold py-2.5 rounded-xl text-sm">← Quay lại chọn món</button>
              </div>
            </div>
          )}
        </>
      )}
    </div>
  );
}
