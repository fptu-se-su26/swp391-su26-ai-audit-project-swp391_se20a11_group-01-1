import React, { useState, useEffect } from 'react';
import { adminCouponApi } from '../../api/adminCouponApi';
import { getApiErrorMessage } from '../../utils/getApiErrorMessage';
import { formatCurrency } from '../../utils/formatCurrency';
import { formatDateTime } from '../../utils/formatDateTime';
import type { CouponResponse, CouponRequest, CouponStatus, DiscountType } from '../../types/coupon';
import './AdminCouponsPage.css';

export const AdminCouponsPage: React.FC = () => {
  const [coupons, setCoupons] = useState<CouponResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [showModal, setShowModal] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [currentId, setCurrentId] = useState<number | null>(null);
  
  const [formData, setFormData] = useState<CouponRequest>({
    code: '',
    name: '',
    description: '',
    discountType: 'FIXED_AMOUNT',
    discountValue: 0,
    minOrderValue: 0,
    maxDiscountAmount: 0,
    startDate: '',
    endDate: '',
    usageLimit: 0,
    status: 'ACTIVE'
  });
  
  const [submitting, setSubmitting] = useState(false);
  const [formError, setFormError] = useState<string | null>(null);
  const [updatingId, setUpdatingId] = useState<number | null>(null);

  const fetchCoupons = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await adminCouponApi.getCoupons();
      setCoupons(data);
    } catch (err) {
      setError(getApiErrorMessage(err, 'Lỗi khi tải danh sách mã giảm giá.'));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchCoupons();
  }, []);

  const handleOpenCreate = () => {
    setIsEditing(false);
    setCurrentId(null);
    
    // Set default dates
    const now = new Date();
    const nextMonth = new Date();
    nextMonth.setMonth(nextMonth.getMonth() + 1);
    
    // Strip seconds/ms for datetime-local input
    const toLocalISOString = (d: Date) => {
      const pad = (n: number) => n.toString().padStart(2, '0');
      return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
    };

    setFormData({
      code: '',
      name: '',
      description: '',
      discountType: 'FIXED_AMOUNT',
      discountValue: 0,
      minOrderValue: 0,
      maxDiscountAmount: 0,
      startDate: toLocalISOString(now),
      endDate: toLocalISOString(nextMonth),
      usageLimit: 100,
      status: 'ACTIVE'
    });
    setFormError(null);
    setShowModal(true);
  };

  const handleOpenEdit = (coupon: CouponResponse) => {
    setIsEditing(true);
    setCurrentId(coupon.id);
    
    // Format dates to fit datetime-local (remove seconds and Z if any)
    const formatForInput = (dateStr: string) => {
      if (!dateStr) return '';
      return dateStr.substring(0, 16);
    };

    setFormData({
      code: coupon.code,
      name: coupon.name || '',
      description: coupon.description || '',
      discountType: coupon.discountType,
      discountValue: coupon.discountValue,
      minOrderValue: coupon.minOrderValue,
      maxDiscountAmount: coupon.maxDiscountAmount || 0,
      startDate: formatForInput(coupon.startDate),
      endDate: formatForInput(coupon.endDate),
      usageLimit: coupon.usageLimit,
      status: coupon.status
    });
    setFormError(null);
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
  };

  const handleFormSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (new Date(formData.startDate) > new Date(formData.endDate)) {
      setFormError('Ngày bắt đầu không được lớn hơn ngày kết thúc.');
      return;
    }
    
    if (formData.discountValue < 0 || formData.minOrderValue < 0 || formData.usageLimit < 0) {
      setFormError('Các giá trị không được là số âm.');
      return;
    }

    if (formData.discountType === 'PERCENTAGE' && formData.discountValue > 100) {
      setFormError('Phần trăm giảm giá không được vượt quá 100%.');
      return;
    }

    // Prepare payload, appending :00 to dates to match LocalDateTime without timezone
    const payload: CouponRequest = {
      ...formData,
      code: formData.code.trim().toUpperCase(),
      name: formData.name?.trim(),
      description: formData.description?.trim(),
      startDate: `${formData.startDate}:00`,
      endDate: `${formData.endDate}:00`,
      maxDiscountAmount: formData.maxDiscountAmount ? formData.maxDiscountAmount : undefined
    };

    if (!payload.code) {
      setFormError('Mã Coupon không được để trống.');
      return;
    }

    setSubmitting(true);
    setFormError(null);
    try {
      if (isEditing && currentId) {
        await adminCouponApi.updateCoupon(currentId, payload);
        alert('Cập nhật mã giảm giá thành công!');
      } else {
        await adminCouponApi.createCoupon(payload);
        alert('Thêm mới mã giảm giá thành công!');
      }
      setShowModal(false);
      fetchCoupons();
    } catch (err) {
      setFormError(getApiErrorMessage(err, 'Lỗi khi lưu mã giảm giá.'));
    } finally {
      setSubmitting(false);
    }
  };

  const handleUpdateStatus = async (coupon: CouponResponse, newStatus: CouponStatus) => {
    if (!window.confirm(`Bạn có chắc chắn muốn chuyển trạng thái mã ${coupon.code} thành ${newStatus}?`)) {
      return;
    }

    setUpdatingId(coupon.id);
    try {
      await adminCouponApi.updateCouponStatus(coupon.id, newStatus);
      alert('Cập nhật trạng thái thành công.');
      fetchCoupons();
    } catch (err) {
      alert(getApiErrorMessage(err, 'Lỗi khi cập nhật trạng thái.'));
    } finally {
      setUpdatingId(null);
    }
  };

  return (
    <div className="admin-coupons-page">
      <div className="admin-header flex-between">
        <h2>Quản lý Mã giảm giá (Coupons)</h2>
        <button className="btn-primary" onClick={handleOpenCreate}>+ Thêm Coupon</button>
      </div>

      <div className="coupons-list-section mt-20">
        {loading && !coupons.length ? (
          <div className="admin-page-state">Đang tải dữ liệu...</div>
        ) : error ? (
          <div className="admin-page-state text-error">{error}</div>
        ) : coupons.length === 0 ? (
          <div className="admin-page-state">Không có mã giảm giá nào.</div>
        ) : (
          <div className="table-responsive">
            <table className="admin-table">
              <thead>
                <tr>
                  <th>Mã Coupon</th>
                  <th>Loại giảm</th>
                  <th>Mức giảm</th>
                  <th>Đơn tối thiểu</th>
                  <th>Đã dùng / Giới hạn</th>
                  <th>Hạn sử dụng</th>
                  <th>Trạng thái</th>
                  <th>Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {coupons.map(coupon => (
                  <tr key={coupon.id}>
                    <td><strong>{coupon.code}</strong></td>
                    <td>{coupon.discountType === 'PERCENTAGE' ? 'Phần trăm (%)' : 'Cố định (VNĐ)'}</td>
                    <td className="highlight-price">
                      {coupon.discountType === 'PERCENTAGE' 
                        ? `${coupon.discountValue}%` 
                        : formatCurrency(coupon.discountValue)}
                    </td>
                    <td>{formatCurrency(coupon.minOrderValue)}</td>
                    <td>{coupon.usedCount} / {coupon.usageLimit}</td>
                    <td>
                      <div className="coupon-dates">
                        <small>Từ: {formatDateTime(coupon.startDate)}</small><br/>
                        <small>Đến: {formatDateTime(coupon.endDate)}</small>
                      </div>
                    </td>
                    <td>
                      <span className={`status-badge ${coupon.status.toLowerCase()}`}>
                        {coupon.status}
                      </span>
                    </td>
                    <td>
                      <div className="action-buttons">
                        <button 
                          className="btn-action btn-edit"
                          onClick={() => handleOpenEdit(coupon)}
                        >
                          Sửa
                        </button>
                        {coupon.status !== 'INACTIVE' && (
                          <button 
                            className="btn-action btn-danger"
                            onClick={() => handleUpdateStatus(coupon, 'INACTIVE')}
                            disabled={updatingId === coupon.id}
                          >
                            {updatingId === coupon.id ? '...' : 'Ngừng'}
                          </button>
                        )}
                        {coupon.status === 'INACTIVE' && (
                          <button 
                            className="btn-action btn-success"
                            onClick={() => handleUpdateStatus(coupon, 'ACTIVE')}
                            disabled={updatingId === coupon.id}
                          >
                            {updatingId === coupon.id ? '...' : 'Kích hoạt'}
                          </button>
                        )}
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
          <div className="modal-content modal-lg">
            <div className="modal-header">
              <h3>{isEditing ? 'Sửa mã giảm giá' : 'Thêm mã giảm giá mới'}</h3>
              <button className="close-btn" onClick={handleCloseModal}>&times;</button>
            </div>
            <form onSubmit={handleFormSubmit}>
              <div className="modal-body coupon-form-grid">
                {formError && <div className="form-error full-width">{formError}</div>}
                
                <div className="form-group">
                  <label>Mã (Code) *</label>
                  <input 
                    type="text" 
                    value={formData.code}
                    onChange={(e) => setFormData({...formData, code: e.target.value.toUpperCase()})}
                    placeholder="VD: SUMMER2026"
                    required
                  />
                </div>
                
                <div className="form-group">
                  <label>Tên hiển thị</label>
                  <input 
                    type="text" 
                    value={formData.name}
                    onChange={(e) => setFormData({...formData, name: e.target.value})}
                  />
                </div>

                <div className="form-group">
                  <label>Loại giảm giá *</label>
                  <select 
                    value={formData.discountType} 
                    onChange={(e) => setFormData({...formData, discountType: e.target.value as DiscountType})}
                  >
                    <option value="FIXED_AMOUNT">Số tiền cố định (VNĐ)</option>
                    <option value="PERCENTAGE">Phần trăm (%)</option>
                  </select>
                </div>

                <div className="form-group">
                  <label>Mức giảm * {formData.discountType === 'PERCENTAGE' ? '(%)' : '(VNĐ)'}</label>
                  <input 
                    type="number" 
                    min="0"
                    step={formData.discountType === 'PERCENTAGE' ? '0.1' : '1'}
                    value={formData.discountValue || ''}
                    onChange={(e) => setFormData({...formData, discountValue: Number(e.target.value)})}
                    required
                  />
                </div>

                <div className="form-group">
                  <label>Giá trị đơn tối thiểu (VNĐ) *</label>
                  <input 
                    type="number" 
                    min="0"
                    value={formData.minOrderValue || ''}
                    onChange={(e) => setFormData({...formData, minOrderValue: Number(e.target.value)})}
                    required
                  />
                </div>

                <div className="form-group">
                  <label>Giảm tối đa (VNĐ) (Tùy chọn)</label>
                  <input 
                    type="number" 
                    min="0"
                    value={formData.maxDiscountAmount || ''}
                    onChange={(e) => setFormData({...formData, maxDiscountAmount: Number(e.target.value)})}
                    placeholder="Bỏ trống nếu không giới hạn"
                  />
                </div>

                <div className="form-group">
                  <label>Giới hạn số lần dùng *</label>
                  <input 
                    type="number" 
                    min="1"
                    value={formData.usageLimit || ''}
                    onChange={(e) => setFormData({...formData, usageLimit: Number(e.target.value)})}
                    required
                  />
                </div>

                <div className="form-group">
                  <label>Trạng thái *</label>
                  <select 
                    value={formData.status} 
                    onChange={(e) => setFormData({...formData, status: e.target.value as CouponStatus})}
                  >
                    <option value="ACTIVE">ACTIVE</option>
                    <option value="INACTIVE">INACTIVE</option>
                  </select>
                </div>

                <div className="form-group">
                  <label>Từ ngày *</label>
                  <input 
                    type="datetime-local" 
                    value={formData.startDate}
                    onChange={(e) => setFormData({...formData, startDate: e.target.value})}
                    required
                  />
                </div>

                <div className="form-group">
                  <label>Đến ngày *</label>
                  <input 
                    type="datetime-local" 
                    value={formData.endDate}
                    onChange={(e) => setFormData({...formData, endDate: e.target.value})}
                    required
                  />
                </div>

                <div className="form-group full-width">
                  <label>Mô tả thêm</label>
                  <textarea 
                    value={formData.description}
                    onChange={(e) => setFormData({...formData, description: e.target.value})}
                    rows={2}
                  />
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
