import { useEffect, useState } from 'react';
import { getAllUsers, deleteUser } from '../../services/userService';
import SearchBar from '../../components/common/SearchBar';
import Loading from '../../components/common/Loading';
import { getInitials, formatDate } from '../../utils/helpers';

const StatusBadge = ({ status }) => (
  <span className={`inline-flex items-center gap-1 text-xs font-semibold px-2.5 py-1 rounded-full ${status === 'ACTIVE' ? 'bg-green-100 text-green-600' : 'bg-gray-100 text-gray-500'}`}>
    <span className={`w-1.5 h-1.5 rounded-full ${status === 'ACTIVE' ? 'bg-green-500' : 'bg-gray-400'}`} />
    {status === 'ACTIVE' ? 'Hoạt động' : 'Vô hiệu'}
  </span>
);

const UserManagement = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');

  const load = () => {
    setLoading(true);
    getAllUsers().then(({ data }) => setUsers(data)).catch(() => {}).finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  const handleDelete = async (id) => {
    if (!confirm('Xóa người dùng này?')) return;
    await deleteUser(id);
    load();
  };

  const filtered = users.filter((u) =>
    u.name?.toLowerCase().includes(search.toLowerCase()) ||
    u.email?.toLowerCase().includes(search.toLowerCase())
  );

  if (loading) return <Loading />;

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h2 className="text-2xl font-bold text-gray-800">Quản lý Users</h2>
          <p className="text-sm text-gray-500 mt-1">{users.length} người dùng</p>
        </div>
      </div>

      <div className="mb-5 max-w-sm">
        <SearchBar value={search} onChange={setSearch} placeholder="Tìm theo tên hoặc email..." />
      </div>

      <div className="bg-white rounded-2xl shadow-sm overflow-hidden">
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-gray-50 text-left text-gray-500 text-xs uppercase tracking-wider">
              <th className="px-5 py-4 font-semibold">Người dùng</th>
              <th className="px-5 py-4 font-semibold">Vai trò</th>
              <th className="px-5 py-4 font-semibold">Trạng thái</th>
              <th className="px-5 py-4 font-semibold">Ngày tham gia</th>
              <th className="px-5 py-4 font-semibold text-right">Hành động</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-50">
            {filtered.map((user) => (
              <tr key={user.id} className="hover:bg-gray-50 transition-colors">
                <td className="px-5 py-4">
                  <div className="flex items-center gap-3">
                    <div className="w-9 h-9 rounded-full bg-orange-100 text-orange-600 flex items-center justify-center font-bold text-xs shrink-0">{getInitials(user.name)}</div>
                    <div>
                      <p className="font-medium text-gray-800">{user.name}</p>
                      <p className="text-gray-400 text-xs">{user.email}</p>
                    </div>
                  </div>
                </td>
                <td className="px-5 py-4">
                  <span className={`text-xs font-semibold px-2.5 py-1 rounded-full ${user.role === 'ADMIN' ? 'bg-orange-100 text-orange-600' : 'bg-blue-50 text-blue-500'}`}>{user.role}</span>
                </td>
                <td className="px-5 py-4"><StatusBadge status={user.status} /></td>
                <td className="px-5 py-4 text-gray-500">{formatDate(user.joinDate)}</td>
                <td className="px-5 py-4 text-right">
                  <button onClick={() => handleDelete(user.id)} className="text-xs text-red-400 hover:text-red-600 font-medium px-2 py-1 rounded hover:bg-red-50 transition-colors">Xóa</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {filtered.length === 0 && (
          <div className="text-center py-12 text-gray-400"><p className="text-4xl mb-2">👥</p><p>Không tìm thấy người dùng</p></div>
        )}
      </div>
    </div>
  );
};

export default UserManagement;
