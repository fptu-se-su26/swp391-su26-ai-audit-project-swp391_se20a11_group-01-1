import React, { useEffect, useState } from 'react';
import './AdminVouchers.css';
import {
  changeVoucherStatus,
  createVoucher,
  deleteVoucher,
  getAllVouchers,
  updateVoucher,
} from '../../services/voucherService';

const emptyForm = {
  code: '', name: '', description: '', discountType: 'PERCENT', discountValue: '',
  maxDiscountAmount: '', minOrderAmount: '', usageLimit: '', usageLimitPerUser: '1',
  startAt: '', endAt: '',
};

const toDateTimeLocal = (value) => value ? new Date(value).toISOString().slice(0, 16) : '';
const money = (value) => Number(value || 0).toLocaleString('vi-VN');

function AdminVouchers() {
  const [vouchers, setVouchers] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  const fetchVouchers = async () => {
    setLoading(true);
    setError('');
    try {
      setVouchers(await getAllVouchers());
    } catch (err) {
      setError(err.response?.data?.message || 'Không thể tải danh sách voucher');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchVouchers(); }, []);

  const updateForm = (field, value) => setForm(prev => ({ ...prev, [field]: value }));

  const openCreate = () => {
    setEditingId(null);
    setForm(emptyForm);
    setShowForm(true);
  };

  const openEdit = (voucher) => {
    setEditingId(voucher.id);
    setForm({
      code: voucher.code,
      name: voucher.name || '',
      description: voucher.description || '',
      discountType: voucher.discountType,
      discountValue: String(voucher.discountValue ?? ''),
      maxDiscountAmount: voucher.maxDiscountAmount == null ? '' : String(voucher.maxDiscountAmount),
      minOrderAmount: String(voucher.minOrderAmount ?? ''),
      usageLimit: String(voucher.usageLimit ?? ''),
      usageLimitPerUser: String(voucher.usageLimitPerUser ?? 1),
      startAt: toDateTimeLocal(voucher.startAt),
      endAt: toDateTimeLocal(voucher.endAt),
    });
    setShowForm(true);
  };

  const buildPayload = () => ({
    ...(editingId ? {} : { code: form.code.trim().toUpperCase() }),
    name: form.name.trim(),
    description: form.description.trim(),
    discountType: form.discountType,
    discountValue: Number(form.discountValue),
    maxDiscountAmount: form.maxDiscountAmount ? Number(form.maxDiscountAmount) : null,
    minOrderAmount: Number(form.minOrderAmount),
    usageLimit: Number(form.usageLimit),
    usageLimitPerUser: Number(form.usageLimitPerUser),
    startAt: `${form.startAt}:00`,
    endAt: `${form.endAt}:00`,
  });

  const handleSubmit = async (event) => {
    event.preventDefault();
    setSaving(true);
    try {
      const payload = buildPayload();
      if (editingId) {
        const updated = await updateVoucher(editingId, payload);
        setVouchers(prev => prev.map(item => item.id === editingId ? updated : item));
      } else {
        const created = await createVoucher(payload);
        setVouchers(prev => [...prev, created]);
      }
      setShowForm(false);
      setEditingId(null);
      setForm(emptyForm);
    } catch (err) {
      alert(err.response?.data?.message || err.response?.data || 'Không thể lưu voucher');
    } finally {
      setSaving(false);
    }
  };

  const toggleActive = async (voucher) => {
    try {
      const updated = await changeVoucherStatus(voucher.id, !voucher.active);
      setVouchers(prev => prev.map(item => item.id === voucher.id ? updated : item));
    } catch (err) {
      alert(err.response?.data?.message || 'Không thể cập nhật trạng thái voucher');
    }
  };

  const handleDelete = async (voucher) => {
    if (!window.confirm(`Xóa hoặc vô hiệu hóa voucher ${voucher.code}?`)) return;
    try {
      await deleteVoucher(voucher.id);
      await fetchVouchers();
    } catch (err) {
      alert(err.response?.data?.message || 'Không thể xóa voucher');
    }
  };

  return (
    <div className="admin-vouchers">
      <div className="page-header">
        <h1 className="page-title">Quản lý mã giảm giá</h1>
        <button className="btn-primary" onClick={openCreate}>+ Tạo voucher</button>
      </div>

      {loading && <p>Đang tải danh sách voucher...</p>}
      {error && <div className="voucher-msg error">{error} <button onClick={fetchVouchers}>Thử lại</button></div>}
      {!loading && !error && vouchers.length === 0 && <p>Chưa có voucher nào.</p>}

      <div className="vouchers-grid">
        {vouchers.map(voucher => (
          <div key={voucher.id} className={`voucher-admin-card card ${!voucher.active ? 'inactive' : ''}`}>
            <div className="vac-header">
              <div><span className="vac-code">{voucher.code}</span><div>{voucher.name}</div></div>
              <label className="toggle-switch">
                <input type="checkbox" checked={Boolean(voucher.active)} onChange={() => toggleActive(voucher)} />
                <span className="toggle-slider" />
              </label>
            </div>
            <div className="vac-discount">
              {voucher.discountType === 'PERCENT' ? `-${voucher.discountValue}%` : `-${money(voucher.discountValue)}đ`}
            </div>
            <div className="vac-details">
              <span>Đơn tối thiểu: {money(voucher.minOrderAmount)}đ</span>
              <span>Đã dùng: {voucher.usedCount}/{voucher.usageLimit}</span>
              <span>HSD: {new Date(voucher.endAt).toLocaleString('vi-VN')}</span>
            </div>
            <div className="vac-progress">
              <div className="vac-bar" style={{ width: `${Math.min(100, (voucher.usedCount / voucher.usageLimit) * 100)}%` }} />
            </div>
            <div className="modal-btns">
              <button className="btn-cancel" onClick={() => openEdit(voucher)}>Sửa</button>
              <button className="vac-del" onClick={() => handleDelete(voucher)}>Xóa</button>
            </div>
          </div>
        ))}
      </div>

      {showForm && (
        <div className="modal-overlay" onClick={() => setShowForm(false)}>
          <div className="add-modal card" onClick={event => event.stopPropagation()}>
            <h2>{editingId ? 'Cập nhật voucher' : 'Tạo voucher mới'}</h2>
            <form onSubmit={handleSubmit}>
              <div className="form-row-2">
                <Field label="Mã voucher"><input className="form-input" value={form.code} disabled={Boolean(editingId)} onChange={e => updateForm('code', e.target.value.toUpperCase())} required /></Field>
                <Field label="Tên voucher"><input className="form-input" value={form.name} onChange={e => updateForm('name', e.target.value)} required /></Field>
              </div>
              <Field label="Mô tả"><input className="form-input" value={form.description} onChange={e => updateForm('description', e.target.value)} /></Field>
              <div className="form-row-2">
                <Field label="Loại giảm"><select className="form-input" value={form.discountType} onChange={e => updateForm('discountType', e.target.value)}><option value="PERCENT">Phần trăm (%)</option><option value="FIXED">Số tiền cố định</option></select></Field>
                <Field label="Giá trị giảm"><input className="form-input" type="number" min="0.01" step="0.01" value={form.discountValue} onChange={e => updateForm('discountValue', e.target.value)} required /></Field>
              </div>
              <div className="form-row-2">
                <Field label="Đơn tối thiểu"><input className="form-input" type="number" min="0" value={form.minOrderAmount} onChange={e => updateForm('minOrderAmount', e.target.value)} required /></Field>
                <Field label="Giảm tối đa"><input className="form-input" type="number" min="0.01" disabled={form.discountType === 'FIXED'} value={form.maxDiscountAmount} onChange={e => updateForm('maxDiscountAmount', e.target.value)} /></Field>
              </div>
              <div className="form-row-2">
                <Field label="Tổng lượt dùng"><input className="form-input" type="number" min="1" value={form.usageLimit} onChange={e => updateForm('usageLimit', e.target.value)} required /></Field>
                <Field label="Giới hạn mỗi khách"><input className="form-input" type="number" min="1" value={form.usageLimitPerUser} onChange={e => updateForm('usageLimitPerUser', e.target.value)} required /></Field>
              </div>
              <div className="form-row-2">
                <Field label="Bắt đầu"><input className="form-input" type="datetime-local" value={form.startAt} onChange={e => updateForm('startAt', e.target.value)} required /></Field>
                <Field label="Kết thúc"><input className="form-input" type="datetime-local" value={form.endAt} onChange={e => updateForm('endAt', e.target.value)} required /></Field>
              </div>
              <div className="modal-btns">
                <button type="submit" className="btn-primary" disabled={saving}>{saving ? 'Đang lưu...' : editingId ? 'Lưu thay đổi' : 'Tạo voucher'}</button>
                <button type="button" className="btn-cancel" onClick={() => setShowForm(false)}>Hủy</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

function Field({ label, children }) {
  return <div className="form-group"><label className="form-label">{label}</label>{children}</div>;
}

export default AdminVouchers;
