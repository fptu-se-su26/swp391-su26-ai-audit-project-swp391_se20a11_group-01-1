import React, { useState, useEffect } from 'react';
import API from '../../services/api';
import './KitchenDashboard.css';

function KitchenDashboard() {
    const [items, setItems] = useState([]);
    const [loading, setLoading] = useState(true);

    const fetchKitchenItems = async () => {
        try {
            const response = await API.get('/kitchen/active-items');
            setItems(response.data);
            setLoading(false);
        } catch (error) {
            console.error("Error fetching kitchen items:", error);
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchKitchenItems();
        // Polling every 10 seconds
        const interval = setInterval(fetchKitchenItems, 10000);
        return () => clearInterval(interval);
    }, []);

    const handleUpdateStatus = async (itemId, newStatus) => {
        try {
            await API.patch(`/kitchen/items/${itemId}/status`, { status: newStatus });
            // Optimistically update the UI
            if (newStatus === 'READY') {
                setItems(items.filter(item => item.orderItemId !== itemId));
            } else {
                setItems(items.map(item => 
                    item.orderItemId === itemId ? { ...item, status: newStatus } : item
                ));
            }
        } catch (error) {
            console.error(`Error updating status for item ${itemId}:`, error);
            alert("Có lỗi xảy ra khi cập nhật trạng thái!");
        }
    };

    const pendingItems = items.filter(item => item.status === 'PENDING');
    const preparingItems = items.filter(item => item.status === 'PREPARING');

    const formatTime = (timeString) => {
        if (!timeString) return '';
        const date = new Date(timeString);
        return date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
    };

    if (loading && items.length === 0) {
        return <div style={{textAlign: 'center', marginTop: '50px'}}>Đang tải dữ liệu bếp...</div>;
    }

    return (
        <div className="kitchen-dashboard">
            <h1 className="kitchen-title">👨‍🍳 KITCHEN DASHBOARD</h1>
            
            <div className="kanban-board">
                {/* Column 1: PENDING */}
                <div className="kanban-column">
                    <h2>CHỜ CHẾ BIẾN ({pendingItems.length})</h2>
                    {pendingItems.map(item => (
                        <div key={item.orderItemId} className="kitchen-card">
                            <div className="kitchen-card-header">
                                <div>
                                    <span className="food-name">{item.foodName}</span>
                                    <span className="quantity-badge">x{item.quantity}</span>
                                </div>
                                <span className="table-badge">{item.tableName || 'Takeaway'}</span>
                            </div>
                            <div className="kitchen-card-body">
                                <p><strong>Mã đơn:</strong> {item.orderCode}</p>
                                <p><strong>Giờ nhận:</strong> {formatTime(item.orderCreatedAt)}</p>
                                {item.note && <p><strong>Ghi chú:</strong> <i>{item.note}</i></p>}
                            </div>
                            <div className="kitchen-card-footer">
                                <button 
                                    className="kitchen-btn btn-start"
                                    onClick={() => handleUpdateStatus(item.orderItemId, 'PREPARING')}
                                >
                                    Bắt đầu nấu &raquo;
                                </button>
                            </div>
                        </div>
                    ))}
                    {pendingItems.length === 0 && <p style={{textAlign:'center', color:'#999'}}>Không có món chờ</p>}
                </div>

                {/* Column 2: PREPARING */}
                <div className="kanban-column">
                    <h2>ĐANG CHẾ BIẾN ({preparingItems.length})</h2>
                    {preparingItems.map(item => (
                        <div key={item.orderItemId} className="kitchen-card" style={{borderLeft: '4px solid #f59f00'}}>
                            <div className="kitchen-card-header">
                                <div>
                                    <span className="food-name">{item.foodName}</span>
                                    <span className="quantity-badge">x{item.quantity}</span>
                                </div>
                                <span className="table-badge">{item.tableName || 'Takeaway'}</span>
                            </div>
                            <div className="kitchen-card-body">
                                <p><strong>Mã đơn:</strong> {item.orderCode}</p>
                                {item.note && <p><strong>Ghi chú:</strong> <i>{item.note}</i></p>}
                            </div>
                            <div className="kitchen-card-footer">
                                <button 
                                    className="kitchen-btn btn-finish"
                                    onClick={() => handleUpdateStatus(item.orderItemId, 'READY')}
                                >
                                    ✔ Nấu xong
                                </button>
                            </div>
                        </div>
                    ))}
                    {preparingItems.length === 0 && <p style={{textAlign:'center', color:'#999'}}>Không có món đang nấu</p>}
                </div>
            </div>
        </div>
    );
}

export default KitchenDashboard;
