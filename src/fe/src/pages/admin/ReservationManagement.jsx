import { useEffect, useState } from 'react';
import { getAllReservations, updateReservationStatus } from '../../services/reservationService';
import { getAllTables } from '../../services/tableService';

const STATUS_MAP = {
  PENDING:   { label: 'Chờ xác nhận', cls: 'bg-yellow-100 text-yellow-700', icon: '⏳' },
  CONFIRMED: { label: 'Đã xác nhận',  cls: 'bg-blue-100 text-blue-600',    icon: '✅' },
  COMPLETED: { label: 'Đã check-in',  cls: 'bg-green-100 text-green-700',  icon: '🪑' },
  CANCELLED: { label: 'Đã hủy',       cls: 'bg-red-100 text-red-500',      icon: '❌' },
};

export default function ReservationManagement() {
  const [reservations, setReservations] = useState([]);
  const [tables, setTables] = useState([]);
  const [filter, setFilter] = useState('all');
  const [search, setSearch] = useState('');
  const [expanded, setExpanded] = useState(null);
  const [tableChoice, setTableChoice] = useState({});

  const load = () => {
    getAllReservations().then(({ data }) => setReservations(data)).catch(() => {});
    getAllTables().then(({ data }) => setTables(data)).catch(() => {});
  };

  useEffect(() => { load(); }, []);

  const update = async (id, status, extraData = {}) => {
    await updateReservationStatus(id, status);
    load();
    setExpanded(null);
  };

  const filtered = reservations.filter(r => {
    const matchFilter = filter === 'all' || r.status === filter;
    const matchSearch = r.guestName?.toLowerCase().includes(search.toLowerCase()) ||
                        r.guestPhone?.includes(search) || String(r.id).includes(search);
    return matchFilter && matchSearch;
  });

  const stats = {
    total:    reservations.length,
    pending:  reservations.filter(r => r.status === 'PENDING').length,
    confirmed:reservations.filter(r => r.status === 'CONFIRMED').length,
    completed:reservations.filter(r => r.status === 'COMPLETED').length,
    cancelled:reservations.filter(r => r.status === 'CANCELLED').length,
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-800">Quản lý đặt bàn</h1>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-2 sm:grid-cols-5 gap-3 mb-5">
        {[
          { k: 'total',    l: 'Tổng',         c: '#e85d04' },
          { k: 'pending',  l: 'Chờ xác nhận', c: '#d69e2e' },
          { k: 'confirmed',l: 'Đã xác nhận',  c: '#3182ce' },
          { k: 'completed',l: 'Đã check-in',  c: '#38a169' },
          { k: 'cancelled',l: 'Đã hủy',       c: '#e53e3e' },
        ].map(({ k, l, c }) => (
          <div key={k} onClick={() => setFilter(k === 'total' ? 'all' : k.toUpperCase())}
            className="bg-white rounded-xl p-4 shadow-sm text-center cursor-pointer hover:shadow-md">
            <p className="text-2xl font-extrabold" style={{ color: c }}>{stats[k]}</p>
            <p className="text-xs text-gray-500 mt-1">{l}</p>
          </div>
        ))}
      </div>

      {/* Toolbar */}
      <div className="flex flex-wrap gap-3 mb-4">
        <input className="px-4 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-[#e85d04] bg-white min-w-64"
          placeholder="🔍 Tìm theo tên, SĐT, mã đặt bàn..." value={search} onChange={e => setSearch(e.target.value)} />
        <div className="flex gap-2 flex-wrap">
          {[['all','Tất cả'],['PENDING','Chờ xác nhận'],['CONFIRMED','Đã xác nhận'],['COMPLETED','Đã check-in'],['CANCELLED','Đã hủy']].map(([v, l]) => (
            <button key={v} onClick={() => setFilter(v)}
              className={`px-3 py-2 rounded-full text-xs font-semibold border transition-colors ${filter === v ? 'bg-[#e85d04] text-white border-[#e85d04]' : 'bg-white text-gray-600 border-gray-200 hover:border-[#e85d04]'}`}>
              {l}
            </button>
          ))}
        </div>
      </div>

      {/* List */}
      <div className="space-y-3">
        {filtered.map(r => {
          const s = STATUS_MAP[r.status] || { label: r.status, cls: 'bg-gray-100 text-gray-500', icon: '?' };
          return (
            <div key={r.id} className="bg-white rounded-2xl shadow-sm overflow-hidden">
              <div className="flex items-center gap-4 px-5 py-4 cursor-pointer" onClick={() => setExpanded(expanded === r.id ? null : r.id)}>
                <div className="shrink-0">
                  <p className="font-bold text-[#e85d04] text-sm">#{r.id}</p>
                  <span className={`text-xs font-semibold px-2 py-0.5 rounded-full ${s.cls}`}>{s.icon} {s.label}</span>
                </div>
                <div className="flex-1 min-w-0">
                  <p className="font-semibold text-gray-800">{r.guestName}</p>
                  <p className="text-xs text-gray-400">{r.guestPhone}</p>
                </div>
                <div className="text-center shrink-0">
                  <p className="text-sm font-semibold text-gray-700">📅 {new Date(r.reservedAt).toLocaleDateString('vi-VN')}</p>
                  <p className="text-xs text-gray-400">🕐 {new Date(r.reservedAt).toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })}</p>
                </div>
                <div className="text-center shrink-0">
                  <p className="text-sm text-gray-600">👥 {r.guestCount} người</p>
                  {r.tableNumber && <p className="text-xs text-gray-400">🪑 Bàn {r.tableNumber}</p>}
                </div>
                <span className="text-gray-400 text-xs">{expanded === r.id ? '▲' : '▼'}</span>
              </div>

              {expanded === r.id && (
                <div className="border-t px-5 py-4">
                  {r.note && <p className="text-sm text-gray-600 mb-3">📝 {r.note}</p>}

                  <div className="flex flex-wrap gap-3">
                    {r.status === 'PENDING' && (
                      <>
                        <div className="flex gap-2 items-center">
                          <select value={tableChoice[r.id] || ''}
                            onChange={e => setTableChoice(prev => ({ ...prev, [r.id]: e.target.value }))}
                            className="text-sm border rounded-lg px-3 py-1.5 focus:outline-none focus:ring-1 focus:ring-[#e85d04]">
                            <option value="">-- Chọn bàn --</option>
                            {tables.filter(t => t.status === 'AVAILABLE').map(t => (
                              <option key={t.id} value={t.id}>Bàn {t.tableNumber} ({t.capacity} người)</option>
                            ))}
                          </select>
                          <button onClick={() => update(r.id, 'CONFIRMED')}
                            disabled={!tableChoice[r.id]}
                            className="bg-blue-500 hover:bg-blue-600 disabled:opacity-50 text-white text-sm font-semibold px-4 py-1.5 rounded-lg">
                            ✅ Xác nhận
                          </button>
                        </div>
                        <button onClick={() => update(r.id, 'CANCELLED')}
                          className="bg-red-50 hover:bg-red-100 text-red-500 text-sm font-semibold px-4 py-1.5 rounded-lg">
                          ❌ Hủy đặt bàn
                        </button>
                      </>
                    )}
                    {r.status === 'CONFIRMED' && (
                      <>
                        <button onClick={() => update(r.id, 'COMPLETED')}
                          className="bg-green-500 hover:bg-green-600 text-white text-sm font-semibold px-4 py-1.5 rounded-lg">
                          🪑 Check-in khách
                        </button>
                        <button onClick={() => update(r.id, 'CANCELLED')}
                          className="bg-red-50 hover:bg-red-100 text-red-500 text-sm font-semibold px-4 py-1.5 rounded-lg">
                          ❌ Hủy
                        </button>
                      </>
                    )}
                    {(r.status === 'COMPLETED' || r.status === 'CANCELLED') && (
                      <button onClick={() => update(r.id, 'PENDING')}
                        className="bg-gray-100 hover:bg-gray-200 text-gray-700 text-sm font-semibold px-4 py-1.5 rounded-lg">
                        ↩ Hoàn tác
                      </button>
                    )}
                  </div>
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
