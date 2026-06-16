import { useEffect, useState } from 'react';
import { getAllCategories, createCategory, updateCategory, deleteCategory } from '../../services/categoryService';
import { getAllFoods } from '../../services/foodService';

export default function CategoryManagement() {
  const [categories, setCategories] = useState([]);
  const [foods, setFoods] = useState([]);
  const [showAdd, setShowAdd] = useState(false);
  const [editItem, setEditItem] = useState(null);
  const [form, setForm] = useState({ name: '', description: '' });

  const load = () => {
    getAllCategories().then(({ data }) => setCategories(data)).catch(() => {});
    getAllFoods().then(({ data }) => setFoods(data)).catch(() => {});
  };

  useEffect(() => { load(); }, []);

  const openAdd = () => { setEditItem(null); setForm({ name: '', description: '' }); setShowAdd(true); };
  const openEdit = (cat) => { setEditItem(cat); setForm({ name: cat.name, description: cat.description || '' }); setShowAdd(true); };

  const handleSave = async (e) => {
    e.preventDefault();
    if (editItem) await updateCategory(editItem.id, form);
    else await createCategory(form);
    setShowAdd(false);
    load();
  };

  const handleDelete = async (id) => {
    if (!confirm('Xóa danh mục này?')) return;
    await deleteCategory(id);
    load();
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-800">Quản lý Danh mục</h1>
          <p className="text-sm text-gray-500 mt-1">{categories.length} danh mục</p>
        </div>
        <button onClick={openAdd} className="bg-[#e85d04] hover:bg-[#c44d00] text-white text-sm font-semibold px-4 py-2.5 rounded-xl">
          + Thêm danh mục
        </button>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        {categories.map(cat => {
          const count = foods.filter(f => f.categoryId === cat.id).length;
          return (
            <div key={cat.id} className="bg-white rounded-2xl p-5 shadow-sm hover:shadow-md transition-shadow">
              <div className="flex items-start justify-between mb-3">
                <div className="flex items-center gap-3">
                  <div className="w-12 h-12 rounded-xl bg-orange-50 flex items-center justify-center text-2xl">📂</div>
                  <div>
                    <h3 className="font-bold text-gray-800">{cat.name}</h3>
                    <p className="text-xs text-gray-400">{count} món ăn</p>
                  </div>
                </div>
                <div className="flex gap-1">
                  <button onClick={() => openEdit(cat)} className="text-xs text-blue-500 hover:text-blue-700 font-medium px-2 py-1 rounded hover:bg-blue-50">✏️</button>
                  <button onClick={() => handleDelete(cat.id)} className="text-xs text-red-400 hover:text-red-600 font-medium px-2 py-1 rounded hover:bg-red-50">🗑️</button>
                </div>
              </div>
              {cat.description && <p className="text-sm text-gray-500">{cat.description}</p>}
            </div>
          );
        })}
        {categories.length === 0 && (
          <div className="col-span-3 text-center py-12 text-gray-400 bg-white rounded-2xl">
            <p className="text-4xl mb-2">📂</p><p>Không có danh mục nào</p>
          </div>
        )}
      </div>

      {showAdd && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4" onClick={() => setShowAdd(false)}>
          <div className="bg-white rounded-2xl p-6 w-full max-w-sm shadow-xl" onClick={e => e.stopPropagation()}>
            <h2 className="text-lg font-bold text-gray-800 mb-4">{editItem ? 'Chỉnh sửa danh mục' : 'Thêm danh mục mới'}</h2>
            <form onSubmit={handleSave} className="space-y-4">
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-1.5">Tên danh mục</label>
                <input className="w-full px-3 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-[#e85d04]"
                  value={form.name} onChange={e => setForm({...form, name: e.target.value})} required />
              </div>
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-1.5">Mô tả</label>
                <textarea rows={2} className="w-full px-3 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-[#e85d04] resize-none"
                  value={form.description} onChange={e => setForm({...form, description: e.target.value})} />
              </div>
              <div className="flex gap-3">
                <button type="submit" className="flex-1 bg-[#e85d04] hover:bg-[#c44d00] text-white font-semibold py-2.5 rounded-xl text-sm">{editItem ? 'Lưu' : 'Thêm'}</button>
                <button type="button" onClick={() => setShowAdd(false)} className="flex-1 bg-gray-100 hover:bg-gray-200 text-gray-700 font-semibold py-2.5 rounded-xl text-sm">Hủy</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
