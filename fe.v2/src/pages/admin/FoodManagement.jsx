import { useEffect, useState } from 'react';
import { getAllFoods, deleteFood } from '../../services/foodService';
import SearchBar from '../../components/common/SearchBar';
import Loading from '../../components/common/Loading';
import { formatPrice } from '../../utils/helpers';

const FoodManagement = () => {
  const [foods, setFoods] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');

  const load = () => {
    setLoading(true);
    getAllFoods().then(({ data }) => setFoods(data)).catch(() => {}).finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  const handleDelete = async (id) => {
    if (!confirm('Xóa món ăn này?')) return;
    await deleteFood(id);
    load();
  };

  const filtered = foods.filter((f) =>
    f.name?.toLowerCase().includes(search.toLowerCase()) ||
    f.categoryName?.toLowerCase().includes(search.toLowerCase())
  );

  if (loading) return <Loading />;

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h2 className="text-2xl font-bold text-gray-800">Quản lý Món ăn</h2>
          <p className="text-sm text-gray-500 mt-1">{foods.length} món ăn</p>
        </div>
        <button className="bg-orange-500 hover:bg-orange-600 text-white text-sm font-semibold px-4 py-2.5 rounded-xl transition-colors">+ Thêm món</button>
      </div>

      <div className="mb-5 max-w-sm">
        <SearchBar value={search} onChange={setSearch} placeholder="Tìm theo tên hoặc danh mục..." />
      </div>

      <div className="bg-white rounded-2xl shadow-sm overflow-hidden">
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-gray-50 text-left text-gray-500 text-xs uppercase tracking-wider">
              <th className="px-5 py-4 font-semibold">Món ăn</th>
              <th className="px-5 py-4 font-semibold">Danh mục</th>
              <th className="px-5 py-4 font-semibold">Giá</th>
              <th className="px-5 py-4 font-semibold">Đánh giá</th>
              <th className="px-5 py-4 font-semibold">Trạng thái</th>
              <th className="px-5 py-4 font-semibold text-right">Hành động</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-50">
            {filtered.map((food) => (
              <tr key={food.id} className="hover:bg-gray-50 transition-colors">
                <td className="px-5 py-4">
                  <div className="flex items-center gap-3">
                    <div className="w-9 h-9 rounded-lg bg-orange-50 flex items-center justify-center text-lg shrink-0">🍜</div>
                    <span className="font-medium text-gray-800">{food.name}</span>
                  </div>
                </td>
                <td className="px-5 py-4"><span className="bg-blue-50 text-blue-500 text-xs font-semibold px-2.5 py-1 rounded-full">{food.categoryName}</span></td>
                <td className="px-5 py-4 font-semibold text-orange-500">{formatPrice(food.price)}</td>
                <td className="px-5 py-4 text-yellow-500 font-semibold">★ {food.rating?.toFixed(1) || '—'}</td>
                <td className="px-5 py-4">
                  <span className={`text-xs font-semibold px-2.5 py-1 rounded-full ${food.status === 'AVAILABLE' ? 'bg-green-100 text-green-600' : 'bg-red-100 text-red-400'}`}>
                    {food.status === 'AVAILABLE' ? 'Còn món' : 'Hết món'}
                  </span>
                </td>
                <td className="px-5 py-4 text-right">
                  <button onClick={() => handleDelete(food.id)} className="text-xs text-red-400 hover:text-red-600 font-medium px-2 py-1 rounded hover:bg-red-50 transition-colors">Xóa</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {filtered.length === 0 && (
          <div className="text-center py-12 text-gray-400"><p className="text-4xl mb-2">🍔</p><p>Không tìm thấy món ăn</p></div>
        )}
      </div>
    </div>
  );
};

export default FoodManagement;
