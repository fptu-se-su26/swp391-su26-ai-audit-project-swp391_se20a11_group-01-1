import React, { useEffect, useState, useRef } from 'react';
import { MENU_ITEMS } from '../data/menuData';
import {
  getAllCategories,
  createCategory,
  deleteCategory as deleteCategoryApi
} from '../services/categoryService';
import './Menu.css';

function Menu() {
  const [items, setItems] = useState(MENU_ITEMS);

  // categories bây giờ là data lấy từ backend
  const [categories, setCategories] = useState([]);

  const [activeCategory, setActiveCategory] = useState('Tất cả');
  const [search, setSearch] = useState('');
  const [showAdd, setShowAdd] = useState(false);
  const [editItem, setEditItem] = useState(null);
  const [form, setForm] = useState({
    name: '',
    category: '',
    price: '',
    img: '🍽️',
    desc: '',
    available: true,
    imageUrl: ''
  });

  const [newCat, setNewCat] = useState('');
  const [tab, setTab] = useState('menu');
  const [loadingCategories, setLoadingCategories] = useState(true);

  const fileRef = useRef();

  useEffect(() => {
    fetchCategories();
  }, []);

  const fetchCategories = async () => {
    try {
      setLoadingCategories(true);

      const data = await getAllCategories();

      const activeCategories = data.filter(cat => cat.isActive === true);

      setCategories(activeCategories);

      if (activeCategories.length > 0 && !form.category) {
        setForm(prev => ({
          ...prev,
          category: activeCategories[0].categoryName
        }));
      }
    } catch (error) {
      console.error('Lỗi khi lấy danh mục:', error);
      alert('Không thể tải danh mục từ backend.');
    } finally {
      setLoadingCategories(false);
    }
  };

  const categoryNames = categories.map(cat => cat.categoryName);
  const allCategories = ['Tất cả', ...categoryNames];

  const filtered = items.filter(item => {
    const matchCat = activeCategory === 'Tất cả' || item.category === activeCategory;
    const matchSearch = item.name.toLowerCase().includes(search.toLowerCase());
    return matchCat && matchSearch;
  });

  const openAdd = () => {
    setEditItem(null);
    setForm({
      name: '',
      category: categoryNames[0] || '',
      price: '',
      img: '🍽️',
      desc: '',
      available: true,
      imageUrl: ''
    });
    setShowAdd(true);
  };

  const handleEdit = (item) => {
    setEditItem(item);
    setForm({
      name: item.name,
      category: item.category,
      price: item.price,
      img: item.img,
      desc: item.desc || '',
      available: item.available,
      imageUrl: item.imageUrl || ''
    });
    setShowAdd(true);
  };

  const handleSave = (e) => {
    e.preventDefault();

    if (!form.category) {
      alert('Vui lòng tạo danh mục trước khi thêm món.');
      return;
    }

    if (editItem) {
      setItems(prev =>
        prev.map(i =>
          i.id === editItem.id
            ? { ...i, ...form, price: Number(form.price) }
            : i
        )
      );
    } else {
      setItems(prev => [
        ...prev,
        {
          ...form,
          id: Date.now(),
          price: Number(form.price),
          rating: 0,
          orders: 0
        }
      ]);
    }

    setShowAdd(false);
    setEditItem(null);
  };

  const handleDelete = (id) => {
    if (window.confirm('Xóa món này?')) {
      setItems(prev => prev.filter(i => i.id !== id));
    }
  };

  const toggleAvailable = (id) => {
    setItems(prev =>
      prev.map(i =>
        i.id === id ? { ...i, available: !i.available } : i
      )
    );
  };

  // Upload ảnh — đọc file thành base64, hiện tại vẫn demo frontend
  const handleImageUpload = (e) => {
    const file = e.target.files[0];

    if (!file) {
      return;
    }

    const reader = new FileReader();

    reader.onload = (ev) => {
      setForm(f => ({
        ...f,
        imageUrl: ev.target.result,
        img: ''
      }));
    };

    reader.readAsDataURL(file);
  };

  // Thêm category qua backend
  const addCategory = async (e) => {
    e.preventDefault();

    const categoryName = newCat.trim();

    if (!categoryName) {
      return;
    }

    if (categoryNames.includes(categoryName)) {
      alert('Danh mục này đã tồn tại.');
      return;
    }

    try {
      const savedCategory = await createCategory({
        categoryName: categoryName,
        description: '',
        imageUrl: '',
        isActive: true
      });

      setCategories(prev => [...prev, savedCategory]);
      setNewCat('');

      if (!form.category) {
        setForm(prev => ({
          ...prev,
          category: savedCategory.categoryName
        }));
      }
    } catch (error) {
      console.error('Lỗi khi thêm danh mục:', error);
      alert('Không thể thêm danh mục. Vui lòng kiểm tra backend.');
    }
  };

  // Xóa category qua backend
  const deleteCategory = async (cat) => {
    const selectedCategory = categories.find(c => c.categoryName === cat);

    if (!selectedCategory) {
      alert('Không tìm thấy danh mục.');
      return;
    }

    if (items.some(i => i.category === cat)) {
      alert(`Không thể xóa danh mục "${cat}" vì còn món ăn thuộc danh mục này.`);
      return;
    }

    const confirmDelete = window.confirm(`Bạn có chắc muốn xóa danh mục "${cat}" không?`);

    if (!confirmDelete) {
      return;
    }

    try {
      await deleteCategoryApi(selectedCategory.categoryId);

      setCategories(prev =>
        prev.filter(c => c.categoryId !== selectedCategory.categoryId)
      );

      if (activeCategory === cat) {
        setActiveCategory('Tất cả');
      }

      if (form.category === cat) {
        setForm(prev => ({
          ...prev,
          category: ''
        }));
      }
    } catch (error) {
      console.error('Lỗi khi xóa danh mục:', error);
      alert('Không thể xóa danh mục. Vui lòng kiểm tra backend.');
    }
  };

  return (
    <div className="menu-page">
      <div className="page-header">
        <h1 className="page-title">Thực đơn</h1>

        <div style={{ display: 'flex', gap: 8 }}>
          <button
            className={`tab-btn ${tab === 'menu' ? 'active' : ''}`}
            onClick={() => setTab('menu')}
          >
            🍽️ Món ăn
          </button>

          <button
            className={`tab-btn ${tab === 'categories' ? 'active' : ''}`}
            onClick={() => setTab('categories')}
          >
            📂 Danh mục
          </button>

          {tab === 'menu' && (
            <button className="btn-primary" onClick={openAdd}>
              + Thêm món
            </button>
          )}
        </div>
      </div>

      {/* Tab: Danh mục */}
      {tab === 'categories' && (
        <div className="cat-manager card">
          <h3>Quản lý danh mục món ăn</h3>

          <form onSubmit={addCategory} className="cat-add-form">
            <input
              className="form-input"
              placeholder="Tên danh mục mới..."
              value={newCat}
              onChange={e => setNewCat(e.target.value)}
              required
            />

            <button type="submit" className="btn-primary">
              + Thêm
            </button>
          </form>

          {loadingCategories ? (
            <p>Đang tải danh mục...</p>
          ) : (
            <div className="cat-list">
              {categories.length > 0 ? (
                categories.map(cat => (
                  <div key={cat.categoryId} className="cat-item">
                    <span className="cat-name">
                      📂 {cat.categoryName}
                    </span>

                    <span className="cat-count">
                      {items.filter(i => i.category === cat.categoryName).length} món
                    </span>

                    <button
                      className="cat-del-btn"
                      onClick={() => deleteCategory(cat.categoryName)}
                      title="Xóa danh mục"
                    >
                      🗑️
                    </button>
                  </div>
                ))
              ) : (
                <p>Chưa có danh mục nào.</p>
              )}
            </div>
          )}
        </div>
      )}

      {/* Tab: Món ăn */}
      {tab === 'menu' && (
        <>
          <div className="menu-toolbar">
            <input
              className="search-input"
              placeholder="🔍  Tìm món ăn..."
              value={search}
              onChange={e => setSearch(e.target.value)}
            />

            <div className="filter-tabs">
              {allCategories.map(cat => (
                <button
                  key={cat}
                  className={`filter-tab ${activeCategory === cat ? 'active' : ''}`}
                  onClick={() => setActiveCategory(cat)}
                >
                  {cat}
                </button>
              ))}
            </div>
          </div>

          <div className="menu-grid">
            {filtered.length > 0 ? (
              filtered.map(item => (
                <div
                  key={item.id}
                  className={`menu-card card ${!item.available ? 'unavailable' : ''}`}
                >
                  <div className="menu-img">
                    {item.imageUrl ? (
                      <img
                        src={item.imageUrl}
                        alt={item.name}
                        className="menu-img-photo"
                      />
                    ) : (
                      item.img
                    )}
                  </div>

                  <div className="menu-info">
                    <h3 className="menu-name">{item.name}</h3>
                    <p className="menu-category">{item.category}</p>

                    <div className="menu-footer">
                      <span className="menu-price">
                        {item.price.toLocaleString('vi-VN')}đ
                      </span>

                      <button
                        className={`avail-badge ${item.available ? 'avail-yes' : 'avail-no'}`}
                        onClick={() => toggleAvailable(item.id)}
                      >
                        {item.available ? 'Còn món' : 'Hết món'}
                      </button>
                    </div>
                  </div>

                  <div className="menu-actions">
                    <button
                      className="action-btn edit-btn"
                      onClick={() => handleEdit(item)}
                    >
                      ✏️
                    </button>

                    <button
                      className="action-btn del-btn"
                      onClick={() => handleDelete(item.id)}
                    >
                      🗑️
                    </button>
                  </div>
                </div>
              ))
            ) : (
              <p>Không có món ăn nào.</p>
            )}
          </div>
        </>
      )}

      {/* Modal thêm/sửa món */}
      {showAdd && (
        <div className="modal-overlay" onClick={() => setShowAdd(false)}>
          <div className="menu-modal card" onClick={e => e.stopPropagation()}>
            <h2>{editItem ? 'Chỉnh sửa món' : 'Thêm món mới'}</h2>

            <form onSubmit={handleSave}>
              <div className="form-group">
                <label className="form-label">Hình ảnh món ăn</label>

                <div
                  className="img-upload-area"
                  onClick={() => fileRef.current.click()}
                >
                  {form.imageUrl ? (
                    <img
                      src={form.imageUrl}
                      alt="preview"
                      className="img-preview"
                    />
                  ) : (
                    <div className="img-placeholder">
                      <span style={{ fontSize: 32 }}>
                        {form.img || '🍽️'}
                      </span>
                      <p>Click để upload ảnh</p>
                    </div>
                  )}
                </div>

                <input
                  ref={fileRef}
                  type="file"
                  accept="image/*"
                  style={{ display: 'none' }}
                  onChange={handleImageUpload}
                />

                {form.imageUrl && (
                  <button
                    type="button"
                    className="remove-img-btn"
                    onClick={() =>
                      setForm(f => ({
                        ...f,
                        imageUrl: '',
                        img: '🍽️'
                      }))
                    }
                  >
                    ✕ Xóa ảnh
                  </button>
                )}
              </div>

              <div className="form-row-2">
                <div className="form-group">
                  <label className="form-label">Tên món</label>

                  <input
                    className="form-input"
                    value={form.name}
                    onChange={e =>
                      setForm({
                        ...form,
                        name: e.target.value
                      })
                    }
                    required
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Icon (emoji)</label>

                  <input
                    className="form-input"
                    value={form.img}
                    onChange={e =>
                      setForm({
                        ...form,
                        img: e.target.value
                      })
                    }
                    disabled={!!form.imageUrl}
                    placeholder={form.imageUrl ? 'Đã dùng ảnh' : '🍽️'}
                  />
                </div>
              </div>

              <div className="form-row-2">
                <div className="form-group">
                  <label className="form-label">Danh mục</label>

                  <select
                    className="form-input"
                    value={form.category}
                    onChange={e =>
                      setForm({
                        ...form,
                        category: e.target.value
                      })
                    }
                  >
                    {categoryNames.map(c => (
                      <option key={c} value={c}>
                        {c}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="form-group">
                  <label className="form-label">Giá (đ)</label>

                  <input
                    className="form-input"
                    type="number"
                    value={form.price}
                    onChange={e =>
                      setForm({
                        ...form,
                        price: e.target.value
                      })
                    }
                    required
                  />
                </div>
              </div>

              <div className="form-group">
                <label className="form-label">Mô tả</label>

                <textarea
                  className="form-input"
                  rows={2}
                  value={form.desc}
                  onChange={e =>
                    setForm({
                      ...form,
                      desc: e.target.value
                    })
                  }
                />
              </div>

              <div className="form-group">
                <label
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: 8,
                    fontSize: 13,
                    fontWeight: 600,
                    cursor: 'pointer'
                  }}
                >
                  <input
                    type="checkbox"
                    checked={form.available}
                    onChange={e =>
                      setForm({
                        ...form,
                        available: e.target.checked
                      })
                    }
                  />
                  Còn phục vụ
                </label>
              </div>

              <div className="modal-btns">
                <button type="submit" className="btn-primary">
                  {editItem ? 'Lưu' : 'Thêm'}
                </button>

                <button
                  type="button"
                  className="btn-cancel"
                  onClick={() => setShowAdd(false)}
                >
                  Hủy
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default Menu;