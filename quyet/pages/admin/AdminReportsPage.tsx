import React, { useState, useEffect } from 'react';
import { adminReportApi } from '../../api/adminReportApi';
import { getApiErrorMessage } from '../../utils/getApiErrorMessage';
import { formatCurrency } from '../../utils/formatCurrency';
import type { 
  RevenueReportResponse, 
  OrderReportResponse, 
  ReservationReportResponse, 
  TopFoodResponse 
} from '../../types/report';
import './AdminReportsPage.css';

export const AdminReportsPage: React.FC = () => {
  const [fromDate, setFromDate] = useState('');
  const [toDate, setToDate] = useState('');
  
  const [revenueReport, setRevenueReport] = useState<RevenueReportResponse | null>(null);
  const [orderReport, setOrderReport] = useState<OrderReportResponse | null>(null);
  const [topFoods, setTopFoods] = useState<TopFoodResponse[]>([]);
  const [reservationReport, setReservationReport] = useState<ReservationReportResponse | null>(null);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchReports = async () => {
    setLoading(true);
    setError(null);
    try {
      // For backend LocalDateTime format, append :00 to datetime-local values
      const from = fromDate ? `${fromDate}:00` : undefined;
      const to = toDate ? `${toDate}:00` : undefined;

      if (from && to && new Date(from) > new Date(to)) {
        setError('Từ ngày không được lớn hơn Đến ngày');
        setLoading(false);
        return;
      }

      const [rev, ord, top, res] = await Promise.all([
        adminReportApi.getRevenueReport(from, to),
        adminReportApi.getOrderReport(from, to),
        adminReportApi.getTopFoods(from, to, 10),
        adminReportApi.getReservationReport(from, to)
      ]);

      setRevenueReport(rev);
      setOrderReport(ord);
      setTopFoods(top);
      setReservationReport(res);
    } catch (err) {
      setError(getApiErrorMessage(err, 'Lỗi khi tải báo cáo.'));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchReports();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleFilter = (e: React.FormEvent) => {
    e.preventDefault();
    fetchReports();
  };

  return (
    <div className="admin-reports-page">
      <div className="admin-header">
        <h2>Báo cáo chi tiết</h2>
      </div>

      <div className="filter-section">
        <form onSubmit={handleFilter} className="filter-form">
          <div className="filter-group">
            <label>Từ ngày:</label>
            <input 
              type="datetime-local" 
              value={fromDate}
              onChange={(e) => setFromDate(e.target.value)}
            />
          </div>
          <div className="filter-group">
            <label>Đến ngày:</label>
            <input 
              type="datetime-local" 
              value={toDate}
              onChange={(e) => setToDate(e.target.value)}
            />
          </div>
          <button type="submit" className="btn-filter" disabled={loading}>
            {loading ? 'Đang lọc...' : 'Lọc dữ liệu'}
          </button>
        </form>
        {error && <div className="filter-error">{error}</div>}
      </div>

      {loading && !revenueReport && (
        <div className="admin-page-state">Đang tải dữ liệu báo cáo...</div>
      )}

      {!loading && revenueReport && (
        <div className="reports-container">
          {/* Revenue Report */}
          <div className="report-card">
            <h3>Báo cáo Doanh thu</h3>
            <div className="report-summary">
              <div className="summary-item">
                <span className="label">Tổng doanh thu:</span>
                <span className="value highlight">{formatCurrency(revenueReport.totalRevenue)}</span>
              </div>
              <div className="summary-item">
                <span className="label">Tổng số thanh toán:</span>
                <span className="value">{revenueReport.totalPaidPayments}</span>
              </div>
              <div className="summary-item">
                <span className="label">Tổng số hóa đơn:</span>
                <span className="value">{revenueReport.totalInvoices}</span>
              </div>
            </div>
            
            {revenueReport.dailyRevenue && revenueReport.dailyRevenue.length > 0 && (
              <div className="data-table-wrapper">
                <table className="data-table">
                  <thead>
                    <tr>
                      <th>Ngày</th>
                      <th className="text-right">Doanh thu</th>
                    </tr>
                  </thead>
                  <tbody>
                    {revenueReport.dailyRevenue.map((dr, idx) => (
                      <tr key={idx}>
                        <td>{dr.date}</td>
                        <td className="text-right">{formatCurrency(dr.revenue)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>

          {/* Orders Report */}
          {orderReport && (
            <div className="report-card">
              <h3>Báo cáo Đơn hàng</h3>
              <div className="report-summary">
                <div className="summary-item">
                  <span className="label">Tổng số đơn:</span>
                  <span className="value">{orderReport.totalOrders}</span>
                </div>
                <div className="summary-item">
                  <span className="label">Giá trị ước tính:</span>
                  <span className="value highlight">{formatCurrency(orderReport.totalAmount)}</span>
                </div>
              </div>
              
              <div className="data-table-wrapper">
                <table className="data-table">
                  <thead>
                    <tr>
                      <th>Trạng thái đơn</th>
                      <th className="text-right">Số lượng</th>
                    </tr>
                  </thead>
                  <tbody>
                    {Object.entries(orderReport.byStatus || {}).map(([status, count]) => (
                      <tr key={status}>
                        <td>{status}</td>
                        <td className="text-right">{count}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}

          {/* Top Foods */}
          <div className="report-card">
            <h3>Top 10 Món bán chạy</h3>
            {topFoods.length === 0 ? (
              <p className="empty-state">Không có dữ liệu món ăn trong thời gian này.</p>
            ) : (
              <div className="data-table-wrapper">
                <table className="data-table">
                  <thead>
                    <tr>
                      <th>Tên món</th>
                      <th className="text-right">Số lượng bán</th>
                      <th className="text-right">Doanh thu mang lại</th>
                    </tr>
                  </thead>
                  <tbody>
                    {topFoods.map((tf, idx) => (
                      <tr key={idx}>
                        <td>{tf.foodName}</td>
                        <td className="text-right">{tf.totalQuantity}</td>
                        <td className="text-right highlight">{formatCurrency(tf.totalRevenue)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>

          {/* Reservations Report */}
          {reservationReport && (
            <div className="report-card">
              <h3>Báo cáo Đặt bàn</h3>
              <div className="report-summary">
                <div className="summary-item">
                  <span className="label">Tổng lượt đặt:</span>
                  <span className="value">{reservationReport.totalReservations}</span>
                </div>
                <div className="summary-item">
                  <span className="label">Tổng số khách đến:</span>
                  <span className="value">{reservationReport.totalGuests}</span>
                </div>
              </div>
              
              <div className="data-table-wrapper">
                <table className="data-table">
                  <thead>
                    <tr>
                      <th>Trạng thái đặt bàn</th>
                      <th className="text-right">Số lượng</th>
                    </tr>
                  </thead>
                  <tbody>
                    {Object.entries(reservationReport.byStatus || {}).map(([status, count]) => (
                      <tr key={status}>
                        <td>{status}</td>
                        <td className="text-right">{count}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}

        </div>
      )}
    </div>
  );
};
