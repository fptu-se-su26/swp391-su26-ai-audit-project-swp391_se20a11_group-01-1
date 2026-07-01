import React, { useState } from 'react';
import './StaffCustomers.css';

const mockCustomers = [
  { id: 1, name: 'Nguyễn Văn An',  phone: '0901-234-567', email: 'an@email.com',   visits: 8,  totalSpend: 4200000, lastVisit: '22/05/2026', tier: 'Kim Cương', note: 'Thích bàn cạnh cửa sổ' },
  { id: 2, name: 'Trần Thị Bích',  phone: '0912-345-678', email: 'bich@email.com',  visits: 5,  totalSpend: 2800000, lastVisit: '20/05/2026', tier: 'Bạch Kim',  note: 'Dị ứng hải sản' },
  { id: 3, name: 'Lê Minh Khoa',   phone: '0933-456-789', email: '',                visits: 12, totalSpend: 7500000, lastVisit: '22/05/2026', tier: 'Kim Cương', note: '' },
  { id: 4, name: 'Phạm Thu Hà',    phone: '0944-567-890', email: 'ha@email.com',    visits: 3,  totalSpend: 1200000, lastVisit: '18/05/2026', tier: 'Vàng',      note: 'Sinh nhật 15/06' },
  { id: 5, name: 'Hoàng Văn Nam',  phone: '0955-678-901', email: '',                visits: 1,  totalSpend: 450000,  lastVisit: '10/05/2026', tier: 'Bạc',       note: '' },
];

const TIER_COLOR = {
  'Kim Cương': '#3182ce',
  'Bạch Kim':  '#805ad5',
  'Vàng':      '#d69e2e',
  'Bạc':       '#a0aec0',
  'Mới':       '#718096',
};

function StaffCustomers() {
  const [customers, setCustomers] = useState(mockCustomers);
  const [search, setSearch]       = useState('');
  const [editingNote, setEditingNote] = useState(null); // id đang sửa ghi chú
  const [noteInput, setNoteInput]     = useState('');
  const [saved, setSaved]             = useState(null);  // id vừa lưu để hiện toast

  const filtered = customers.filter(c =>
    c.name.toLowerCase().includes(search.toLowerCase()) ||
    c.phone.includes(search)
  );

  const openNote = (c) => {
    setEditingNote(c.id);
    setNoteInput(c.note);
  };

  const saveNote = (id) => {
    setCustomers(prev => prev.map(c => c.id === id ? { ...c, note: noteInput } : c));
    setEditingNote(null);
    setSaved(id);
    setTimeout(() => setSaved(null), 2000);
  };

  return (
    <div className="staff-customers">
      <div className="page-header">
        <h1 className="page-title">Tra cứu khách hàng</h1>
      </div>

      <input
        className="search-input"
        placeholder="🔍 Tìm theo tên hoặc số điện thoại..."
        value={search}
        onChange={e => setSearch(e.target.value)}
        style={{ marginBottom: 16 }}
      />

      {filtered.length === 0 && (
        <div className="card" style={{ padding: 32, textAlign: 'center', color: '#a0aec0' }}>
          Không tìm thấy khách hàng nào
        </div>
      )}

      <div className="customers-list">
        {filtered.map(c => (
          <div key={c.id} className="customer-card card">
            <div className="customer-info">
              {/* Avatar */}
              <div className="cust-avatar-lg">{c.name.charAt(0)}</div>

              {/* Details */}
              <div className="cust-details">
                <div className="cust-name-row">
                  <h3>{c.name}</h3>
                  <span
                    className="tier-badge"
                    style={{ background: TIER_COLOR[c.tier] || TIER_COLOR['Mới'] }}
                  >
                    ⭐ {c.tier}
                  </span>
                </div>

                <div className="cust-meta">
                  <span>📞 {c.phone}</span>
                  {c.email && <span>✉️ {c.email}</span>}
                  <span>🗓 Lần cuối: {c.lastVisit}</span>
                </div>

                <div className="cust-stats">
                  <span className="cust-stat">🔁 {c.visits} lần ghé</span>
                  <span className="cust-stat">💰 {c.totalSpend.toLocaleString('vi-VN')}đ</span>
                </div>

                {/* Ghi chú phục vụ */}
                {editingNote === c.id ? (
                  <div className="note-edit-row">
                    <input
                      className="form-input note-input"
                      placeholder="Dị ứng, sở thích chỗ ngồi, yêu cầu đặc biệt..."
                      value={noteInput}
                      onChange={e => setNoteInput(e.target.value)}
                      autoFocus
                    />
                    <button className="save-btn-sm" onClick={() => saveNote(c.id)}>💾 Lưu</button>
                    <button className="cancel-btn-sm" onClick={() => setEditingNote(null)}>Hủy</button>
                  </div>
                ) : (
                  <div className="note-row">
                    {c.note
                      ? <p className="cust-note">📝 {c.note}</p>
                      : <p className="cust-note-empty">Chưa có ghi chú phục vụ</p>
                    }
                    <button
                      className="edit-note-btn"
                      onClick={() => openNote(c)}
                      title="Sửa ghi chú phục vụ"
                    >
                      ✏️ {c.note ? 'Sửa ghi chú' : 'Thêm ghi chú'}
                    </button>
                  </div>
                )}

                {saved === c.id && (
                  <span className="note-saved">✅ Đã lưu ghi chú</span>
                )}
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default StaffCustomers;
