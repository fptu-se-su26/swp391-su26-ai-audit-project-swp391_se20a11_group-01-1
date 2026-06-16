import { useState } from 'react';
import { updateUser } from '../../services/userService';
import { useAuth } from '../../context/AuthContext';

const ProfileForm = () => {
  const { user } = useAuth();
  const [form, setForm] = useState({
    fullName: user?.fullName || '',
    phone: user?.phone || '',
    address: user?.address || '',
  });
  const [editing, setEditing] = useState(false);
  const [loading, setLoading] = useState(false);
  const [saved, setSaved] = useState(false);
  const [error, setError] = useState(null);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSave = async () => {
    setLoading(true);
    setError(null);
    try {
      await updateUser(user.id, form);
      setSaved(true);
      setEditing(false);
      setTimeout(() => setSaved(false), 2500);
    } catch (err) {
      setError(err.response?.data?.message || 'Lưu thất bại');
    } finally {
      setLoading(false);
    }
  };

  const fields = [
    { label: 'Họ và tên', name: 'fullName', type: 'text' },
    { label: 'Số điện thoại', name: 'phone', type: 'tel' },
    { label: 'Địa chỉ', name: 'address', type: 'text' },
  ];

  return (
    <div className="bg-white rounded-3xl shadow-sm p-8">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-5 mb-6">
        {/* Email — read only */}
        <div>
          <label className="block text-sm font-medium text-gray-600 mb-1.5">Email</label>
          <input type="email" value={user?.email || ''} disabled
            className="w-full px-4 py-3 rounded-xl border border-gray-100 bg-gray-50 text-gray-500 text-sm cursor-not-allowed" />
        </div>
        {fields.map((f) => (
          <div key={f.name}>
            <label className="block text-sm font-medium text-gray-600 mb-1.5">{f.label}</label>
            <input type={f.type} name={f.name} value={form[f.name]} onChange={handleChange} disabled={!editing}
              className={`w-full px-4 py-3 rounded-xl border text-sm transition ${
                editing ? 'border-orange-300 focus:outline-none focus:ring-2 focus:ring-orange-400 bg-white' : 'border-gray-100 bg-gray-50 text-gray-600 cursor-not-allowed'
              }`} />
          </div>
        ))}
      </div>

      {error && <p className="text-red-500 text-sm mb-4">{error}</p>}

      <div className="flex items-center gap-3">
        {editing ? (
          <>
            <button onClick={handleSave} disabled={loading}
              className="bg-orange-500 hover:bg-orange-600 text-white font-semibold px-6 py-2.5 rounded-xl transition-colors disabled:opacity-60">
              {loading ? 'Đang lưu...' : 'Lưu thay đổi'}
            </button>
            <button onClick={() => setEditing(false)}
              className="text-sm text-gray-500 px-4 py-2.5 rounded-xl border hover:bg-gray-50 transition-colors">Hủy</button>
          </>
        ) : (
          <button onClick={() => setEditing(true)}
            className="bg-orange-500 hover:bg-orange-600 text-white font-semibold px-6 py-2.5 rounded-xl transition-colors">
            Chỉnh sửa
          </button>
        )}
        {saved && <span className="text-sm text-green-500 font-medium">✓ Đã lưu thành công</span>}
      </div>
    </div>
  );
};

export default ProfileForm;
