import React, { useCallback, useEffect, useRef, useState } from 'react';
import API from '../../services/api';
import './KitchenHistory.css';

function getApiMessage(data, fallback) {
  if (!data) return fallback;

  if (typeof data === 'string') {
    return data.trim() || fallback;
  }

  if (typeof data === 'object') {
    return data.message || data.detail || data.error || fallback;
  }

  return fallback;
}

function formatDateTime(value) {
  if (!value) return '';

  const date = new Date(value);

  if (Number.isNaN(date.getTime())) {
    return value;
  }

  return date.toLocaleString('vi-VN', {
    hour: '2-digit',
    minute: '2-digit',
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  });
}

function KitchenHistory() {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const mountedRef = useRef(false);
  const fetchInFlightRef = useRef(false);

  const fetchKitchenHistory = useCallback(async () => {
    if (fetchInFlightRef.current) {
      return;
    }

    fetchInFlightRef.current = true;
    setLoading(true);

    try {
      const response = await API.get('/orders/kitchen/items?statuses=READY');
      const items = Array.isArray(response.data) ? [...response.data] : [];

      items.sort((a, b) => {
        const aTime = Date.parse(a.statusUpdatedAt || a.createdAt) || 0;
        const bTime = Date.parse(b.statusUpdatedAt || b.createdAt) || 0;
        return bTime - aTime;
      });

      if (mountedRef.current) {
        setHistory(items);
        setError('');
      }
    } catch (requestError) {
      if (mountedRef.current) {
        setError(
          getApiMessage(
            requestError.response?.data,
            'Không thể tải lịch sử bếp.'
          )
        );
      }
    } finally {
      fetchInFlightRef.current = false;

      if (mountedRef.current) {
        setLoading(false);
      }
    }
  }, []);

  useEffect(() => {
    mountedRef.current = true;
    fetchKitchenHistory();

    return () => {
      mountedRef.current = false;
    };
  }, [fetchKitchenHistory]);

  const getTableDisplay = (item) => {
    return item.tableName || (item.tableId ? `Bàn ${item.tableId}` : 'Không có bàn');
  };

  if (loading) {
    return (
      <div className="kitchen-history">
        <h1 className="kitchen-history-title">Lịch sử bếp</h1>

        <div className="history-card history-empty">
          ⏳ Đang tải lịch sử bếp...
        </div>
      </div>
    );
  }

  return (
    <div className="kitchen-history">
      <div className="kitchen-history-header">
        <h1 className="kitchen-history-title">Lịch sử bếp</h1>

        <button className="history-refresh-btn" onClick={fetchKitchenHistory}>
          🔄 Làm mới
        </button>
      </div>

      {error && (
        <div className="history-card" style={{ color: '#dc2626' }}>
          ⚠️ {error}
        </div>
      )}

      {!error && history.length === 0 ? (
        <div className="history-card history-empty">
          Chưa có món nào hoàn thành.
        </div>
      ) : (
        <div className="history-list">
          {history.map((item) => (
            <div key={item.orderItemId} className="history-card">
              <div className="history-header">
                <span className="history-id">
                  {item.orderCode || `#${item.orderId}`}
                </span>

                <span className="history-table">
                  🪑 {getTableDisplay(item)}
                </span>

                <span className="history-time">
                  ✅ {formatDateTime(item.statusUpdatedAt || item.createdAt)}
                </span>

                <span className="history-status history-ready">
                  Sẵn sàng phục vụ
                </span>
              </div>

              <div className="history-items">
                <span className="history-tag">
                  {item.emoji ? `${item.emoji} ` : ''}
                  {item.foodName || `Món #${item.foodId}`} × {item.quantity}
                </span>
              </div>

              {item.note && (
                <div className="history-note">
                  📝 {item.note}
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default KitchenHistory;
