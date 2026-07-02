import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { reservationApi } from '../../api/reservationApi';
import { getApiErrorMessage } from '../../utils/getApiErrorMessage';
import type { TableResponse } from '../../types/table';
import './ReservationCreatePage.css';

export const ReservationCreatePage: React.FC = () => {
  const navigate = useNavigate();
  
  const [reservationTime, setReservationTime] = useState('');
  const [guestCount, setGuestCount] = useState<number>(1);
  const [tableId, setTableId] = useState<string>(''); // empty means auto-assign
  const [specialRequest, setSpecialRequest] = useState('');
  
  const [tables, setTables] = useState<TableResponse[]>([]);
  const [loadingTables, setLoadingTables] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let mounted = true;
    const fetchTables = async () => {
      setLoadingTables(true);
      try {
        const data = await reservationApi.getAvailableTables();
        if (mounted) setTables(data);
      } catch (err) {
        if (mounted) console.error('Failed to load tables', err);
      } finally {
        if (mounted) setLoadingTables(false);
      }
    };
    fetchTables();
    return () => { mounted = false; };
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    if (!reservationTime) {
      setError('Vui lòng chọn thời gian đặt bàn.');
      return;
    }

    const selectedTime = new Date(reservationTime);
    if (selectedTime < new Date()) {
      setError('Thời gian đặt bàn phải trong tương lai.');
      return;
    }

    if (guestCount < 1) {
      setError('Số lượng khách phải lớn hơn 0.');
      return;
    }

    if (tableId) {
      const table = tables.find(t => t.id === Number(tableId));
      if (table && table.capacity < guestCount) {
        setError(`Bàn này chỉ chứa tối đa ${table.capacity} người.`);
        return;
      }
    }

    setSubmitting(true);
    try {
      // datetime-local value is "YYYY-MM-DDThh:mm". Append seconds to match typical LocalDateTime format without timezone.
      const payload = {
        reservationTime: `${reservationTime}:00`,
        guestCount,
        ...(tableId ? { tableId: Number(tableId) } : {}),
        ...(specialRequest.trim() ? { specialRequest: specialRequest.trim() } : {})
      };
      
      const res = await reservationApi.createReservation(payload);
      alert('Đặt bàn thành công!');
      navigate(`/customer/reservations/${res.id}`, { replace: true });
    } catch (err) {
      setError(getApiErrorMessage(err, 'Lỗi khi đặt bàn. Vui lòng thử lại.'));
    } finally {
      setSubmitting(false);
    }
  };

  // Filter tables that can accommodate the current guestCount
  const suitableTables = tables.filter(t => t.capacity >= guestCount);

  return (
    <div className="reservation-create-page">
      <div className="page-header">
        <h2>Đặt bàn mới</h2>
        <Link to="/customer/reservations" className="btn-back">Hủy</Link>
      </div>

      <div className="reservation-form-container">
        {error && <div className="error-message">{error}</div>}
        
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="reservationTime">Thời gian đến <span className="text-danger">*</span></label>
            <input
              type="datetime-local"
              id="reservationTime"
              value={reservationTime}
              onChange={(e) => setReservationTime(e.target.value)}
              required
            />
            <small className="help-text">Nhà hàng phục vụ trong thời gian hoạt động. Thời gian giữ bàn mặc định là 2 tiếng.</small>
          </div>

          <div className="form-group">
            <label htmlFor="guestCount">Số lượng khách <span className="text-danger">*</span></label>
            <input
              type="number"
              id="guestCount"
              min="1"
              max="50"
              value={guestCount}
              onChange={(e) => setGuestCount(Number(e.target.value))}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="tableId">Chọn bàn (Tùy chọn)</label>
            {loadingTables ? (
              <p className="loading-text">Đang tải danh sách bàn...</p>
            ) : (
              <select
                id="tableId"
                value={tableId}
                onChange={(e) => setTableId(e.target.value)}
              >
                <option value="">-- Tự động xếp bàn --</option>
                {suitableTables.map(t => (
                  <option key={t.id} value={t.id}>
                    Bàn {t.tableNumber} - {t.location} (Tối đa {t.capacity} người) {t.status === 'AVAILABLE' ? '' : `[Đang ${t.status}]`}
                  </option>
                ))}
              </select>
            )}
            <small className="help-text">
              Bạn có thể để trống để nhà hàng tự động sắp xếp bàn phù hợp có sẵn. 
              Nếu bàn bạn chọn bị trùng lịch, hệ thống sẽ báo lỗi.
            </small>
          </div>

          <div className="form-group">
            <label htmlFor="specialRequest">Ghi chú / Yêu cầu đặc biệt</label>
            <textarea
              id="specialRequest"
              rows={3}
              value={specialRequest}
              onChange={(e) => setSpecialRequest(e.target.value)}
              placeholder="Ví dụ: Ghế em bé, dị ứng đậu phộng..."
            />
          </div>

          <button 
            type="submit" 
            className="btn-submit" 
            disabled={submitting}
          >
            {submitting ? 'Đang xử lý...' : 'Xác nhận đặt bàn'}
          </button>
        </form>
      </div>
    </div>
  );
};
