import { useEffect, useRef, useState } from 'react';
import { getAllFoods, createFood, updateFood, deleteFood } from '../../services/foodService';
import { getAllCategories, createCategory, deleteCategory } from '../../services/categoryService';
import { formatPrice } from '../../utils/helpers';

export default function FoodManagement() {
  const [tab, setTab] = useState('menu');
  const [foods, setFoods] = useState([]);
  const [categories, setCategories] = useState([]);
  const [search, setSearch] = useState('');
  const [activeCat, setActiveCat] = useState('Tất cả');
  const [showModal, setShowModal] = useState(false);
  const [editItem, setEditItem] = useState(null);
  const [form, setForm] = useState({ name: '', categoryId: '', price: '', imageUrl: '', description: '', status: 'AVAILABLE' });
  const [showCatForm, setShowCatForm] = useState(false);
  const [catForm, setCatForm] = useState({ name: '', description: '' });
  const fileRef = useRef();

  const loadFoods = () => getAllFoods().then(({ data }) => setFoods(data)).catch(() => {});
  const loadCats  = () => getAllCategories().then(({ data }) => setCategories(data)).catch(() => {});

  useEffect(() => { loadFoods(); loadCats(); }, []);

  const handleSaveCat = async (e) => {
    e.preventDefault();
    await createCategory(catForm);
    setCatForm({ name: '', description: '' });
    setShowCatForm(false);
    loadCats();
  };

  const handleDeleteCat = async (id) => {
    if (!confirm('Xóa danh mục này?')) return;
    await deleteCategory(id);
    loadCats();
  };

  const filtered = foods.filter(f => {
    const matchCat = activeCat === 'Tất cả' || f.categoryName === activeCat;
    const matchSearch = f.name?.toLowerCase().includes(search.toLowerCase());
    return matchCat && matchSearch;
  });

  const openAdd = () => {
    setEditItem(null);
    setForm({ name: '', categoryId: categories[0]?.id || '', price: '', imageUrl: '', description: '', status: 'AVAILABLE' });
    setShowModal(true);
  };

  const openEdit = (food) => {
    setEditItem(food);
    setForm({ name: food.name, categoryId: food.categoryId || '', price: food.price, imageUrl: food.imageUrl || '', description: food.description || '', status: food.status });
    setShowModal(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    const payload = { ...form, price: Number(form.price), categoryId: Number(form.categoryId) };
    if (editItem) await updateFood(editItem.id, payload);
    else await createFood(payload);
    setShowModal(false);
    loadFoods();
  };

  const handleDelete = async (id) => {
    if (!confirm('Xóa món này?')) return;
    await deleteFood(id);
    loadFoods();
  };

  const handleToggle = async (food) => {
    const newStatus = food.status === 'AVAILABLE' ? 'UNAVAILABLE' : 'AVAILABLE';
    await updateFood(food.id, { ...food, status: newStatus, categoryId: food.categoryId, price: Number(food.price) });
    loadFoods();
  };

  const handleImageUpload = (e) => {
    const file = e.target.files[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = (ev) => setForm(f => ({ ...f, imageUrl: ev.target.result }));
    reader.readAsDataURL(file);
  };

  const allCats = ['Tất cả', ...categories.map(c => c.name)];

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-800">Thực đơn</h1>
        <div className="flex gap-2">
          {['menu', 'categories'].map(t => (
            <button key={t} onClick={() => setTab(t)}
              className={`px-4 py-2 rounded-xl text-sm font-semibold border transition-colors ${tab === t ? 'bg-[#e85d04] text-white border-[#e85d04]' : 'bg-white text-gray-600 border-gray-200 hover:border-[#e85d04]'}`}>
              {t === 'menu' ? '🍽️ Món ăn' : '📂 Danh mục'}
            </button>
          ))}
          {tab === 'menu' && (
            <button onClick={openAdd} className="bg-[#e85d04] hover:bg-[#c44d00] text-white text-sm font-semibold px-4 py-2 rounded-xl">+ Thêm món</button>
          )}
        </div>
      </div>

      {/* Categories tab */}
      {tab === 'categories' && (
        <div className="bg-white rounded-2xl p-6 shadow-sm">
          <div className="flex items-center justify-between mb-4">
            <h3 className="font-bold text-gray-700">Quản lý danh mục món ăn</h3>
            <button onClick={() => setShowCatForm(!showCatForm)} className="bg-[#e85d04] text-white text-sm font-semibold px-4 py-2 rounded-xl">+ Thêm</button>
          </div>
          {showCatForm && (
            <form onSubmit={handleSaveCat} className="flex gap-3 mb-4">
              <input className="flex-1 px-4 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-[#e85d04]"
                placeholder="Tên danh mục mới..." value={catForm.name}
                onChange={e => setCatForm({...catForm, name: e.target.value})} required />
              <input className="flex-1 px-4 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-[#e85d04]"
                placeholder="Mô tả..." value={catForm.description}
                onChange={e => setCatForm({...catForm, description: e.target.value})} />
              <button type="submit" className="bg-[#e85d04] text-white text-sm font-semibold px-4 py-2.5 rounded-xl">Lưu</button>
            </form>
          )}
          <div className="space-y-2">
            {categories.map(cat => (
              <div key={cat.id} className="flex items-center justify-between px-4 py-3 bg-gray-50 rounded-xl">
                <div>
                  <span className="font-semibold text-gray-800">📂 {cat.name}</span>
                  <span className="text-xs text-gray-400 ml-3">{foods.filter(f => f.categoryId === cat.id).length} món</span>
                </div>
                <button onClick={() => handleDeleteCat(cat.id)} className="text-xs text-red-400 hover:text-red-600 px-2 py-1 rounded hover:bg-red-50">🗑️</button>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Menu tab */}
      {tab === 'menu' && (
        <>
          <div className="flex flex-wrap gap-3 mb-5">
            <input className="px-4 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-[#e85d04] bg-white min-w-64"
              placeholder="🔍 Tìm món ăn..." value={search} onChange={e => setSearch(e.target.value)} />
            <div className="flex gap-2 flex-wrap">
              {allCats.map(cat => (
                <button key={cat} onClick={() => setActiveCat(cat)}
                  className={`px-3 py-2 rounded-full text-xs font-semibold border transition-colors ${activeCat === cat ? 'bg-[#e85d04] text-white border-[#e85d04]' : 'bg-white text-gray-600 border-gray-200 hover:border-[#e85d04]'}`}>
                  {cat}
                </button>
              ))}
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
            {filtered.map(food => (
              <div key={food.id} className={`bg-white rounded-2xl shadow-sm overflow-hidden ${food.status !== 'AVAILABLE' ? 'opacity-60' : ''}`}>
                <div className="h-40 bg-gray-100 flex items-center justify-center text-5xl relative overflow-hidden">
                  {food.imageUrl
                    ? <img src={food.imageUrl} alt={food.name} className="w-full h-full object-cover" />
                    : '🍽️'}
                  <span className="absolute top-2 left-2 text-xs font-semibold bg-white/90 px-2 py-0.5 rounded-full text-gray-700">
                    {food.categoryName}
                  </span>
                </div>
                <div className="p-3">
                  <h3 className="font-semibold text-gray-800 text-sm line-clamp-1">{food.name}</h3>
                  <div className="flex items-center justify-between mt-2">
                    <span className="text-[#e85d04] font-bold text-sm">{formatPrice(food.price)}</span>
                    <button onClick={() => handleToggle(food)}
                      className={`text-xs font-semibold px-2 py-1 rounded-full ${food.status === 'AVAILABLE' ? 'bg-green-100 text-green-600' : 'bg-red-100 text-red-500'}`}>
                      {food.status === 'AVAILABLE' ? 'Còn món' : 'Hết món'}
                    </button>
                  </div>
                  <div className="flex gap-2 mt-2">
                    <button onClick={() => openEdit(food)} className="flex-1 text-xs bg-gray-100 hover:bg-gray-200 text-gray-700 font-semibold py-1.5 rounded-lg">✏️ Sửa</button>
                    <button onClick={() => handleDelete(food.id)} className="flex-1 text-xs bg-red-50 hover:bg-red-100 text-red-500 font-semibold py-1.5 rounded-lg">🗑️ Xóa</button>
                  </div>
                </div>
              </div>
            ))}
            {filtered.length === 0 && (
              <div className="col-span-4 text-center py-12 text-gray-400">
                <p className="text-4xl mb-2">🍽️</p><p>Không có món ăn nào</p>
              </div>
            )}
          </div>
        </>
      )}

      {/* Modal thêm/sửa */}
      {showModal && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4" onClick={() => setShowModal(false)}>
          <div className="bg-white rounded-2xl p-6 w-full max-w-lg shadow-xl" onClick={e => e.stopPropagation()}>
            <h2 className="text-lg font-bold text-gray-800 mb-4">{editItem ? 'Chỉnh sửa món' : 'Thêm món mới'}</h2>
            <form onSubmit={handleSave} className="space-y-4">
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-1.5">Hình ảnh</label>
                <div className="h-32 bg-gray-100 rounded-xl flex items-center justify-center cursor-pointer overflow-hidden" onClick={() => fileRef.current.click()}>
                  {form.imageUrl
                    ? <img src={form.imageUrl} alt="preview" className="w-full h-full object-cover" />
                    : <div className="text-center text-gray-400 text-sm"><p className="text-3xl mb-1">🍽️</p><p>Click để upload ảnh</p></div>}
                </div>
                <input ref={fileRef} type="file" accept="image/*" className="hidden" onChange={handleImageUpload} />
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-1.5">Tên món</label>
                  <input className="w-full px-3 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-[#e85d04]"
                    value={form.name} onChange={e => setForm({...form, name: e.target.value})} required />
                </div>
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-1.5">Danh mục</label>
                  <select className="w-full px-3 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-[#e85d04]"
                    value={form.categoryId} onChange={e => setForm({...form, categoryId: e.target.value})} required>
                    <option value="">-- Chọn --</option>
                    {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-1.5">Giá (đ)</label>
                  <input type="number" className="w-full px-3 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-[#e85d04]"
                    value={form.price} onChange={e => setForm({...form, price: e.target.value})} required />
                </div>
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-1.5">Trạng thái</label>
                  <select className="w-full px-3 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-[#e85d04]"
                    value={form.status} onChange={e => setForm({...form, status: e.target.value})}>
                    <option value="AVAILABLE">Còn món</option>
                    <option value="UNAVAILABLE">Hết món</option>
                  </select>
                </div>
              </div>
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-1.5">Mô tả</label>
                <textarea rows={2} className="w-full px-3 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-[#e85d04] resize-none"
                  value={form.description} onChange={e => setForm({...form, description: e.target.value})} />
              </div>
              <div className="flex gap-3">
                <button type="submit" className="flex-1 bg-[#e85d04] hover:bg-[#c44d00] text-white font-semibold py-2.5 rounded-xl text-sm">
                  {editItem ? 'Lưu' : 'Thêm món'}
                </button>
                <button type="button" onClick={() => setShowModal(false)} className="flex-1 bg-gray-100 hover:bg-gray-200 text-gray-700 font-semibold py-2.5 rounded-xl text-sm">Hủy</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
