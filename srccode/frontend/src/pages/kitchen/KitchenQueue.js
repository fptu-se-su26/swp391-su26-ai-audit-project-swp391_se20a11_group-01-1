import React, { useCallback, useEffect, useRef, useState } from 'react';
import API from '../../services/api';
import './KitchenQueue.css';

const statusMap = {
  CONFIRMED: {
    label: 'Đã xác nhận',
    cls: 'item-pending',
    next: 'PREPARING',
    action: 'Bắt đầu làm'
  },
  PREPARING: {
    label: 'Đang chế biến',
    cls: 'item-cooking',
    next: 'READY',
    action: 'Sẵn sàng'
  }
};

const KITCHEN_REFRESH_INTERVAL_MS = 8000;

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

function elapsed(createdAt) {
  if (!createdAt) return '';

  const createdDate = new Date(createdAt);

  if (Number.isNaN(createdDate.getTime())) {
    return '';
  }

  const mins = Math.floor((Date.now() - createdDate.getTime()) / 60000);

  if (mins < 1) return '< 1 phút';

  return `${mins} phút`;
}

function urgencyClass(createdAt) {
  if (!createdAt) return '';

  const createdDate = new Date(createdAt);

  if (Number.isNaN(createdDate.getTime())) {
    return '';
  }

  const mins = Math.floor((Date.now() - createdDate.getTime()) / 60000);

  if (mins >= 20) return 'urgent-high';
  if (mins >= 10) return 'urgent-med';

  return '';
}

function KitchenItemVisual({ item }) {
  const [imageFailed, setImageFailed] = useState(false);

  useEffect(() => {
    setImageFailed(false);
  }, [item.imageUrl]);

  if (item.imageUrl && !imageFailed) {
    return (
      <img
        className="kitem-image"
        src={item.imageUrl}
        alt={item.foodName || 'Món ăn'}
        onError={() => setImageFailed(true)}
      />
    );
  }

  return (
    <span className="kitem-emoji" aria-hidden="true">
      {item.emoji || '🍽️'}
    </span>
  );
}

