import { useEffect, useState } from 'react';
import { getAllUsers, deleteUser, updateUser } from '../../services/userService';
import { getInitials, formatDate } from '../../utils/helpers';

const ROLES = ['ROLE_ADMIN', 'ROLE_STAFF', 'ROLE_KITCHEN', 'ROLE_CUSTOMER'];
const roleLabel = { ROLE_ADMIN: 'Admin', ROLE_STAFF: 'Nhân viên', ROLE_KITCHEN: 'Bếp', ROLE_CUSTOMER: 'Khách hàng' };
const roleColor = { ROLE_ADMIN: 'bg-orange-100 text-orange-600', ROLE_STAFF: 'bg-blue-100 text-blue-600', ROLE_KITCHEN: 'bg-yellow-100 text-yellow-700', ROLE_CUSTOMER: 'bg-green-100 text-green-700' };
const roleIcon  = { ROLE_ADMIN: '👑', ROLE_STAFF: '🧑‍💼', ROLE_KITCHEN: '👨‍🍳', ROLE_CUSTOMER: '👤' };

function Toast({ msg, ok }) {
  return (
    <div className={`fixed top-5 right-6 z-50 px-4 py-3 rounded-xl text-sm font-semibold shadow-lg animate-bounce-once ${ok ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-600'}`}>
      {msg}
    </div>
  );
}

