import { useEffect, useState } from 'react';
import { getAllTables, createTable, updateTableStatus, deleteTable } from '../../services/tableService';

const STATUS_LABEL = { AVAILABLE: 'Trống', OCCUPIED: 'Có khách', RESERVED: 'Đặt trước' };
const STATUS_COLOR = {
  AVAILABLE: 'bg-green-100 text-green-700 border-green-200',
  OCCUPIED:  'bg-red-100 text-red-600 border-red-200',
  RESERVED:  'bg-yellow-100 text-yellow-700 border-yellow-200',
};
const DOT_COLOR = { AVAILABLE: 'bg-green-500', OCCUPIED: 'bg-red-500', RESERVED: 'bg-yellow-500' };

export default function TableManagement() {
  const [tables, setTables] = useState([]);
  const [filter, setFilter] = useState('all');
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({ tableNumber: '', capacity: 4, location: '' });
  const [selected, setSelected] = useState(null);

  const load = () => getAllTables().then(({ data }) => setTables(data)).catch(() => {});
  useEffect(() => { load(); }, []);

  const handleCreate = async (e) => {
    e.preventDefault();
    await createTable({ ...form, capacity: Number(form.capacity) });
    setForm({ tableNumber: '', capacity: 4, location: '' });
    setShowForm(false);
    load();
  };

  const handleStatus = async (id, status) => {
    await updateTableStatus(id, status);
    load();
    setSelected(null);
  };

  const handleDelete = async (id) => {
    if (!confirm('Xóa bàn này?')) return;
    await deleteTable(id);
    load();
    setSelected(null);
  };

  const filtered = filter === 'all' ? tables : tables.filter(t => t.status === filter);
  const counts = { AVAILABLE: tables.filter(t => t.status === 'AVAILABLE').length, OCCUPIED: tables.filter(t => t.status === 'OCCUPIED').length, RESERVED: tables.filter(t => t.status === 'RESERVED').length };

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-800">Quản lý bàn</h1>
          <div className="flex gap-4 mt-1">
            {Object.entries(STATUS_LABEL).map(([k, l]) => (
              <span key={k} className="flex items-center gap-1.5 text-xs text-gray-500">
                <span className={`w-2 h-2 rounded-full ${DOT_COLOR[k]}`}></span>
                {l}: <strong>{counts[k]}</strong>
              </span>
            ))}
          </div>
        </div>
        <button onClick={() => setShowForm(!showForm)}
          className="bg-[#e85d04] hover:bg-[#c44d00] text-white text-sm font-semibold px-4 py-2.5 rounded-xl">
          {showForm ? 'Hủy' : '+ Thêm bàn'}
        </button>
      </div>

      {showForm && (
        <form onSubmit={handleCreate} className="bg-white rounded-2xl p-5 shadow-sm mb-5 flex gap-3 flex-wrap">
          <input value={form.tableNumber} onChange={e => setForm({...form, tableNumber: e.target.value})}
            placeholder="Số bàn (VD: T11)" required
            className="flex-1 px-4 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-[#e85d04] min-w-32" />
          <input type="number" min="1" value={form.capacity} onChange={e => setForm({...form, capacity: e.target.value})}
            placeholder="Sức chứa"
            className="w-28 px-4 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-[#e85d04]" />
          <input value={form.location} onChange={e => setForm({...form, location: e.target.value})}
            placeholder="Vị trí (VD: Tầng 1)"
            className="flex-1 px-4 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-[#e85d04] min-w-32" />
          <button type="submit" className="bg-[#e85d04] hover:bg-[#c44d00] text-white font-semibold px-5 py-2.5 rounded-xl text-sm">Lưu</button>
        </form>
      )}

      {/* Filter */}
      <div className="flex gap-2 mb-5">
        {[['all','Tất cả'], ['AVAILABLE','Trống'], ['OCCUPIED','Có khách'], ['RESERVED','Đặt trước']].map(([v, l]) => (
          <button key={v} onClick={() => setFilter(v)}
            className={`px-4 py-2 rounded-full text-sm font-semibold border transition-colors ${filter === v ? 'bg-[#e85d04] text-white border-[#e85d04]' : 'bg-white text-gray-600 border-gray-200 hover:border-[#e85d04]'}`}>
            {l}
          </button>
        ))}
      </div>

      {/* Grid */}
      <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4">
        {filtered.map(table => (
          <div key={table.id} onClick={() => setSelected(table)}
            className={`bg-white rounded-2xl p-5 shadow-sm cursor-pointer hover:shadow-md transition-shadow border-2 ${STATUS_COLOR[table.status]}`}>
            <div className="text-3xl mb-2 text-center">🪑</div>
            <h3 className="font-bold text-gray-800 text-center">Bàn {table.tableNumber}</h3>
            <p className="text-xs text-gray-400 text-center mb-2">{table.capacity} người • {table.location}</p>
            <span className={`block text-center text-xs font-semibold px-2 py-1 rounded-full ${
              table.status === 'AVAILABLE' ? 'bg-green-100 text-green-700' :
              table.status === 'OCCUPIED'  ? 'bg-red-100 text-red-600' : 'bg-yellow-100 text-yellow-700'}`}>
              {STATUS_LABEL[table.status]}
            </span>
          </div>
        ))}
      </div>

      {/* Modal */}
      {selected && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50" onClick={() => setSelected(null)}>
          <div className="bg-white rounded-2xl p-6 shadow-xl w-80" onClick={e => e.stopPropagation()}>
            <h2 className="text-lg font-bold text-gray-800 mb-1">Bàn {selected.tableNumber}</h2>
            <p className="text-sm text-gray-500 mb-4">{selected.capacity} người • {selected.location} • <strong>{STATUS_LABEL[selected.status]}</strong></p>

            <div className="grid grid-cols-3 gap-2 mb-4">
              {Object.entries(STATUS_LABEL).map(([k, l]) => (
                <button key={k} onClick={() => handleStatus(selected.id, k)}
                  className={`py-2 rounded-xl text-xs font-semibold border transition-colors ${selected.status === k ? 'bg-[#e85d04] text-white border-[#e85d04]' : 'bg-gray-50 text-gray-600 border-gray-200 hover:border-[#e85d04]'}`}>
                  {l}
                </button>
              ))}
            </div>

            <div className="flex gap-2">
              <button onClick={() => handleDelete(selected.id)}
                className="flex-1 bg-red-50 hover:bg-red-100 text-red-500 font-semibold py-2 rounded-xl text-sm">
                🗑️ Xóa bàn
              </button>
              <button onClick={() => setSelected(null)}
                className="flex-1 bg-gray-100 hover:bg-gray-200 text-gray-700 font-semibold py-2 rounded-xl text-sm">
                Đóng
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
