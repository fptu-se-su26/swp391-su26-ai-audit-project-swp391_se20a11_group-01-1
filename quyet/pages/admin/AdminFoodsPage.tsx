import React, { useState, useEffect } from 'react';
import { adminFoodApi } from '../../api/adminFoodApi';
import { adminCategoryApi } from '../../api/adminCategoryApi';
import { getApiErrorMessage } from '../../utils/getApiErrorMessage';
import { formatCurrency } from '../../utils/formatCurrency';
import type { FoodResponse as Food, FoodRequest } from '../../types/food';
import type { CategoryResponse as Category } from '../../types/category';
import type { Page } from '../../types/common';
import './AdminFoodsPage.css';

export const AdminFoodsPage: React.FC = () => {
  const [foodsPage, setFoodsPage] = useState<Page<Food> | null>(null);
  const [categories, setCategories] = useState<Category[]>([]);
  
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  
  const [keyword, setKeyword] = useState('');
  const [categoryId, setCategoryId] = useState('');
  const [page, setPage] = useState(0);

  const [showModal, setShowModal] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [currentId, setCurrentId] = useState<number | null>(null);
  
  const [formData, setFormData] = useState<FoodRequest>({
    categoryId: 0,
    name: '',
    description: '',
    price: 0,
    imageUrl: '',
    isAvailable: true
  });
  
  const [submitting, setSubmitting] = useState(false);
  const [formError, setFormError] = useState<string | null>(null);
  const [updatingId, setUpdatingId] = useState<number | null>(null);

  const fetchFoods = async (pageIndex: number = 0) => {
    setLoading(true);
    setError(null);
    try {
      const parsedCatId = categoryId ? Number(categoryId) : undefined;
      const data = await adminFoodApi.getFoods(pageIndex, 10, keyword, parsedCatId);
      setFoodsPage(data);
      setPage(pageIndex);
    } catch (err) {
      setError(getApiErrorMessage(err, 'Lỗi khi tải danh sách món ăn.'));
    } finally {
      setLoading(false);
    }
  };

  const fetchCategories = async () => {
    try {
      const data = await adminCategoryApi.getCategories();
      setCategories(data);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchCategories();
    fetchFoods(0);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleFilter = (e: React.FormEvent) => {
    e.preventDefault();
    fetchFoods(0);
  };

  const handleOpenCreate = () => {
    setIsEditing(false);
    setCurrentId(null);
    setFormData({
      categoryId: categories.length > 0 ? categories[0].id : 0,
      name: '',
      description: '',
      price: 0,
      imageUrl: '',
      isAvailable: true
    });
    setFormError(null);
    setShowModal(true);
  };

  const handleOpenEdit = (food: Food) => {
    setIsEditing(true);
    setCurrentId(food.id);
    setFormData({
      categoryId: food.categoryId || 0,
      name: food.name,
      description: '', // FoodResponse has no description
      price: food.price,
      imageUrl: food.imageUrl || '',
      isAvailable: food.isAvailable
    });
    setFormError(null);
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
  };

  const handleFormSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (formData.price <= 0) {
      setFormError('Giá món ăn phải lớn hơn 0.');
      return;
    }
    
    if (!formData.categoryId) {
      setFormError('Vui lòng chọn danh mục.');
      return;
    }

    const payload: FoodRequest = {
      ...formData,
      name: formData.name.trim(),
      description: formData.description?.trim(),
      imageUrl: formData.imageUrl?.trim()
    };

    if (!payload.name) {
      setFormError('Tên món ăn không được để trống.');
      return;
    }

    setSubmitting(true);
    setFormError(null);
    try {
      if (isEditing && currentId) {
        await adminFoodApi.updateFood(currentId, payload);
        alert('Cập nhật món ăn thành công!');
      } else {
        await adminFoodApi.createFood(payload);
        alert('Thêm mới món ăn thành công!');
      }
      setShowModal(false);
      fetchFoods(page);
    } catch (err) {
      setFormError(getApiErrorMessage(err, 'Lỗi khi lưu món ăn.'));
    } finally {
      setSubmitting(false);
    }
  };

  const handleToggleStatus = async (food: Food) => {
    const newStatus = !food.isAvailable;
    const actionName = newStatus ? 'mở bán' : 'ngừng bán';
    if (!window.confirm(`Bạn có chắc chắn muốn ${actionName} món ${food.name}?`)) {
      return;
    }

    setUpdatingId(food.id);
    try {
      await adminFoodApi.updateFoodStatus(food.id, newStatus);
      alert(`Cập nhật trạng thái thành công.`);
      fetchFoods(page);
    } catch (err) {
      alert(getApiErrorMessage(err, 'Lỗi khi thay đổi trạng thái món ăn.'));
    } finally {
      setUpdatingId(null);
    }
  };

  return (
    <div className="admin-foods-page">
      <div className="admin-header flex-between">
        <h2>Quản lý Thực đơn</h2>
        <button className="btn-primary" onClick={handleOpenCreate}>+ Thêm món ăn</button>
      </div>

      <div className="filter-section">
        <form onSubmit={handleFilter} className="filter-form">
          <div className="filter-group">
            <label>Từ khóa (Tên, mô tả):</label>
            <input 
              type="text" 
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
              placeholder="Nhập tên món..."
            />
          </div>
          <div className="filter-group">
            <label>Danh mục:</label>
            <select value={categoryId} onChange={(e) => setCategoryId(e.target.value)}>
              <option value="">-- Tất cả --</option>
              {categories.map(c => (
                <option key={c.id} value={c.id}>{c.name}</option>
              ))}
            </select>
          </div>
          <button type="submit" className="btn-filter" disabled={loading}>
            Tìm kiếm
          </button>
        </form>
      </div>

      <div className="foods-list-section mt-20">
        {loading && !foodsPage ? (
          <div className="admin-page-state">Đang tải dữ liệu...</div>
        ) : error ? (
          <div className="admin-page-state text-error">{error}</div>
        ) : !foodsPage || foodsPage.content.length === 0 ? (
          <div className="admin-page-state">Không tìm thấy món ăn nào.</div>
        ) : (
          <>
            <div className="table-responsive">
              <table className="admin-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Ảnh</th>
                    <th>Tên món</th>
                    <th>Danh mục</th>
                    <th>Giá bán</th>
                    <th>Trạng thái</th>
                    <th>Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  {foodsPage.content.map((food: Food) => (
                    <tr key={food.id}>
                      <td>{food.id}</td>
                      <td>
                        {food.imageUrl ? (
                          <img src={food.imageUrl} alt={food.name} className="food-img-preview" />
                        ) : (
                          <span className="no-image">No img</span>
                        )}
                      </td>
                      <td>
                        <strong>{food.name}</strong>
                        {/* no description in FoodResponse */}
                      </td>
                      <td>{food.categoryName}</td>
                      <td className="highlight-price">{formatCurrency(food.price)}</td>
                      <td>
                        <span className={`status-badge ${food.isAvailable ? 'active' : 'inactive'}`}>
                          {food.isAvailable ? 'Đang bán' : 'Ngừng bán'}
                        </span>
                      </td>
                      <td>
                        <div className="action-buttons">
                          <button 
                            className="btn-action btn-edit"
                            onClick={() => handleOpenEdit(food)}
                          >
                            Sửa
                          </button>
                          <button 
                            className={`btn-action ${food.isAvailable ? 'btn-danger' : 'btn-success'}`}
                            onClick={() => handleToggleStatus(food)}
                            disabled={updatingId === food.id}
                          >
                            {updatingId === food.id ? '...' : (food.isAvailable ? 'Tạm ngưng' : 'Mở bán')}
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {/* Pagination */}
            {foodsPage.totalPages > 1 && (
              <div className="pagination">
                <button 
                  disabled={foodsPage.first || loading} 
                  onClick={() => fetchFoods(page - 1)}
                >
                  &laquo; Trước
                </button>
                <span>Trang {page + 1} / {foodsPage.totalPages}</span>
                <button 
                  disabled={foodsPage.last || loading} 
                  onClick={() => fetchFoods(page + 1)}
                >
                  Sau &raquo;
                </button>
              </div>
            )}
          </>
        )}
      </div>

      {/* Modal */}
      {showModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-header">
              <h3>{isEditing ? 'Sửa món ăn' : 'Thêm món ăn mới'}</h3>
              <button className="close-btn" onClick={handleCloseModal}>&times;</button>
            </div>
            <form onSubmit={handleFormSubmit}>
              <div className="modal-body">
                {formError && <div className="form-error">{formError}</div>}
                
                <div className="form-group">
                  <label>Tên món ăn *</label>
                  <input 
                    type="text" 
                    value={formData.name}
                    onChange={(e) => setFormData({...formData, name: e.target.value})}
                    required
                  />
                </div>
                
                <div className="form-group">
                  <label>Danh mục *</label>
                  <select 
                    value={formData.categoryId} 
                    onChange={(e) => setFormData({...formData, categoryId: Number(e.target.value)})}
                    required
                  >
                    <option value={0} disabled>-- Chọn danh mục --</option>
                    {categories.map(c => (
                      <option key={c.id} value={c.id}>{c.name}</option>
                    ))}
                  </select>
                </div>

                <div className="form-group">
                  <label>Giá bán (VNĐ) *</label>
                  <input 
                    type="number" 
                    min="1"
                    value={formData.price || ''}
                    onChange={(e) => setFormData({...formData, price: Number(e.target.value)})}
                    required
                  />
                </div>

                <div className="form-group">
                  <label>Mô tả</label>
                  <textarea 
                    value={formData.description}
                    onChange={(e) => setFormData({...formData, description: e.target.value})}
                    rows={2}
                  />
                </div>
                
                <div className="form-group">
                  <label>URL Hình ảnh</label>
                  <input 
                    type="text" 
                    value={formData.imageUrl}
                    onChange={(e) => setFormData({...formData, imageUrl: e.target.value})}
                  />
                </div>

                <div className="form-group-checkbox">
                  <label>
                    <input 
                      type="checkbox" 
                      checked={formData.isAvailable}
                      onChange={(e) => setFormData({...formData, isAvailable: e.target.checked})}
                    />
                    Cho phép bán ngay (isAvailable)
                  </label>
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn-secondary" onClick={handleCloseModal} disabled={submitting}>
                  Hủy
                </button>
                <button type="submit" className="btn-primary" disabled={submitting}>
                  {submitting ? 'Đang lưu...' : 'Lưu lại'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
