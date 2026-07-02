import React, { useState, useEffect, useCallback } from 'react';
import { useParams, Link } from 'react-router-dom';
import { reservationApi } from '../../api/reservationApi';
import { getApiErrorMessage } from '../../utils/getApiErrorMessage';
import { formatDateTime } from '../../utils/formatDateTime';
import { getReservationStatusLabel, getReservationStatusColor } from '../../utils/reservationStatus';
import type { ReservationDetailResponse } from '../../types/reservation';
import './ReservationDetailPage.css';

export const ReservationDetailPage: React.FC = () => {
  const { reservationId } = useParams<{ reservationId: string }>();
  const [reservation, setReservation] = useState<ReservationDetailResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [cancelling, setCancelling] = useState(false);

  const fetchReservation = useCallback(async () => {
    if (!reservationId) return;
    try {
      const data = await reservationApi.getReservationById(Number(reservationId));
      setReservation(data);
    } catch (err) {
      setError(getApiErrorMessage(err, 'Không thể tải chi tiết đặt bàn.'));
    }
  }, [reservationId]);

  useEffect(() => {
    let mounted = true;
    const loadData = async () => {
      setLoading(true);
      setError(null);
      try {
        const data = await reservationApi.getReservationById(Number(reservationId));
        if (mounted) setReservation(data);
      } catch (err) {
        if (mounted) setError(getApiErrorMessage(err, 'Không thể tải chi tiết đặt bàn.'));
      } finally {
        if (mounted) setLoading(false);
      }
    };
    loadData();
    return () => { mounted = false; };
  }, [reservationId]);

  const handleCancelReservation = async () => {
    if (!reservation) return;
    const reason = window.prompt('Nhập lý do hủy (tùy chọn):');
    if (reason === null) return; // user cancelled prompt

    setCancelling(true);
    try {
      const payload = reason.trim() ? { reason: reason.trim() } : {};
      await reservationApi.cancelReservation(reservation.id, payload);
      alert('Đã hủy đặt bàn thành công.');
      await fetchReservation();
    } catch (err) {
      alert(getApiErrorMessage(err, 'Lỗi khi hủy đặt bàn.'));
    } finally {
      setCancelling(false);
    }
  };

  if (loading) return <div className="reservation-state">Đang tải chi tiết đặt bàn...</div>;
  
  if (error || !reservation) {
    return (
      <div className="reservation-state text-error">
        <p>{error || 'Không tìm thấy đặt bàn.'}</p>
        <Link to="/customer/reservations" className="btn-secondary">Quay lại danh sách</Link>
      </div>
    );
  }

  const canCancel = reservation.status === 'PENDING' || reservation.status === 'CONFIRMED';

  return (
    <div className="reservation-detail-page">
      <div className="page-header">
        <h2>Chi tiết đặt bàn #{reservation.id}</h2>
        <Link to="/customer/reservations" className="btn-back">Trở về</Link>
      </div>

      <div className="res-info-cards">
        <div className="info-card">
          <h3>Thông tin chung</h3>
          <p><strong>Khách hàng:</strong> {reservation.customerName}</p>
          <p><strong>Ngày tạo:</strong> {formatDateTime(reservation.createdAt)}</p>
          <p>
            <strong>Trạng thái:</strong>{' '}
            <span 
              className="status-badge"
              style={{ backgroundColor: getReservationStatusColor(reservation.status) }}
            >
              {getReservationStatusLabel(reservation.status)}
            </span>
          </p>
        </div>

        <div className="info-card">
          <h3>Thông tin xếp bàn</h3>
          <p><strong>Thời gian đến:</strong> <span className="highlight-text">{formatDateTime(reservation.reservationTime)}</span></p>
          <p><strong>Số lượng khách:</strong> {reservation.guestCount} người</p>
          {reservation.table ? (
            <div className="table-details">
              <p><strong>Bàn xếp:</strong> Bàn {reservation.table.tableNumber}</p>
              <p><strong>Khu vực:</strong> {reservation.table.location}</p>
              <p><strong>Sức chứa:</strong> {reservation.table.capacity} người</p>
            </div>
          ) : (
            <p><strong>Bàn xếp:</strong> Chưa được xếp bàn</p>
          )}
          {reservation.specialRequest && (
            <div className="special-request-box">
              <strong>Ghi chú:</strong> {reservation.specialRequest}
            </div>
          )}
        </div>
      </div>

      {canCancel && (
        <div className="res-actions">
          <button 
            className="btn-cancel" 
            onClick={handleCancelReservation}
            disabled={cancelling}
          >
            {cancelling ? 'Đang xử lý...' : 'Hủy đặt bàn'}
          </button>
          <p className="cancel-note">Lưu ý: Chỉ có thể hủy đơn đặt bàn đang ở trạng thái Chờ xác nhận hoặc Đã xác nhận.</p>
        </div>
      )}
    </div>
  );
};
