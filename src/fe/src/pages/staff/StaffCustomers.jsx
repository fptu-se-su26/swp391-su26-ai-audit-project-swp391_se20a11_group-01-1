import { useEffect, useState } from 'react';
import { getAllUsers } from '../../services/userService';
import { getInitials, formatDate } from '../../utils/helpers';

const getTier = (spend) => {
  if (spend >= 6000000) return { label: 'Kim Cương', color: '#3182ce' };
  if (spend >= 3000000) return { label: 'Bạch Kim',  color: '#805ad5' };
  if (spend >= 1500000) return { label: 'Vàng',      color: '#d69e2e' };
  if (spend >= 500000)  return { label: 'Bạc',       color: '#a0aec0' };
  return { label: 'Mới', color: '#718096' };
};

export default function StaffCustomers() {
  const [customers, setCustomers] = useState([]);
  const [search, setSearch] = useState('');

  useEffect(() => {
    getAllUsers().then(({ data }) => {
      setCustomers(data.filter(u => u.roles?.includes('ROLE_CUSTOMER')));
    }).catch(() => {});
  }, []);

  const filtered = customers.filter(c =>
    c.fullName?.toLowerCase().includes(search.toLowerCase()) ||
    c.phone?.includes(search) || c.email?.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div>
      <div className="flex items-center justify-between mb-5">
        <h1 className="text-2xl font-bold text-gray-800">Khách hàng</h1>
        <span className="text-sm text-gray-500">{filtered.length} khách</span>
      </div>

      <input className="w-full max-w-sm px-4 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-[#e85d04] bg-white mb-5"
        placeholder="🔍 Tìm theo tên, SĐT hoặc email..." value={search} onChange={e => setSearch(e.target.value)} />

      <div className="space-y-3">
        {filtered.map(c => {
          const tier = getTier(0);
          return (
            <div key={c.id} className="bg-white rounded-2xl p-5 shadow-sm flex items-center gap-4">
              <div className="w-12 h-12 rounded-full bg-orange-100 text-orange-600 flex items-center justify-center font-bold text-lg shrink-0">
                {getInitials(c.fullName)}
              </div>
              <div className="flex-1 min-w-0">
                <div className="flex items-center gap-2 mb-0.5">
                  <h3 className="font-semibold text-gray-800">{c.fullName}</h3>
                  <span className="text-xs font-semibold px-2 py-0.5 rounded-full text-white" style={{ background: tier.color }}>⭐ {tier.label}</span>
                </div>
                <div className="flex gap-4 text-xs text-gray-400">
                  {c.phone && <span>📞 {c.phone}</span>}
                  {c.email && <span>✉️ {c.email}</span>}
                  <span>📅 {formatDate(c.createdAt)}</span>
                </div>
              </div>
              <span className={`text-xs font-semibold px-2.5 py-1 rounded-full ${c.status === 'ACTIVE' ? 'bg-green-100 text-green-600' : 'bg-red-100 text-red-500'}`}>
                {c.status === 'ACTIVE' ? 'Hoạt động' : 'Đã khóa'}
              </span>
            </div>
          );
        })}
        {filtered.length === 0 && (
          <div className="text-center py-12 text-gray-400 bg-white rounded-2xl">
            <p className="text-4xl mb-2">👤</p><p>Không có khách hàng nào</p>
          </div>
        )}
      </div>
    </div>
  );
}
