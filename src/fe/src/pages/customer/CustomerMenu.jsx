import { useEffect, useState } from 'react';
import { getAllFoods } from '../../services/foodService';
import { getAllCategories } from '../../services/categoryService';
import { useCart } from '../../context/CartContext';
import { formatPrice } from '../../utils/helpers';

export default function CustomerMenu() {
  const { addItem, items } = useCart();
  const [foods, setFoods] = useState([]);
  const [categories, setCategories] = useState([]);
  const [category, setCategory] = useState('Tất cả');
  const [search, setSearch] = useState('');
  const [selected, setSelected] = useState(null);
  const [added, setAdded] = useState(null);

  useEffect(() => {
    getAllFoods().then(({ data }) => setFoods(data)).catch(() => {});
    getAllCategories().then(({ data }) => setCategories(data)).catch(() => {});
  }, []);

  const allCats = ['Tất cả', ...categories.map(c => c.name)];
  const filtered = foods.filter(f => {
    const matchCat    = category === 'Tất cả' || f.categoryName === category;
    const matchSearch = f.name?.toLowerCase().includes(search.toLowerCase());
    return matchCat && matchSearch;
  });

  const handleAdd = (food, e) => {
    if (e) e.stopPropagation();
    addItem(food, 1);
    setAdded(food.id);
    setTimeout(() => setAdded(null), 1200);
  };

  const getQty = (id) => items.find(i => i.food?.id === id)?.quantity || 0;

  return (
    <div>
      {/* Hero */}
      <div className="bg-gradient-to-br from-[#1a1a2e] to-gray-800 text-white py-12 px-4">
        <div className="max-w-4xl mx-auto text-center">
          <h1 className="text-3xl font-extrabold mb-2">Thực đơn của chúng tôi</h1>
          <p className="text-gray-400 mb-6">Khám phá các món ăn ngon được chế biến từ nguyên liệu tươi sạch</p>
          <input value={search} onChange={e => setSearch(e.target.value)}
            placeholder="🔍  Tìm món ăn..."
            className="w-full max-w-md px-5 py-3 rounded-full bg-white/10 border border-white/20 text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-[#e85d04] focus:bg-white/20" />
        </div>
      </div>

      {/* Category tabs */}
      <div className="sticky top-16 z-30 bg-white border-b shadow-sm">
        <div className="max-w-6xl mx-auto px-4 py-3 flex gap-2 overflow-x-auto scrollbar-hide">
          {allCats.map(cat => (
            <button key={cat} onClick={() => setCategory(cat)}
              className={`shrink-0 px-4 py-2 rounded-full text-sm font-semibold border transition-colors ${category === cat ? 'bg-[#e85d04] text-white border-[#e85d04]' : 'bg-white text-gray-600 border-gray-200 hover:border-[#e85d04]'}`}>
              {cat}
            </button>
          ))}
        </div>
      </div>

      {/* Grid */}
      <div className="max-w-6xl mx-auto px-4 py-8">
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-5">
          {filtered.map(food => (
            <div key={food.id} onClick={() => setSelected(food)}
              className={`bg-white rounded-2xl shadow-sm hover:shadow-md cursor-pointer overflow-hidden transition-shadow ${food.status !== 'AVAILABLE' ? 'opacity-60' : ''}`}>
              <div className="h-44 bg-gray-100 relative overflow-hidden">
                {food.imageUrl
                  ? <img src={food.imageUrl} alt={food.name} className="w-full h-full object-cover hover:scale-105 transition-transform duration-300" />
                  : <div className="w-full h-full flex items-center justify-center text-6xl">🍽️</div>}
                {food.categoryName && (
                  <span className="absolute top-2 left-2 bg-[#e85d04] text-white text-xs font-semibold px-2 py-0.5 rounded-full">{food.categoryName}</span>
                )}
              </div>
              <div className="p-4">
                <h3 className="font-semibold text-gray-800 mb-1 line-clamp-1">{food.name}</h3>
                <p className="text-xs text-gray-400 line-clamp-2 mb-3">{food.description}</p>
                <div className="flex items-center justify-between">
                  <span className="text-[#e85d04] font-extrabold">{formatPrice(food.price)}</span>
                  {food.status === 'AVAILABLE' ? (
                    <button onClick={(e) => handleAdd(food, e)}
                      className={`text-xs font-bold px-3 py-1.5 rounded-full transition-all ${added === food.id ? 'bg-green-500 text-white' : getQty(food.id) > 0 ? 'bg-[#e85d04] text-white' : 'bg-orange-100 text-[#e85d04] hover:bg-[#e85d04] hover:text-white'}`}>
                      {added === food.id ? '✓ Đã thêm' : getQty(food.id) > 0 ? `+1 (${getQty(food.id)})` : '+ Thêm'}
                    </button>
                  ) : (
                    <span className="text-xs text-red-400 font-semibold">Hết món</span>
                  )}
                </div>
              </div>
            </div>
          ))}
          {filtered.length === 0 && (
            <div className="col-span-4 text-center py-16 text-gray-400">
              <div className="text-6xl mb-3">🍽️</div>
              <p className="text-lg font-medium">Không tìm thấy món ăn</p>
            </div>
          )}
        </div>
      </div>

      {/* Modal chi tiết */}
      {selected && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4" onClick={() => setSelected(null)}>
          <div className="bg-white rounded-3xl w-full max-w-sm shadow-2xl overflow-hidden" onClick={e => e.stopPropagation()}>
            <div className="h-52 bg-gray-100 relative">
              {selected.imageUrl
                ? <img src={selected.imageUrl} alt={selected.name} className="w-full h-full object-cover" />
                : <div className="w-full h-full flex items-center justify-center text-7xl">🍽️</div>}
              <button onClick={() => setSelected(null)} className="absolute top-3 right-3 w-8 h-8 bg-white/90 rounded-full flex items-center justify-center text-gray-600 hover:bg-white">✕</button>
            </div>
            <div className="p-5">
              <span className="text-xs text-[#e85d04] font-semibold">{selected.categoryName}</span>
              <h2 className="text-xl font-extrabold text-gray-800 mt-1 mb-2">{selected.name}</h2>
              <p className="text-sm text-gray-500 mb-4">{selected.description}</p>
              <div className="flex items-center justify-between">
                <span className="text-2xl font-extrabold text-[#e85d04]">{formatPrice(selected.price)}</span>
                {selected.status === 'AVAILABLE' ? (
                  <button onClick={(e) => { handleAdd(selected, e); setSelected(null); }}
                    className="bg-[#e85d04] hover:bg-[#c44d00] text-white font-bold px-5 py-2.5 rounded-xl text-sm">
                    🛒 Thêm vào giỏ
                  </button>
                ) : (
                  <span className="text-red-400 font-semibold text-sm">Hết món</span>
                )}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
