import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { reservationApi } from '../../api/reservationApi';
import { getApiErrorMessage } from '../../utils/getApiErrorMessage';
import { formatDateTime } from '../../utils/formatDateTime';
import { getReservationStatusLabel, getReservationStatusColor } from '../../utils/reservationStatus';
import type { ReservationResponse } from '../../types/reservation';
import './ReservationListPage.css';

export const ReservationListPage: React.FC = () => {
  const [reservations, setReservations] = useState<ReservationResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let mounted = true;
    const fetchReservations = async () => {
      setLoading(true);
      setError(null);
      try {
        const data = await reservationApi.getMyReservations();
        const sorted = [...data].sort((a, b) => new Date(b.reservationTime).getTime() - new Date(a.reservationTime).getTime());
        if (mounted) setReservations(sorted);
      } catch (err) {
        if (mounted) setError(getApiErrorMessage(err, 'Lỗi khi tải danh sách đặt bàn.'));
      } finally {
        if (mounted) setLoading(false);
      }
    };
    fetchReservations();
    return () => { mounted = false; };
  }, []);

  const handleRetry = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await reservationApi.getMyReservations();
      const sorted = [...data].sort((a, b) => new Date(b.reservationTime).getTime() - new Date(a.reservationTime).getTime());
      setReservations(sorted);
    } catch (err) {
      setError(getApiErrorMessage(err, 'Lỗi khi tải danh sách đặt bàn.'));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="reservation-list-page">
      <div className="page-header">
        <h2>Lịch sử đặt bàn</h2>
        <Link to="/customer/reservations/new" className="btn-primary">Đặt bàn mới</Link>
      </div>

      {loading ? (
        <div className="reservation-state">Đang tải danh sách đặt bàn...</div>
      ) : error ? (
        <div className="reservation-state text-error">
          <p>{error}</p>
          <button className="btn-secondary" onClick={handleRetry}>Thử lại</button>
        </div>
      ) : reservations.length === 0 ? (
        <div className="reservation-state">
          <p>Bạn chưa có lịch đặt bàn nào.</p>
          <Link to="/customer/reservations/new" className="btn-primary" style={{marginTop: '10px'}}>Đặt bàn ngay</Link>
        </div>
      ) : (
        <div className="reservation-list">
          {reservations.map(res => (
            <div key={res.id} className="reservation-card">
              <div className="res-header">
                <h3>Mã đặt bàn: #{res.id}</h3>
                <span 
                  className="status-badge"
                  style={{ backgroundColor: getReservationStatusColor(res.status) }}
                >
                  {getReservationStatusLabel(res.status)}
                </span>
              </div>
              
              <div className="res-body">
                <p><strong>Thời gian đến:</strong> {formatDateTime(res.reservationTime)}</p>
                <p><strong>Số lượng khách:</strong> {res.guestCount} người</p>
                <p><strong>Ngày đặt:</strong> {formatDateTime(res.createdAt)}</p>
                {res.tableId && <p><strong>Mã bàn:</strong> {res.tableId}</p>}
              </div>
              
              <div className="res-footer">
                <Link to={`/customer/reservations/${res.id}`} className="btn-outline">
                  Xem chi tiết
                </Link>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
