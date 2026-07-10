import React, { useEffect, useState } from 'react';
import { getAllReservations, updateReservationStatus } from '../../services/reservationService';
import './AdminReservations.css';

const statusMap = {
  PENDING: { label: 'Cho xac nhan', cls: 'res-pending', icon: '⏳' },
  CONFIRMED: { label: 'Da xac nhan', cls: 'res-confirmed', icon: '✅' },
  ARRIVED: { label: 'Da check-in', cls: 'res-arrived', icon: '🪑' },
  CANCELLED: { label: 'Da huy', cls: 'res-cancelled', icon: '✕' },
  NO_SHOW: { label: 'Khong den', cls: 'res-cancelled', icon: '!' },
};

const TABLES = ['Ban 1', 'Ban 2', 'Ban 3', 'Ban 4', 'Ban 5', 'Ban 6', 'Ban 7', 'Ban 8'];

function AdminReservations() {
  const [reservations, setReservations] = useState([]);
  const [filter, setFilter] = useState('all');
  const [search, setSearch] = useState('');
  const [expanded, setExpanded] = useState(null);
  const [tableChoice, setTableChoice] = useState({});
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const fetchReservations = async () => {
    setLoading(true);
    setError('');

    try {
      setReservations(await getAllReservations());
    } catch (err) {
      console.error('Fetch reservations error:', err);
      setError('Khong the tai danh sach dat ban.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchReservations();
  }, []);

  const update = async (reservation, status, assignedTable) => {
    try {
      const updated = await updateReservationStatus(reservation.reservationId, status, assignedTable);
      setReservations((prev) => prev.map((item) => (
        item.reservationId === updated.reservationId ? updated : item
      )));
    } catch (err) {
      console.error('Update reservation error:', err);
      setError('Khong the cap nhat dat ban.');
    }
  };

  const filtered = reservations.filter((reservation) => {
    const normalizedSearch = search.toLowerCase();
    const matchFilter = filter === 'all' || reservation.status === filter;
    const matchSearch =
      reservation.customerName?.toLowerCase().includes(normalizedSearch) ||
      reservation.phone?.includes(search) ||
      reservation.reservationCode?.toLowerCase().includes(normalizedSearch);

    return matchFilter && matchSearch;
  });

  const stats = {
    total: reservations.length,
    PENDING: reservations.filter((reservation) => reservation.status === 'PENDING').length,
    CONFIRMED: reservations.filter((reservation) => reservation.status === 'CONFIRMED').length,
    ARRIVED: reservations.filter((reservation) => reservation.status === 'ARRIVED').length,
    CANCELLED: reservations.filter((reservation) => reservation.status === 'CANCELLED').length,
  };

  const preOrderTotal = (items = []) =>
    items.reduce((sum, item) => sum + Number(item.subtotal || 0), 0);

  return (
    <div className="admin-reservations">
      <div className="page-header">
        <h1 className="page-title">Quan ly dat ban</h1>
      </div>

      {error && <div className="card" style={{ padding: 16, color: '#e53e3e', marginBottom: 16 }}>{error}</div>}
      {loading && <div className="card" style={{ padding: 16, marginBottom: 16 }}>Dang tai dat ban...</div>}

      <div className="res-admin-stats">
        {[
          ['total', 'Tong', '#e85d04'],
          ['PENDING', 'Cho xac nhan', '#d69e2e'],
          ['CONFIRMED', 'Da xac nhan', '#3182ce'],
          ['ARRIVED', 'Da check-in', '#38a169'],
          ['CANCELLED', 'Da huy', '#e53e3e']
        ].map(([key, label, color]) => (
          <div key={key} className="res-stat-card" style={{ borderTopColor: color }} onClick={() => setFilter(key === 'total' ? 'all' : key)}>
            <span className="res-stat-num" style={{ color }}>{stats[key]}</span>
            <span className="res-stat-label">{label}</span>
          </div>
        ))}
      </div>

      <div className="res-toolbar">
        <input
          className="search-input"
          placeholder="Tim theo ten, SDT, ma dat ban..."
          value={search}
          onChange={(event) => setSearch(event.target.value)}
        />
        <div className="filter-tabs">
          {[
            ['all', 'Tat ca'],
            ['PENDING', 'Cho xac nhan'],
            ['CONFIRMED', 'Da xac nhan'],
            ['ARRIVED', 'Da check-in'],
            ['CANCELLED', 'Da huy'],
            ['NO_SHOW', 'Khong den']
          ].map(([value, label]) => (
            <button key={value} className={`filter-tab ${filter === value ? 'active' : ''}`} onClick={() => setFilter(value)}>
              {label}
            </button>
          ))}
        </div>
      </div>

      <div className="res-admin-list">
        {!loading && filtered.length === 0 && (
          <div className="card" style={{ textAlign: 'center', padding: 40, color: '#a0aec0' }}>Khong co dat ban nao</div>
        )}

        {filtered.map((reservation) => {
          const status = statusMap[reservation.status] || statusMap.PENDING;

          return (
            <div key={reservation.reservationId} className="res-admin-card card">
              <div className="res-admin-row" onClick={() => setExpanded(expanded === reservation.reservationId ? null : reservation.reservationId)}>
                <div className="res-col-id">
                  <span className="res-admin-id">{reservation.reservationCode}</span>
                  <span className={`res-status-badge ${status.cls}`}>{status.icon} {status.label}</span>
                </div>
                <div className="res-col-guest">
                  <strong>{reservation.customerName}</strong>
                  <span>{reservation.phone}</span>
                </div>
                <div className="res-col-time">
                  <span>📅 {reservation.reservationDate}</span>
                  <span>🕐 {String(reservation.reservationTime).slice(0, 5)}</span>
                </div>
                <div className="res-col-info">
                  <span>👥 {reservation.guests} nguoi</span>
                  {reservation.assignedTable && <span>🪑 {reservation.assignedTable}</span>}
                  {reservation.preOrderItems?.length > 0 && <span className="preorder-tag">🍽 {reservation.preOrderItems.length} mon</span>}
                </div>
                <span className="expand-arrow">{expanded === reservation.reservationId ? '▲' : '▼'}</span>
              </div>

              {expanded === reservation.reservationId && (
                <div className="res-admin-detail">
                  {reservation.note && <div className="res-note">📝 {reservation.note}</div>}

                  {reservation.preOrderItems?.length > 0 && (
                    <div className="res-preorder-section">
                      <h4>🍽 Mon dat truoc</h4>
                      {reservation.preOrderItems.map((item) => (
                        <div key={item.preOrderItemId} className="preorder-item-row">
                          <span>{item.foodName} x {item.quantity}</span>
                          <span>{Number(item.subtotal || 0).toLocaleString('vi-VN')}đ</span>
                        </div>
                      ))}
                      <div className="preorder-total-row">
                        <span>Tong du kien</span>
                        <strong>{preOrderTotal(reservation.preOrderItems).toLocaleString('vi-VN')}đ</strong>
                      </div>
                    </div>
                  )}

                  <div className="res-admin-actions">
                    {reservation.status === 'PENDING' && (
                      <>
                        <div className="confirm-row">
                          <select
                            className="table-select"
                            value={tableChoice[reservation.reservationId] || ''}
                            onChange={(event) => setTableChoice((prev) => ({ ...prev, [reservation.reservationId]: event.target.value }))}
                          >
                            <option value="">-- Chon ban --</option>
                            {TABLES.map((table) => <option key={table} value={table}>{table}</option>)}
                          </select>
                          <button
                            className="action-btn confirm-btn"
                            disabled={!tableChoice[reservation.reservationId]}
                            onClick={() => update(reservation, 'CONFIRMED', tableChoice[reservation.reservationId])}
                          >
                            ✅ Xac nhan
                          </button>
                        </div>
                        <button className="action-btn cancel-btn" onClick={() => update(reservation, 'CANCELLED')}>✕ Huy dat ban</button>
                      </>
                    )}

                    {reservation.status === 'CONFIRMED' && (
                      <div className="confirm-row">
                        <button className="action-btn checkin-btn" onClick={() => update(reservation, 'ARRIVED', reservation.assignedTable)}>🪑 Check-in khach</button>
                        <button className="action-btn cancel-btn" onClick={() => update(reservation, 'CANCELLED')}>✕ Huy</button>
                      </div>
                    )}

                    {(reservation.status === 'ARRIVED' || reservation.status === 'CANCELLED' || reservation.status === 'NO_SHOW') && (
                      <button className="action-btn undo-btn" onClick={() => update(reservation, 'PENDING', '')}>↩ Hoan tac</button>
                    )}
                  </div>
                </div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
}

export default AdminReservations;
