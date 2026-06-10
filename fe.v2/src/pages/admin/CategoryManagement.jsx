import { useEffect, useState } from 'react';
import { getAllCategories, deleteCategory } from '../../services/categoryService';
import SearchBar from '../../components/common/SearchBar';
import Loading from '../../components/common/Loading';

const CategoryManagement = () => {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');

  const load = () => {
    setLoading(true);
    getAllCategories().then(({ data }) => setCategories(data)).catch(() => {}).finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  const handleDelete = async (id) => {
    if (!confirm('Xóa danh mục này?')) return;
    await deleteCategory(id);
    load();
  };

  const filtered = categories.filter((c) => c.name?.toLowerCase().includes(search.toLowerCase()));

  if (loading) return <Loading />;

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h2 className="text-2xl font-bold text-gray-800">Quản lý Danh mục</h2>
          <p className="text-sm text-gray-500 mt-1">{categories.length} danh mục</p>
        </div>
        <button className="bg-orange-500 hover:bg-orange-600 text-white text-sm font-semibold px-4 py-2.5 rounded-xl transition-colors">+ Thêm danh mục</button>
      </div>

      <div className="mb-6 max-w-sm">
        <SearchBar value={search} onChange={setSearch} placeholder="Tìm danh mục..." />
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        {filtered.map((cat) => (
          <div key={cat.id} className="bg-white rounded-2xl p-5 shadow-sm hover:shadow-md transition-shadow">
            <div className="flex items-start justify-between mb-3">
              <div className="flex items-center gap-3">
                <div className="w-12 h-12 rounded-xl bg-orange-50 flex items-center justify-center text-2xl">{cat.icon || '🍴'}</div>
                <div>
                  <h3 className="font-bold text-gray-800">{cat.name}</h3>
                  <p className="text-xs text-gray-400">{cat.foodCount ?? '—'} món ăn</p>
                </div>
              </div>
              <button onClick={() => handleDelete(cat.id)} className="text-xs text-red-400 hover:text-red-600 font-medium px-2 py-1 rounded hover:bg-red-50">Xóa</button>
            </div>
            {cat.description && <p className="text-sm text-gray-500">{cat.description}</p>}
          </div>
        ))}
        {filtered.length === 0 && (
          <div className="col-span-3 text-center py-12 text-gray-400"><p className="text-4xl mb-2">📂</p><p>Không tìm thấy danh mục</p></div>
        )}
      </div>
    </div>
  );
};

export default CategoryManagement;