export default function UserManagement() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [roleFilter, setRoleFilter] = useState('all');
  const [toast, setToast] = useState(null);
  const [confirmAction, setConfirmAction] = useState(null);

  const showToast = (msg, ok = true) => {
    setToast({ msg, ok });
    setTimeout(() => setToast(null), 2500);
  };

  const load = () => {
    setLoading(true);
    getAllUsers().then(({ data }) => setUsers(data)).catch(() => {}).finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  const handleDelete = async () => {
    if (!confirmAction) return;
    await deleteUser(confirmAction.id);
    setConfirmAction(null);
    showToast('Đã xóa tài khoản');
    load();
  };

  const stats = {
    total: users.length,
    active: users.filter(u => u.status === 'ACTIVE').length,
    banned: users.filter(u => u.status !== 'ACTIVE').length,
    byRole: ROLES.reduce((acc, r) => ({ ...acc, [r]: users.filter(u => u.roles?.includes(r)).length }), {}),
  };

  const filtered = users.filter(u => {
    const matchSearch = u.fullName?.toLowerCase().includes(search.toLowerCase()) || u.email?.toLowerCase().includes(search.toLowerCase());
    const matchRole = roleFilter === 'all' || u.roles?.includes(roleFilter);
    return matchSearch && matchRole;
  });

  return (
    <div>
      {toast && <Toast {...toast} />}

      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-800">Quản lý người dùng</h1>
        </div>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3 mb-5">
        {[
          { label: 'Tổng tài khoản', val: stats.total, color: '#e85d04' },
          { label: 'Đang hoạt động', val: stats.active, color: '#38a169' },
          { label: 'Đã khóa', val: stats.banned, color: '#e53e3e' },
          ...ROLES.map(r => ({ label: `${roleIcon[r]} ${roleLabel[r]}`, val: stats.byRole[r], color: '#3182ce' }))
        ].map((s, i) => (
          <div key={i} className="bg-white rounded-xl p-4 shadow-sm text-center cursor-pointer hover:shadow-md">
            <p className="text-2xl font-extrabold" style={{ color: s.color }}>{s.val}</p>
            <p className="text-xs text-gray-500 mt-1">{s.label}</p>
          </div>
        ))}
      </div>

      {/* Toolbar */}
      <div className="flex flex-wrap gap-3 mb-4">
        <input
          className="px-4 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-[#e85d04] bg-white min-w-64"
          placeholder="🔍 Tìm theo tên hoặc email..."
          value={search} onChange={e => setSearch(e.target.value)}
        />
        <div className="flex gap-2 flex-wrap">
          {['all', ...ROLES].map(r => (
            <button key={r}
              onClick={() => setRoleFilter(r)}
              className={`px-3 py-2 rounded-full text-xs font-semibold border transition-colors ${roleFilter === r ? 'bg-[#e85d04] text-white border-[#e85d04]' : 'bg-white text-gray-600 border-gray-200 hover:border-[#e85d04]'}`}>
              {r === 'all' ? 'Tất cả' : `${roleIcon[r]} ${roleLabel[r]}`}
            </button>
          ))}
        </div>
      </div>

      {/* Table */}
      <div className="bg-white rounded-2xl shadow-sm overflow-hidden">
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-gray-50 text-left text-gray-500 text-xs uppercase tracking-wider">
              <th className="px-5 py-4 font-semibold">Người dùng</th>
              <th className="px-5 py-4 font-semibold">Vai trò</th>
              <th className="px-5 py-4 font-semibold">SĐT</th>
              <th className="px-5 py-4 font-semibold">Trạng thái</th>
              <th className="px-5 py-4 font-semibold">Ngày tham gia</th>
              <th className="px-5 py-4 font-semibold text-right">Thao tác</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-50">
            {filtered.map(user => (
              <tr key={user.id} className="hover:bg-gray-50 transition-colors">
                <td className="px-5 py-4">
                  <div className="flex items-center gap-3">
                    <div className="w-9 h-9 rounded-full bg-orange-100 text-orange-600 flex items-center justify-center font-bold text-xs shrink-0">
                      {getInitials(user.fullName)}
                    </div>
                    <div>
                      <p className="font-semibold text-gray-800">{user.fullName}</p>
                      <p className="text-gray-400 text-xs">{user.email}</p>
                    </div>
                  </div>
                </td>
                <td className="px-5 py-4">
                  <div className="flex flex-wrap gap-1">
                    {user.roles?.map(r => (
                      <span key={r} className={`text-xs font-semibold px-2 py-0.5 rounded-full ${roleColor[r] || 'bg-gray-100 text-gray-500'}`}>
                        {roleIcon[r]} {roleLabel[r] || r.replace('ROLE_', '')}
                      </span>
                    ))}
                  </div>
                </td>
                <td className="px-5 py-4 text-gray-500">{user.phone || '—'}</td>
                <td className="px-5 py-4">
                  <span className={`inline-flex items-center gap-1 text-xs font-semibold px-2.5 py-1 rounded-full ${user.status === 'ACTIVE' ? 'bg-green-100 text-green-600' : 'bg-red-100 text-red-500'}`}>
                    <span className={`w-1.5 h-1.5 rounded-full ${user.status === 'ACTIVE' ? 'bg-green-500' : 'bg-red-400'}`}></span>
                    {user.status === 'ACTIVE' ? 'Hoạt động' : 'Đã khóa'}
                  </span>
                </td>
                <td className="px-5 py-4 text-gray-400 text-xs">{formatDate(user.createdAt)}</td>
                <td className="px-5 py-4 text-right">
                  <button
                    onClick={() => setConfirmAction({ id: user.id, name: user.fullName })}
                    className="text-xs text-red-400 hover:text-red-600 font-medium px-2 py-1 rounded hover:bg-red-50">
                    🗑️ Xóa
                  </button>
                </td>
              </tr>
            ))}
            {filtered.length === 0 && (
              <tr><td colSpan={6} className="text-center py-12 text-gray-400">
                <p className="text-4xl mb-2">👥</p><p>Không có tài khoản nào</p>
              </td></tr>
            )}
          </tbody>
        </table>
      </div>

      {/* Confirm delete */}
      {confirmAction && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
          <div className="bg-white rounded-2xl p-6 shadow-xl w-80 text-center">
            <div className="text-4xl mb-3">🗑️</div>
            <p className="font-semibold text-gray-800 mb-1">Xóa tài khoản "{confirmAction.name}"?</p>
            <p className="text-sm text-red-500 mb-5">Hành động này không thể hoàn tác!</p>
            <div className="flex gap-3">
              <button onClick={handleDelete} className="flex-1 bg-red-500 hover:bg-red-600 text-white font-semibold py-2.5 rounded-xl text-sm">Xác nhận</button>
              <button onClick={() => setConfirmAction(null)} className="flex-1 bg-gray-100 hover:bg-gray-200 text-gray-700 font-semibold py-2.5 rounded-xl text-sm">Hủy</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
