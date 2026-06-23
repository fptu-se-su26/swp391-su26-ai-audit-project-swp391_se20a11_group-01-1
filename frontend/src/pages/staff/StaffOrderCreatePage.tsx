import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { staffOrderApi } from '../../api/staffOrderApi';
import { categoryApi } from '../../api/categoryApi';
import { foodApi } from '../../api/foodApi';
import { tableApi } from '../../api/tableApi';
import { getApiErrorMessage } from '../../utils/getApiErrorMessage';
import { formatCurrency } from '../../utils/formatCurrency';
import type { StaffCreateOrderRequest, StaffOrderItemRequest } from '../../types/staff';
import type { CategoryResponse } from '../../types/category';
import type { FoodResponse } from '../../types/food';
import type { TableResponse } from '../../types/table';
import type { OrderType } from '../../types/order';
import './StaffOrderCreatePage.css';

export const StaffOrderCreatePage: React.FC = () => {
  const navigate = useNavigate();

  const [categories, setCategories] = useState<CategoryResponse[]>([]);
  const [foods, setFoods] = useState<FoodResponse[]>([]);
  const [tables, setTables] = useState<TableResponse[]>([]);
  
  const [orderType, setOrderType] = useState<OrderType>('DINE_IN');
  const [tableId, setTableId] = useState<number | ''>('');
  const [orderNote, setOrderNote] = useState('');
  
  const [selectedCategoryId, setSelectedCategoryId] = useState<number | ''>('');
  const [cartItems, setCartItems] = useState<(StaffOrderItemRequest & { foodName: string, unitPrice: number })[]>([]);

  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      try {
        const [cats, tbls] = await Promise.all([
          categoryApi.getActiveCategories(),
          tableApi.getAllTables()
        ]);
        setCategories(cats);
        // Only show AVAILABLE tables
        setTables(tbls.filter((t: TableResponse) => t.status === 'AVAILABLE'));
      } catch (err) {
        setError(getApiErrorMessage(err, 'Lỗi tải dữ liệu ban đầu.'));
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  useEffect(() => {
    const fetchFoods = async () => {
      try {
        const data = await foodApi.getPublicFoods({ page: 0, size: 100, categoryId: selectedCategoryId || undefined });
        // Only active foods
        setFoods(data.content.filter((f: FoodResponse) => f.isAvailable));
      } catch (err) {
        console.error('Lỗi khi tải món ăn', err);
      }
    };
    fetchFoods();
  }, [selectedCategoryId]);

  const handleAddFood = (food: FoodResponse) => {
    setCartItems(prev => {
      const existing = prev.find(i => i.foodItemId === food.id);
      if (existing) {
        return prev.map(i => i.foodItemId === food.id ? { ...i, quantity: i.quantity + 1 } : i);
      }
      return [...prev, { foodItemId: food.id, foodName: food.name, unitPrice: food.price, quantity: 1, note: '' }];
    });
  };

  const handleUpdateQuantity = (foodId: number, delta: number) => {
    setCartItems(prev => prev.map(i => {
      if (i.foodItemId === foodId) {
        const newQ = i.quantity + delta;
        return newQ > 0 ? { ...i, quantity: newQ } : i;
      }
      return i;
    }));
  };

  const handleUpdateItemNote = (foodId: number, note: string) => {
    setCartItems(prev => prev.map(i => i.foodItemId === foodId ? { ...i, note } : i));
  };

  const handleRemoveItem = (foodId: number) => {
    setCartItems(prev => prev.filter(i => i.foodItemId !== foodId));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (cartItems.length === 0) {
      setError('Vui lòng chọn ít nhất 1 món.');
      return;
    }
    if (orderType === 'DINE_IN' && !tableId) {
      setError('Vui lòng chọn bàn cho đơn Tại bàn.');
      return;
    }

    setSubmitting(true);
    setError(null);

    const request: StaffCreateOrderRequest = {
      orderType,
      tableId: orderType === 'DINE_IN' ? Number(tableId) : null,
      note: orderNote.trim() || undefined,
      items: cartItems.map(({ foodItemId, quantity, note }) => ({ 
        foodItemId, 
        quantity, 
        note: note?.trim() || undefined 
      }))
    };

    try {
      const newOrder = await staffOrderApi.createOrder(request);
      navigate(`/staff/orders/${newOrder.id}`);
    } catch (err) {
      setError(getApiErrorMessage(err, 'Lỗi khi tạo đơn hàng.'));
      setSubmitting(false);
    }
  };

  const currentTotal = cartItems.reduce((sum, item) => sum + (item.unitPrice * item.quantity), 0);

  if (loading) return <div className="staff-page-state">Đang tải dữ liệu...</div>;

  return (
    <div className="staff-order-create-page">
      <div className="page-header">
        <button onClick={() => navigate('/staff/orders')} className="btn-back">
          &larr; Quay lại
        </button>
        <h2>Tạo Đơn Hàng Tại Quầy</h2>
      </div>

      {error && <div className="error-message">{error}</div>}

      <form className="create-order-layout" onSubmit={handleSubmit}>
        <div className="menu-section">
          <h3>Menu Chọn Món</h3>
          <div className="category-filter">
            <button 
              type="button"
              className={`cat-btn ${!selectedCategoryId ? 'active' : ''}`}
              onClick={() => setSelectedCategoryId('')}
            >
              Tất cả
            </button>
            {categories.map(cat => (
              <button 
                type="button"
                key={cat.id} 
                className={`cat-btn ${selectedCategoryId === cat.id ? 'active' : ''}`}
                onClick={() => setSelectedCategoryId(cat.id)}
              >
                {cat.name}
              </button>
            ))}
          </div>

          <div className="food-grid">
            {foods.map(food => (
              <div key={food.id} className="food-item-card" onClick={() => handleAddFood(food)}>
                {food.imageUrl ? (
                  <img src={food.imageUrl} alt={food.name} className="food-img" />
                ) : (
                  <div className="food-img-placeholder">Không có ảnh</div>
                )}
                <div className="food-info">
                  <div className="food-name">{food.name}</div>
                  <div className="food-price">{formatCurrency(food.price)}</div>
                </div>
              </div>
            ))}
            {foods.length === 0 && <div className="empty-state">Không có món ăn.</div>}
          </div>
        </div>

        <div className="cart-section">
          <h3>Đơn hàng</h3>
          
          <div className="order-details-form">
            <div className="form-group">
              <label>Loại đơn:</label>
              <select value={orderType} onChange={(e) => {
                setOrderType(e.target.value as OrderType);
                if (e.target.value !== 'DINE_IN') setTableId('');
              }}>
                <option value="DINE_IN">Tại bàn</option>
                <option value="TAKEAWAY">Mang về</option>
                <option value="DELIVERY">Giao hàng</option>
              </select>
            </div>

            {orderType === 'DINE_IN' && (
              <div className="form-group">
                <label>Chọn bàn (Trống):</label>
                <select value={tableId} onChange={(e) => setTableId(e.target.value ? Number(e.target.value) : '')} required>
                  <option value="">-- Chọn bàn --</option>
                  {tables.map(t => (
                    <option key={t.id} value={t.id}>Bàn {t.tableNumber} (Sức chứa: {t.capacity})</option>
                  ))}
                </select>
              </div>
            )}

            <div className="form-group">
              <label>Ghi chú chung:</label>
              <textarea 
                value={orderNote} 
                onChange={(e) => setOrderNote(e.target.value)}
                placeholder="Ghi chú cho bếp..."
                rows={2}
              />
            </div>
          </div>

          <div className="cart-items">
            {cartItems.map(item => (
              <div key={item.foodItemId} className="cart-item">
                <div className="cart-item-header">
                  <strong>{item.foodName}</strong>
                  <span className="cart-item-price">{formatCurrency(item.unitPrice * item.quantity)}</span>
                </div>
                <div className="cart-item-controls">
                  <div className="qty-controls">
                    <button type="button" onClick={() => handleUpdateQuantity(item.foodItemId, -1)} disabled={item.quantity <= 1}>-</button>
                    <span>{item.quantity}</span>
                    <button type="button" onClick={() => handleUpdateQuantity(item.foodItemId, 1)}>+</button>
                  </div>
                  <button type="button" className="btn-remove" onClick={() => handleRemoveItem(item.foodItemId)}>Xóa</button>
                </div>
                <input 
                  type="text" 
                  placeholder="Ghi chú món (VD: ít cay)" 
                  value={item.note || ''}
                  onChange={(e) => handleUpdateItemNote(item.foodItemId, e.target.value)}
                  className="item-note-input"
                />
              </div>
            ))}
            {cartItems.length === 0 && <div className="empty-cart">Chưa chọn món nào.</div>}
          </div>

          <div className="cart-footer">
            <div className="cart-total">
              <span>Tạm tính:</span>
              <span className="highlight-price">{formatCurrency(currentTotal)}</span>
            </div>
            <button 
              type="submit" 
              className="btn-submit-order" 
              disabled={submitting || cartItems.length === 0}
            >
              {submitting ? 'Đang tạo...' : 'Tạo Đơn Hàng'}
            </button>
          </div>
        </div>
      </form>
    </div>
  );
};
