import React, { useState, useEffect } from 'react';
import { adminCategoryApi } from '../../api/adminCategoryApi';
import { getApiErrorMessage } from '../../utils/getApiErrorMessage';
import type { CategoryResponse, CategoryRequest } from '../../types/category';
import './AdminCategoriesPage.css';

export const AdminCategoriesPage: React.FC = () => {
  const [categories, setCategories] = useState<CategoryResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [showModal, setShowModal] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [currentId, setCurrentId] = useState<number | null>(null);
  
  const [formData, setFormData] = useState<CategoryRequest>({
    name: '',
    description: '',
    imageUrl: ''
  });
  const [submitting, setSubmitting] = useState(false);
  const [formError, setFormError] = useState<string | null>(null);
  const [updatingId, setUpdatingId] = useState<number | null>(null);

  const fetchCategories = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await adminCategoryApi.getCategories();
      setCategories(data);
    } catch (err) {
      setError(getApiErrorMessage(err, 'Lỗi khi tải danh sách danh mục.'));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchCategories();
  }, []);

  const handleOpenCreate = () => {
    setIsEditing(false);
    setCurrentId(null);
    setFormData({ name: '', description: '', imageUrl: '' });
    setFormError(null);
    setShowModal(true);
  };

  const handleOpenEdit = (category: CategoryResponse) => {
    setIsEditing(true);
    setCurrentId(category.id);
    setFormData({
      name: category.name,
      description: category.description || '',
      imageUrl: category.imageUrl || ''
    });
    setFormError(null);
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
  };

  const handleFormSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const payload = {
      name: formData.name.trim(),
      description: formData.description?.trim(),
      imageUrl: formData.imageUrl?.trim()
    };

    if (!payload.name) {
      setFormError('Tên danh mục không được để trống.');
      return;
    }

    setSubmitting(true);
    setFormError(null);
    try {
      if (isEditing && currentId) {
        await adminCategoryApi.updateCategoryResponse(currentId, payload);
        alert('Cập nhật danh mục thành công!');
      } else {
        await adminCategoryApi.createCategoryResponse(payload);
        alert('Thêm mới danh mục thành công!');
      }
      setShowModal(false);
      fetchCategories();
    } catch (err) {
      setFormError(getApiErrorMessage(err, 'Lỗi khi lưu danh mục.'));
    } finally {
      setSubmitting(false);
    }
  };

  const handleToggleStatus = async (category: CategoryResponse) => {
    // Backend doesn't return isActive in CategoryResponseResponse, it only returns active categories.
    // If a category is in this list, it means it's ACTIVE. If we deactivate, it'll be deleted.
    if (!window.confirm(`Bạn có chắc chắn muốn XÓA / VÔ HIỆU HÓA danh mục ${category.name}? Danh mục này sẽ bị ẩn.`)) {
      return;
    }

    setUpdatingId(category.id);
    try {
      await adminCategoryApi.updateCategoryResponseStatus(category.id, false);
      alert('Vô hiệu hóa danh mục thành công.');
      fetchCategories();
    } catch (err) {
      alert(getApiErrorMessage(err, 'Lỗi khi thay đổi trạng thái danh mục.'));
    } finally {
      setUpdatingId(null);
    }
  };

  return (
    <div className="admin-categories-page">
      <div className="admin-header flex-between">
        <h2>Quản lý Danh mục</h2>
        <button className="btn-primary" onClick={handleOpenCreate}>+ Thêm danh mục</button>
      </div>

      <div className="limitation-notice">
        <p>ℹ️ Lưu ý: Backend hiện chỉ hỗ trợ lấy danh sách danh mục đang hoạt động (ACTIVE). Các danh mục đã vô hiệu hóa (DELETED) sẽ không xuất hiện trong danh sách này.</p>
      </div>

      <div className="categories-list-section mt-20">
        {loading && !categories.length ? (
          <div className="admin-page-state">Đang tải dữ liệu...</div>
        ) : error ? (
          <div className="admin-page-state text-error">{error}</div>
        ) : categories.length === 0 ? (
          <div className="admin-page-state">Chưa có danh mục nào.</div>
        ) : (
          <div className="table-responsive">
            <table className="admin-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Ảnh</th>
                  <th>Tên danh mục</th>
                  <th>Mô tả</th>
                  <th>Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {categories.map(cat => (
                  <tr key={cat.id}>
                    <td>{cat.id}</td>
                    <td>
                      {cat.imageUrl ? (
                        <img src={cat.imageUrl} alt={cat.name} className="category-img-preview" />
                      ) : (
                        <span className="no-image">No img</span>
                      )}
                    </td>
                    <td><strong>{cat.name}</strong></td>
                    <td>{cat.description || '-'}</td>
                    <td>
                      <div className="action-buttons">
                        <button 
                          className="btn-action btn-edit"
                          onClick={() => handleOpenEdit(cat)}
                        >
                          Sửa
                        </button>
                        <button 
                          className="btn-action btn-danger"
                          onClick={() => handleToggleStatus(cat)}
                          disabled={updatingId === cat.id}
                        >
                          {updatingId === cat.id ? '...' : 'Vô hiệu hóa'}
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Modal */}
      {showModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-header">
              <h3>{isEditing ? 'Sửa danh mục' : 'Thêm danh mục mới'}</h3>
              <button className="close-btn" onClick={handleCloseModal}>&times;</button>
            </div>
            <form onSubmit={handleFormSubmit}>
              <div className="modal-body">
                {formError && <div className="form-error">{formError}</div>}
                <div className="form-group">
                  <label>Tên danh mục *</label>
                  <input 
                    type="text" 
                    value={formData.name}
                    onChange={(e) => setFormData({...formData, name: e.target.value})}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Mô tả</label>
                  <textarea 
                    value={formData.description}
                    onChange={(e) => setFormData({...formData, description: e.target.value})}
                    rows={3}
                  />
                </div>
                <div className="form-group">
                  <label>URL Hình ảnh</label>
                  <input 
                    type="text" 
                    value={formData.imageUrl}
                    onChange={(e) => setFormData({...formData, imageUrl: e.target.value})}
                  />
                  {formData.imageUrl && (
                    <div className="img-preview-container">
                      <img src={formData.imageUrl} alt="preview" onError={(e) => (e.currentTarget.style.display = 'none')} />
                    </div>
                  )}
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
