import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { createReservation, getMyReservations, cancelReservation } from '../../services/reservationService';
import { getAvailableTables } from '../../services/tableService';
import Loading from '../../components/common/Loading';
import { formatDate } from '../../utils/helpers';

const STATUS_LABEL = {
  PENDING: { label: 'Chờ xác nhận', color: 'text-yellow-500' },
  CONFIRMED: { label: 'Đã xác nhận', color: 'text-blue-500' },
  CANCELLED: { label: 'Đã hủy', color: 'text-red-400' },
  COMPLETED: { label: 'Hoàn thành', color: 'text-green-600' },
};

const Reservation = () => {
  const [tab, setTab] = useState('new');
  const [tables, setTables] = useState([]);
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(false);
  const [form, setForm] = useState({
    guestName: '', guestPhone: '', guestCount: 2,
    reservedAt: '', tableId: '', note: '',
  });
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    getAvailableTables().then(({ data }) => setTables(data)).catch(() => {});
    loadReservations();
  }, []);

  const loadReservations = () => {
    getMyReservations().then(({ data }) => setReservations(data)).catch(() => {});
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true); setError(null);
    try {
      await createReservation({
        ...form,
        tableId: form.tableId ? Number(form.tableId) : null,
        guestCount: Number(form.guestCount),
      });
      setSuccess(true);
      setForm({ guestName: '', guestPhone: '', guestCount: 2, reservedAt: '', tableId: '', note: '' });
      loadReservations();
      setTimeout(() => setSuccess(false), 3000);
    } catch (err) {
      setError(err.response?.data?.message || 'Đặt bàn thất bại');
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = async (id) => {
    if (!confirm('Hủy đặt bàn này?')) return;
    await cancelReservation(id);
    loadReservations();
  };

  return (
    <div className="max-w-3xl mx-auto px-4 py-10">
      <h1 className="text-3xl font-bold text-gray-800 mb-8">Đặt bàn</h1>

      {/* Tabs */}
      <div className="flex gap-2 mb-6">
        {[{ key: 'new', label: 'Đặt bàn mới' }, { key: 'history', label: 'Lịch sử đặt bàn' }].map((t) => (
          <button key={t.key} onClick={() => setTab(t.key)}
            className={`px-5 py-2.5 rounded-xl text-sm font-semibold transition-colors ${tab === t.key ? 'bg-orange-500 text-white' : 'bg-white text-gray-600 hover:bg-orange-50'}`}>
            {t.label}
          </button>
        ))}
      </div>

      {tab === 'new' ? (
        <div className="bg-white rounded-3xl shadow-sm p-8">
          {success && <div className="bg-green-50 border border-green-200 text-green-600 rounded-xl px-4 py-3 mb-4 text-sm">✓ Đặt bàn thành công! Chúng tôi sẽ xác nhận sớm.</div>}
          <form onSubmit={handleSubmit} className="space-y-5">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
              <div>
                <label className="block text-sm font-medium text-gray-600 mb-1.5">Họ và tên</label>
                <input type="text" value={form.guestName} onChange={e => setForm({...form, guestName: e.target.value})}
                  placeholder="Nguyễn Văn A" required
                  className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-orange-400 text-sm" />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-600 mb-1.5">Số điện thoại</label>
                <input type="tel" value={form.guestPhone} onChange={e => setForm({...form, guestPhone: e.target.value})}
                  placeholder="0909 123 456" required
                  className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-orange-400 text-sm" />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-600 mb-1.5">Số người</label>
                <input type="number" min="1" max="20" value={form.guestCount}
                  onChange={e => setForm({...form, guestCount: e.target.value})} required
                  className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-orange-400 text-sm" />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-600 mb-1.5">Thời gian</label>
                <input type="datetime-local" value={form.reservedAt}
                  onChange={e => setForm({...form, reservedAt: e.target.value})} required
                  className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-orange-400 text-sm" />
              </div>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-600 mb-1.5">Chọn bàn (tuỳ chọn)</label>
              <select value={form.tableId} onChange={e => setForm({...form, tableId: e.target.value})}
                className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-orange-400 text-sm">
                <option value="">-- Hệ thống tự sắp xếp --</option>
                {tables.map((t) => (
                  <option key={t.id} value={t.id}>Bàn {t.tableNumber} ({t.capacity} người) - {t.location}</option>
                ))}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-600 mb-1.5">Ghi chú</label>
              <textarea value={form.note} onChange={e => setForm({...form, note: e.target.value})}
                placeholder="Yêu cầu đặc biệt, dị ứng thực phẩm..." rows={3}
                className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-orange-400 text-sm resize-none" />
            </div>
            {error && <p className="text-red-500 text-sm">{error}</p>}
            <button type="submit" disabled={loading}
              className="w-full bg-orange-500 hover:bg-orange-600 text-white font-bold py-3 rounded-xl transition-colors disabled:opacity-60">
              {loading ? 'Đang đặt bàn...' : 'Xác nhận đặt bàn'}
            </button>
          </form>
        </div>
      ) : (
        <div className="space-y-4">
          {reservations.length === 0 ? (
            <div className="text-center py-12 text-gray-400">
              <div className="text-5xl mb-3">📅</div>
              <p>Chưa có lịch đặt bàn nào</p>
            </div>
          ) : reservations.map((r) => {
            const s = STATUS_LABEL[r.status] || { label: r.status, color: 'text-gray-500' };
            return (
              <div key={r.id} className="bg-white rounded-2xl p-5 shadow-sm">
                <div className="flex items-center justify-between mb-2">
                  <span className="font-bold text-gray-800">#{r.id} — {r.guestName}</span>
                  <span className={`text-sm font-semibold ${s.color}`}>{s.label}</span>
                </div>
                <div className="text-sm text-gray-500 space-y-1">
                  <p>📅 {new Date(r.reservedAt).toLocaleString('vi-VN')}</p>
                  <p>👥 {r.guestCount} người • 📞 {r.guestPhone}</p>
                  {r.tableNumber && <p>🪑 Bàn {r.tableNumber}</p>}
                </div>
                {(r.status === 'PENDING' || r.status === 'CONFIRMED') && (
                  <button onClick={() => handleCancel(r.id)}
                    className="mt-3 text-sm text-red-400 hover:text-red-600 font-medium">
                    Hủy đặt bàn
                  </button>
                )}
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};

export default Reservation;
