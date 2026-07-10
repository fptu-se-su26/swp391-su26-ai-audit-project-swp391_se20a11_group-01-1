import React, { useEffect, useState } from 'react';
import { getAllReservations, updateReservationStatus } from '../../services/reservationService';
import './StaffReservations.css';

const TABLES = ['Ban 1', 'Ban 2', 'Ban 3', 'Ban 4', 'Ban 5', 'Ban 6', 'Ban 7', 'Ban 8'];

const statusMap = {
  PENDING: { label: 'Cho den', cls: 'res-pending', icon: '⏳' },
  CONFIRMED: { label: 'Da xac nhan', cls: 'res-pending', icon: '✅' },
  ARRIVED: { label: 'Da check-in', cls: 'res-arrived', icon: '✅' },
  CANCELLED: { label: 'Da huy', cls: 'res-cancelled', icon: '✕' },
  NO_SHOW: { label: 'Khong den', cls: 'res-noshow', icon: '!' },
};

function StaffReservations() {
  const [reservations, setReservations] = useState([]);
  const [selected, setSelected] = useState(null);
  const [tableChoice, setTableChoice] = useState('');
  const [filter, setFilter] = useState('all');
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

  const filtered = filter === 'all'
    ? reservations
    : reservations.filter((reservation) => reservation.status === filter);

  const update = async (reservation, status, assignedTable) => {
    try {
      const updated = await updateReservationStatus(reservation.reservationId, status, assignedTable);
      setReservations((prev) => prev.map((item) => (
        item.reservationId === updated.reservationId ? updated : item
      )));
      setSelected(null);
      setTableChoice('');
    } catch (err) {
      console.error('Update reservation error:', err);
      setError('Khong the cap nhat dat ban.');
    }
  };

  const handleCheckin = (reservation) => {
    if (!tableChoice) return;
    update(reservation, 'ARRIVED', tableChoice);
  };

  const preOrderTotal = (items = []) =>
    items.reduce((sum, item) => sum + Number(item.subtotal || 0), 0);

  return (
    <div className="staff-res">
      <div className="page-header">
        <h1 className="page-title">Dat ban truoc</h1>
        <div className="res-summary">
          <span className="res-count pending">⏳ {reservations.filter(r => r.status === 'PENDING').length} cho</span>
          <span className="res-count arrived">✅ {reservations.filter(r => r.status === 'ARRIVED').length} da den</span>
        </div>
      </div>

      {error && <div className="no-res">{error}</div>}
      {loading && <div className="no-res">Dang tai dat ban...</div>}

      <div className="filter-tabs" style={{ marginBottom: 20 }}>
        {[
          ['all', 'Tat ca'],
          ['PENDING', 'Cho den'],
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

      <div className="res-list">
        {filtered.map((reservation) => {
          const status = statusMap[reservation.status] || statusMap.PENDING;

          return (
            <div key={reservation.reservationId} className={`res-card card ${reservation.status === 'ARRIVED' ? 'card-arrived' : ''}`}>
              <div className="res-card-main" onClick={() => setSelected(selected?.reservationId === reservation.reservationId ? null : reservation)}>
                <div className="res-col">
                  <span className="res-id">{reservation.reservationCode}</span>
                  <span className={`res-status-badge ${status.cls}`}>{status.icon} {status.label}</span>
                </div>
                <div className="res-col res-guest-col">
                  <span className="res-name">👤 {reservation.customerName}</span>
                  <span className="res-phone">☎ {reservation.phone}</span>
                </div>
                <div className="res-col">
                  <span className="res-time">📅 {reservation.reservationDate}</span>
                  <span className="res-time">🕐 {String(reservation.reservationTime).slice(0, 5)}</span>
                  <span className="res-guests">👥 {reservation.guests} nguoi</span>
                </div>
                <div className="res-col">
                  {reservation.assignedTable
                    ? <span className="res-table-assigned">🪑 {reservation.assignedTable}</span>
                    : <span className="res-table-none">Chua xep ban</span>
                  }
                  {reservation.preOrderItems?.length > 0 && (
                    <span className="res-preorder-badge">🍽 {reservation.preOrderItems.length} mon pre-order</span>
                  )}
                </div>
                <div className="res-col res-expand">
                  {selected?.reservationId === reservation.reservationId ? '▲' : '▼'}
                </div>
              </div>

              {selected?.reservationId === reservation.reservationId && (
                <div className="res-detail">
                  {reservation.note && (
                    <div className="res-note">📝 <strong>Ghi chu:</strong> {reservation.note}</div>
                  )}

                  {reservation.preOrderItems?.length > 0 && (
                    <div className="res-preorder">
                      <h4>🍽 Mon dat truoc</h4>
                      <div className="preorder-items">
                        {reservation.preOrderItems.map((item) => (
                          <div key={item.preOrderItemId} className="preorder-row">
                            <span>{item.foodName} x {item.quantity}</span>
                            <span>{Number(item.subtotal || 0).toLocaleString('vi-VN')}đ</span>
                          </div>
                        ))}
                        <div className="preorder-total">
                          <span>Tong du kien</span>
                          <strong>{preOrderTotal(reservation.preOrderItems).toLocaleString('vi-VN')}đ</strong>
                        </div>
                      </div>
                    </div>
                  )}

                  {(reservation.status === 'PENDING' || reservation.status === 'CONFIRMED') && (
                    <div className="res-actions">
                      <div className="checkin-row">
                        <select className="table-select" value={tableChoice} onChange={(e) => setTableChoice(e.target.value)}>
                          <option value="">-- Chon ban --</option>
                          {TABLES.map((table) => <option key={table} value={table}>{table}</option>)}
                        </select>
                        <button className="checkin-btn" onClick={() => handleCheckin(reservation)} disabled={!tableChoice}>
                          ✅ Check-in khach
                        </button>
                      </div>
                      <div className="other-actions">
                        <button className="action-sm cancel-sm" onClick={() => update(reservation, 'CANCELLED')}>✕ Huy dat ban</button>
                        <button className="action-sm noshow-sm" onClick={() => update(reservation, 'NO_SHOW')}>! Khong den</button>
                      </div>
                    </div>
                  )}

                  {reservation.status === 'ARRIVED' && (
                    <div className="arrived-info">
                      <span>✅ Da check-in - <strong>{reservation.assignedTable}</strong></span>
                      <button className="action-sm" onClick={() => update(reservation, 'PENDING', '')}>↩ Hoan tac</button>
                    </div>
                  )}
                </div>
              )}
            </div>
          );
        })}

        {!loading && filtered.length === 0 && (
          <div className="no-res">Khong co dat ban nao</div>
        )}
      </div>
    </div>
  );
}

export default StaffReservations;