function KitchenQueue() {
  const [queue, setQueue] = useState([]);
  const [clock, setClock] = useState(new Date());
  const [filter, setFilter] = useState('all'); // all | CONFIRMED | PREPARING
  const [loading, setLoading] = useState(true);
  const [actionLoadingIds, setActionLoadingIds] = useState(() => new Set());
  const [error, setError] = useState('');
  const mountedRef = useRef(false);
  const fetchInFlightRef = useRef(false);
  const actionInFlightIdsRef = useRef(new Set());
  const queueRevisionRef = useRef(0);

  const fetchKitchenItems = useCallback(async (showLoading = true) => {
    if (fetchInFlightRef.current || actionInFlightIdsRef.current.size > 0) {
      return;
    }

    fetchInFlightRef.current = true;
    const revisionAtStart = queueRevisionRef.current;

    if (showLoading) {
      setLoading(true);
    }

    try {
      const response = await API.get(
        '/orders/kitchen/items?statuses=CONFIRMED,PREPARING'
      );
      const items = Array.isArray(response.data) ? [...response.data] : [];

      items.sort((a, b) => {
        return (Date.parse(a.createdAt) || 0) - (Date.parse(b.createdAt) || 0);
      });

      if (mountedRef.current && revisionAtStart === queueRevisionRef.current) {
        setQueue(items);
      }

      if (mountedRef.current) {
        setError('');
      }
    } catch (requestError) {
      if (mountedRef.current) {
        const message = getApiMessage(
          requestError.response?.data,
          'Không thể tải hàng đợi bếp.'
        );
        setError((current) => (current === message ? current : message));
      }
    } finally {
      fetchInFlightRef.current = false;

      if (showLoading && mountedRef.current) {
        setLoading(false);
      }
    }
  }, []);

  useEffect(() => {
    mountedRef.current = true;
    fetchKitchenItems();

    const clockTimer = setInterval(() => {
      setClock(new Date());
    }, 1000);

    const refreshTimer = setInterval(() => {
      fetchKitchenItems(false);
    }, KITCHEN_REFRESH_INTERVAL_MS);

    return () => {
      mountedRef.current = false;
      clearInterval(clockTimer);
      clearInterval(refreshTimer);
    };
  }, [fetchKitchenItems]);

  const updateItemStatus = async (item, nextStatus) => {
    const itemId = item.orderItemId;

    if (!itemId || actionInFlightIdsRef.current.has(itemId)) {
      return;
    }

    actionInFlightIdsRef.current.add(itemId);
    queueRevisionRef.current += 1;
    setActionLoadingIds((current) => new Set(current).add(itemId));
    setError('');

    try {
      const response = await API.patch(`/orders/items/${itemId}/status`, {
        status: nextStatus
      });
      const updatedItem =
        response.data && typeof response.data === 'object'
          ? response.data
          : {
              ...item,
              status: nextStatus,
              statusUpdatedAt: new Date().toISOString()
            };

      if (mountedRef.current) {
        setQueue((current) => {
          if (nextStatus === 'READY') {
            return current.filter((queueItem) => queueItem.orderItemId !== itemId);
          }

          return current.map((queueItem) =>
            queueItem.orderItemId === itemId ? updatedItem : queueItem
          );
        });
      }
    } catch (requestError) {
      if (mountedRef.current) {
        setError(
          getApiMessage(
            requestError.response?.data,
            'Không thể cập nhật trạng thái món.'
          )
        );
      }
    } finally {
      actionInFlightIdsRef.current.delete(itemId);

      if (mountedRef.current) {
        setActionLoadingIds((current) => {
          const next = new Set(current);
          next.delete(itemId);
          return next;
        });
      }
    }
  };

  const formatTime = (value) => {
    if (!value) return '';

    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
      return value;
    }

    return date.toLocaleTimeString('vi-VN', {
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const getItemStatusInfo = (status) => {
    return (
      statusMap[status] || {
        label: status || 'Không xác định',
        cls: 'item-pending',
        next: null,
        action: ''
      }
    );
  };

  const getTableDisplay = (item) => {
    return item.tableName || (item.tableId ? `Bàn ${item.tableId}` : 'Không có bàn');
  };

  const filteredQueue = queue.filter((item) => {
    if (filter === 'all') return true;
    return item.status === filter;
  });

  const confirmedCount = queue.filter((item) => item.status === 'CONFIRMED').length;
  const preparingCount = queue.filter((item) => item.status === 'PREPARING').length;

  if (loading) {
    return (
      <div className="kitchen-queue">
        <div className="kitchen-topbar">
          <div className="kitchen-clock">
            🕐 {clock.toLocaleTimeString('vi-VN')}
          </div>
        </div>

        <div className="kitchen-empty">
          <p>⏳ Đang tải hàng đợi bếp...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="kitchen-queue">
      <div className="kitchen-topbar">
        <div className="kitchen-clock">
          🕐 {clock.toLocaleTimeString('vi-VN')}
        </div>

        <div className="kitchen-summary">
          <span className="ks-badge ks-total">{queue.length} món</span>
          <span className="ks-badge ks-pending">⏳ {confirmedCount} đã xác nhận</span>
          <span className="ks-badge ks-cooking">🔥 {preparingCount} đang chế biến</span>

          <button
            className="kfilter-btn"
            onClick={() => fetchKitchenItems(false)}
            style={{ marginLeft: 8 }}
          >
            🔄 Làm mới
          </button>
        </div>
      </div>

      <div className="kitchen-filters">
        {[
          ['all', 'Tất cả'],
          ['CONFIRMED', 'Đã xác nhận'],
          ['PREPARING', 'Đang chế biến']
        ].map(([value, label]) => (
          <button
            key={value}
            className={`kfilter-btn ${filter === value ? 'active' : ''}`}
            onClick={() => setFilter(value)}
          >
            {label}
          </button>
        ))}
      </div>

      {error && (
        <div className="kitchen-empty" style={{ color: '#dc2626' }}>
          <p>⚠️ {error}</p>
        </div>
      )}

      <div className="kitchen-cards">
        {!error && filteredQueue.length === 0 && (
          <div className="kitchen-empty">
            <p>Hiện không có món nào đang chờ xử lý.</p>
          </div>
        )}

        {filteredQueue.map((item) => {
          const urgent = urgencyClass(item.createdAt);
          const statusInfo = getItemStatusInfo(item.status);
          const isActionLoading = actionLoadingIds.has(item.orderItemId);

          return (
            <div
              key={item.orderItemId}
              className={`kitchen-card ${urgent}`}
            >
              <div className="kcard-header">
                <div className="kcard-left">
                  <span className="kcard-id">
                    {item.orderCode || `#${item.orderId}`}
                  </span>

                  <span className="kcard-table">
                    🪑 {getTableDisplay(item)}
                  </span>
                </div>

                <div className="kcard-right">
                  <span className="kcard-time">
                    ⏱ {formatTime(item.createdAt)}
                  </span>

                  <span className={`kcard-elapsed ${urgent}`}>
                    {elapsed(item.createdAt)}
                    {urgent === 'urgent-high' && ' ⚠️'}
                  </span>
                </div>
              </div>

              <div className="kcard-items">
                <div className={`kcard-item ${statusInfo.cls}`}>
                  <div className="kitem-media">
                    <KitchenItemVisual item={item} />
                  </div>

                  <div className="kitem-content">
                    <div className="kitem-info">
                      <span className="kitem-name">
                        {item.foodName || `Món #${item.foodId}`}
                      </span>

                      <span className="kitem-qty">× {item.quantity}</span>
                    </div>

                    {item.note && (
                      <div className="kitem-note">📝 {item.note}</div>
                    )}
                  </div>

                  <div className="kitem-right">
                    <span className="kitem-status-label">
                      {statusInfo.label}
                    </span>
                  </div>
                </div>
              </div>

              <div className="kcard-ready">
                <span>
                  {item.status === 'CONFIRMED'
                    ? '⏳ Món đang chờ bắt đầu'
                    : '🔥 Món đang được chế biến'}
                </span>

                {statusInfo.next && (
                  <button
                    className="kcard-serve-btn"
                    disabled={isActionLoading}
                    onClick={() => updateItemStatus(item, statusInfo.next)}
                  >
                    {isActionLoading ? 'Đang xử lý...' : statusInfo.action}
                  </button>
                )}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}

export default KitchenQueue;
