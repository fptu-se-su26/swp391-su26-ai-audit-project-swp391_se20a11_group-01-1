import React, { useState, useEffect } from 'react';
import './AdminVouchers.css';
import { getAllVouchers, createVoucher, updateVoucher, deleteVoucher } from '../../services/voucherService';

function AdminVouchers() {
  const [vouchers, setVouchers] = useState([]);
  const [showAdd, setShowAdd] = useState(false);
  const [form, setForm] = useState({ code:'', discount:'', type:'PERCENT', minOrder:'', total:'', expiry:'' });

  useEffect(() => {
    fetchVouchers();
  }, []);

  const fetchVouchers = async () => {
    try {
      const data = await getAllVouchers();
      setVouchers(data);
    } catch (error) {
      console.error('Error fetching vouchers:', error);
    }
  };

  const toggleActive = async (id) => {
    const voucher = vouchers.find(v => v.id === id);
    if (!voucher) return;
    try {
      const updated = await updateVoucher(id, { ...voucher, active: !voucher.active });
      setVouchers(prev => prev.map(v => v.id === id ? updated : v));
    } catch (error) {
      console.error('Error toggling voucher:', error);
      alert('Lỗi cập nhật mã giảm giá');
    }
  };

  const handleDeleteVoucher = async (id) => {
    if (!window.confirm("Bạn có chắc chắn muốn xóa mã giảm giá này?")) return;
    try {
      await deleteVoucher(id);
      setVouchers(prev => prev.filter(v => v.id !== id));
    } catch (error) {
      console.error('Error deleting voucher:', error);
      alert('Lỗi xóa mã giảm giá');
    }
  };

  const handleAdd = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        code: form.code,
        discount: Number(form.discount),
        type: form.type,
        minOrder: Number(form.minOrder),
        total: Number(form.total),
        expiry: form.expiry,
        active: true
      };
      const created = await createVoucher(payload);
      setVouchers(prev => [...prev, created]);
      setShowAdd(false);
      setForm({ code:'', discount:'', type:'PERCENT', minOrder:'', total:'', expiry:'' });
    } catch (error) {
      console.error('Error creating voucher:', error);
      alert(error.response?.data?.message || 'Lỗi tạo mã giảm giá');
    }
  };

  return (
    <div className="admin-vouchers">
      <div className="page-header">
        <h1 className="page-title">Quản lý mã giảm giá</h1>
        <button className="btn-primary" onClick={() => setShowAdd(true)}>+ Tạo voucher</button>
      </div>

      <div className="vouchers-grid">
        {vouchers.map(v => (
          <div key={v.id} className={`voucher-admin-card card ${!v.active ? 'inactive' : ''}`}>
            <div className="vac-header">
              <span className="vac-code">{v.code}</span>
              <label className="toggle-switch">
                <input type="checkbox" checked={v.active} onChange={() => toggleActive(v.id)} />
                <span className="toggle-slider"></span>
              </label>
            </div>
            <div className="vac-discount">
              {v.type === 'PERCENT' ? `-${v.discount}%` : `-${v.discount.toLocaleString('vi-VN')}đ`}
            </div>
            <div className="vac-details">
              <span>Đơn tối thiểu: {v.minOrder.toLocaleString('vi-VN')}đ</span>
              <span>Đã dùng: {v.used}/{v.total}</span>
              <span>HSD: {v.expiry}</span>
            </div>
            <div className="vac-progress">
              <div className="vac-bar" style={{width: `${(v.used/v.total)*100}%`}}></div>
            </div>
            <button className="vac-del" onClick={() => handleDeleteVoucher(v.id)}>🗑️ Xóa</button>
          </div>
        ))}
      </div>

      {showAdd && (
        <div className="modal-overlay" onClick={() => setShowAdd(false)}>
          <div className="add-modal card" onClick={e => e.stopPropagation()}>
            <h2>Tạo voucher mới</h2>
            <form onSubmit={handleAdd}>
              <div className="form-row-2">
                <div className="form-group">
                  <label className="form-label">Mã voucher</label>
                  <input className="form-input" placeholder="VD: SUMMER30" value={form.code}
                    onChange={e => setForm({...form, code: e.target.value.toUpperCase()})} required />
                </div>
                <div className="form-group">
                  <label className="form-label">Loại giảm</label>
                  <select className="form-input" value={form.type} onChange={e => setForm({...form, type: e.target.value})}>
                    <option value="PERCENT">Phần trăm (%)</option>
                    <option value="FIXED">Số tiền cố định (đ)</option>
                  </select>
                </div>
              </div>
              <div className="form-row-2">
                <div className="form-group">
                  <label className="form-label">Giá trị giảm</label>
                  <input className="form-input" type="number" placeholder={form.type === 'percent' ? '10' : '50000'}
                    value={form.discount} onChange={e => setForm({...form, discount: e.target.value})} required />
                </div>
                <div className="form-group">
                  <label className="form-label">Đơn tối thiểu (đ)</label>
                  <input className="form-input" type="number" placeholder="200000"
                    value={form.minOrder} onChange={e => setForm({...form, minOrder: e.target.value})} required />
                </div>
              </div>
              <div className="form-row-2">
                <div className="form-group">
                  <label className="form-label">Số lượng</label>
                  <input className="form-input" type="number" placeholder="100"
                    value={form.total} onChange={e => setForm({...form, total: e.target.value})} required />
                </div>
                <div className="form-group">
                  <label className="form-label">Hạn sử dụng</label>
                  <input className="form-input" type="date" value={form.expiry}
                    onChange={e => setForm({...form, expiry: e.target.value})} required />
                </div>
              </div>
              <div className="modal-btns">
                <button type="submit" className="btn-primary">Tạo voucher</button>
                <button type="button" className="btn-cancel" onClick={() => setShowAdd(false)}>Hủy</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default AdminVouchers;
