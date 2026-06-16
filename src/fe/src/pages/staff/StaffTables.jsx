import { useEffect, useState } from 'react';
import { getAllTables, updateTableStatus } from '../../services/tableService';

const STATUS_LABEL = { AVAILABLE: 'Trống', OCCUPIED: 'Có khách', RESERVED: 'Đặt trước' };
const STATUS_CLS   = { AVAILABLE: 'border-green-400 bg-green-50', OCCUPIED: 'border-red-400 bg-red-50', RESERVED: 'border-yellow-400 bg-yellow-50' };
const DOT_CLS      = { AVAILABLE: 'bg-green-500', OCCUPIED: 'bg-red-500', RESERVED: 'bg-yellow-500' };

export default function StaffTables() {
  const [tables, setTables] = useState([]);
  const [selected, setSelected] = useState(null);
  const [mode, setMode] = useState(null); // 'transfer' | 'merge'
  const [filter, setFilter] = useState('all');

  const load = () => getAllTables().then(({ data }) => setTables(data)).catch(() => {});
  useEffect(() => { load(); }, []);

  const handleStatus = async (id, status) => {
    await updateTableStatus(id, status);
    load(); setSelected(null); setMode(null);
  };

  const handleTransfer = async (targetId) => {
    await updateTableStatus(selected.id, 'AVAILABLE');
    await updateTableStatus(targetId, 'OCCUPIED');
    load(); setSelected(null); setMode(null);
  };

  const filtered = filter === 'all' ? tables : tables.filter(t => t.status === filter);
  const counts = { AVAILABLE: tables.filter(t => t.status === 'AVAILABLE').length, OCCUPIED: tables.filter(t => t.status === 'OCCUPIED').length, RESERVED: tables.filter(t => t.status === 'RESERVED').length };

  return (
    <div>
      <div className="flex items-center justify-between mb-5">
        <h1 className="text-2xl font-bold text-gray-800">Quản lý bàn</h1>
        <div className="flex gap-4">
          {Object.entries(STATUS_LABEL).map(([k, l]) => (
            <span key={k} className="flex items-center gap-1.5 text-sm text-gray-500">
              <span className={`w-2.5 h-2.5 rounded-full ${DOT_CLS[k]}`}></span>
              {l}: <strong>{counts[k]}</strong>
            </span>
          ))}
        </div>
      </div>

      <div className="flex gap-2 mb-5">
        {[['all','Tất cả'],['AVAILABLE','Trống'],['OCCUPIED','Có khách'],['RESERVED','Đặt trước']].map(([v,l]) => (
          <button key={v} onClick={() => setFilter(v)}
            className={`px-4 py-2 rounded-full text-sm font-semibold border transition-colors ${filter === v ? 'bg-[#e85d04] text-white border-[#e85d04]' : 'bg-white text-gray-600 border-gray-200 hover:border-[#e85d04]'}`}>
            {l}
          </button>
        ))}
      </div>

      <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4">
        {filtered.map(table => (
          <div key={table.id} onClick={() => setSelected(table)}
            className={`rounded-2xl p-5 cursor-pointer border-2 hover:shadow-md transition-all ${STATUS_CLS[table.status]}`}>
            <div className="text-3xl text-center mb-2">🪑</div>
            <h3 className="font-bold text-gray-800 text-center text-sm">Bàn {table.tableNumber}</h3>
            <p className="text-xs text-gray-500 text-center">{table.capacity} người</p>
            {table.location && <p className="text-xs text-gray-400 text-center">{table.location}</p>}
            <span className={`mt-2 block text-center text-xs font-semibold px-2 py-1 rounded-full ${
              table.status === 'AVAILABLE' ? 'bg-green-100 text-green-700' :
              table.status === 'OCCUPIED'  ? 'bg-red-100 text-red-600' : 'bg-yellow-100 text-yellow-700'}`}>
              {STATUS_LABEL[table.status]}
            </span>
          </div>
        ))}
      </div>

      {selected && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50" onClick={() => { setSelected(null); setMode(null); }}>
          <div className="bg-white rounded-2xl p-6 w-80 shadow-xl" onClick={e => e.stopPropagation()}>
            {mode === 'transfer' ? (
              <>
                <h2 className="text-lg font-bold text-gray-800 mb-1">🔄 Chuyển bàn</h2>
                <p className="text-sm text-gray-500 mb-4">Chọn bàn trống để chuyển khách từ Bàn {selected.tableNumber}</p>
                <div className="space-y-2 max-h-48 overflow-y-auto mb-4">
                  {tables.filter(t => t.id !== selected.id && t.status === 'AVAILABLE').map(t => (
                    <button key={t.id} onClick={() => handleTransfer(t.id)}
                      className="w-full text-left px-4 py-2.5 bg-green-50 hover:bg-green-100 rounded-xl text-sm font-medium text-gray-700">
                      🪑 Bàn {t.tableNumber} ({t.capacity} chỗ) — {t.location}
                    </button>
                  ))}
                  {tables.filter(t => t.id !== selected.id && t.status === 'AVAILABLE').length === 0 && (
                    <p className="text-center text-gray-400 text-sm py-3">Không có bàn trống</p>
                  )}
                </div>
                <button onClick={() => setMode(null)} className="w-full bg-gray-100 text-gray-700 font-semibold py-2.5 rounded-xl text-sm">← Quay lại</button>
              </>
            ) : (
              <>
                <h2 className="text-lg font-bold text-gray-800 mb-1">Bàn {selected.tableNumber}</h2>
                <p className="text-sm text-gray-500 mb-4">Trạng thái: <strong>{STATUS_LABEL[selected.status]}</strong> • {selected.capacity} người</p>
                <div className="grid grid-cols-3 gap-2 mb-4">
                  {Object.entries(STATUS_LABEL).map(([k, l]) => (
                    <button key={k} onClick={() => handleStatus(selected.id, k)}
                      className={`py-2 rounded-xl text-xs font-semibold border transition-colors ${selected.status === k ? 'bg-[#e85d04] text-white border-[#e85d04]' : 'bg-gray-50 text-gray-600 border-gray-200 hover:border-[#e85d04]'}`}>
                      {l}
                    </button>
                  ))}
                </div>
                {selected.status === 'OCCUPIED' && (
                  <button onClick={() => setMode('transfer')}
                    className="w-full bg-blue-50 hover:bg-blue-100 text-blue-600 font-semibold py-2.5 rounded-xl text-sm mb-2">
                    🔄 Chuyển bàn
                  </button>
                )}
                <button onClick={() => { setSelected(null); setMode(null); }}
                  className="w-full bg-gray-100 hover:bg-gray-200 text-gray-700 font-semibold py-2.5 rounded-xl text-sm">
                  Đóng
                </button>
              </>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
