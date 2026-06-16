import { useEffect, useState } from 'react';
import { getAllReservations, updateReservationStatus } from '../../services/reservationService';
import { getAllTables } from '../../services/tableService';

const STATUS_MAP = {
  PENDING:   { label: 'Chờ đến',    cls: 'bg-yellow-100 text-yellow-700', icon: '⏳' },
  CONFIRMED: { label: 'Đã xác nhận',cls: 'bg-blue-100 text-blue-600',    icon: '✅' },
  COMPLETED: { label: 'Đã check-in',cls: 'bg-green-100 text-green-700',  icon: '🪑' },
  CANCELLED: { label: 'Đã hủy',     cls: 'bg-red-100 text-red-500',      icon: '❌' },
};

export default function StaffReservations() {
  const [reservations, setReservations] = useState([]);
  const [tables, setTables] = useState([]);
  const [selected, setSelected] = useState(null);
  const [tableChoice, setTableChoice] = useState('');
  const [filter, setFilter] = useState('all');

  const load = () => {
    getAllReservations().then(({ data }) => setReservations(data)).catch(() => {});
    getAllTables().then(({ data }) => setTables(data)).catch(() => {});
  };
  useEffect(() => { load(); }, []);

  const update = async (id, status) => {
    await updateReservationStatus(id, status);
    load(); setSelected(null); setTableChoice('');
  };

  const filtered = filter === 'all' ? reservations : reservations.filter(r => r.status === filter.toUpperCase());

  const counts = {
    pending:  reservations.filter(r => r.status === 'PENDING').length,
    arrived:  reservations.filter(r => r.status === 'COMPLETED').length,
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-5">
        <h1 className="text-2xl font-bold text-gray-800">Đặt bàn trước</h1>
        <div className="flex gap-3">
          <span className="bg-yellow-100 text-yellow-700 text-sm font-semibold px-3 py-1.5 rounded-full">⏳ {counts.pending} chờ</span>
          <span className="bg-green-100 text-green-700 text-sm font-semibold px-3 py-1.5 rounded-full">✅ {counts.arrived} đã đến</span>
        </div>
      </div>

      <div className="flex gap-2 mb-5 flex-wrap">
        {[['all','Tất cả'],['PENDING','Chờ đến'],['CONFIRMED','Đã xác nhận'],['COMPLETED','Đã check-in'],['CANCELLED','Đã hủy']].map(([v,l]) => (
          <button key={v} onClick={() => setFilter(v)}
            className={`px-3 py-2 rounded-full text-xs font-semibold border transition-colors ${filter === v ? 'bg-[#e85d04] text-white border-[#e85d04]' : 'bg-white text-gray-600 border-gray-200 hover:border-[#e85d04]'}`}>
            {l}
          </button>
        ))}
      </div>

      <div className="space-y-3">
        {filtered.map(r => {
          const s = STATUS_MAP[r.status] || { label: r.status, cls: 'bg-gray-100 text-gray-500', icon: '?' };
          return (
            <div key={r.id} className="bg-white rounded-2xl shadow-sm overflow-hidden">
              <div className="flex items-center gap-4 px-5 py-4 cursor-pointer" onClick={() => setSelected(selected?.id === r.id ? null : r)}>
                <div className="shrink-0">
                  <p className="font-bold text-[#e85d04] text-sm">#{r.id}</p>
                  <span className={`text-xs font-semibold px-2 py-0.5 rounded-full ${s.cls}`}>{s.icon} {s.label}</span>
                </div>
                <div className="flex-1 min-w-0">
                  <p className="font-semibold text-gray-800">👤 {r.guestName}</p>
                  <p className="text-xs text-gray-400">📞 {r.guestPhone}</p>
                </div>
                <div className="text-center shrink-0 text-sm">
                  <p className="text-gray-700">🕐 {new Date(r.reservedAt).toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })}</p>
                  <p className="text-gray-400 text-xs">👥 {r.guestCount} người</p>
                </div>
                <div className="shrink-0 text-sm">
                  {r.tableNumber ? <span className="text-green-600 font-semibold">🪑 Bàn {r.tableNumber}</span> : <span className="text-gray-400">Chưa xếp bàn</span>}
                </div>
                <span className="text-gray-400 text-xs">{selected?.id === r.id ? '▲' : '▼'}</span>
              </div>

              {selected?.id === r.id && (
                <div className="border-t px-5 py-4">
                  {r.note && <p className="text-sm text-gray-600 mb-3">📝 {r.note}</p>}
                  {r.status === 'PENDING' && (
                    <div className="flex flex-wrap gap-3">
                      <select value={tableChoice} onChange={e => setTableChoice(e.target.value)}
                        className="text-sm border rounded-xl px-3 py-2 focus:outline-none focus:ring-1 focus:ring-[#e85d04]">
                        <option value="">-- Chọn bàn --</option>
                        {tables.filter(t => t.status === 'AVAILABLE').map(t => (
                          <option key={t.id} value={t.id}>Bàn {t.tableNumber} ({t.capacity} người)</option>
                        ))}
                      </select>
                      <button onClick={() => update(r.id, 'CONFIRMED')} disabled={!tableChoice}
                        className="bg-green-500 hover:bg-green-600 disabled:opacity-50 text-white text-sm font-semibold px-4 py-2 rounded-xl">
                        ✅ Check-in khách
                      </button>
                      <button onClick={() => update(r.id, 'CANCELLED')}
                        className="bg-red-50 hover:bg-red-100 text-red-500 text-sm font-semibold px-4 py-2 rounded-xl">
                        ❌ Hủy đặt bàn
                      </button>
                    </div>
                  )}
                  {r.status === 'CONFIRMED' && (
                    <div className="flex gap-3">
                      <button onClick={() => update(r.id, 'COMPLETED')}
                        className="bg-green-500 hover:bg-green-600 text-white text-sm font-semibold px-4 py-2 rounded-xl">
                        🪑 Xác nhận đã đến
                      </button>
                      <button onClick={() => update(r.id, 'CANCELLED')}
                        className="bg-red-50 hover:bg-red-100 text-red-500 text-sm font-semibold px-4 py-2 rounded-xl">
                        ❌ Hủy
                      </button>
                    </div>
                  )}
                  {(r.status === 'COMPLETED' || r.status === 'CANCELLED') && (
                    <button onClick={() => update(r.id, 'PENDING')}
                      className="bg-gray-100 hover:bg-gray-200 text-gray-700 text-sm font-semibold px-4 py-2 rounded-xl">
                      ↩ Hoàn tác
                    </button>
                  )}
                </div>
              )}
            </div>
          );
        })}
        {filtered.length === 0 && (
          <div className="text-center py-12 text-gray-400 bg-white rounded-2xl">
            <p className="text-4xl mb-2">📅</p><p>Không có đặt bàn nào</p>
          </div>
        )}
      </div>
    </div>
  );
}
