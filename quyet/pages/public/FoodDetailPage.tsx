import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { foodApi } from '../../api/foodApi';
import type { FoodDetailResponse } from '../../types/food';
import { formatCurrency } from '../../utils/formatCurrency';
import { useAuth } from '../../hooks/useAuth';
import { useCart } from '../../hooks/useCart';
import './FoodDetailPage.css';

interface FoodDetailPageProps {
  backUrl: string;
}

export const FoodDetailPage: React.FC<FoodDetailPageProps> = ({ backUrl }) => {
  const { foodId } = useParams<{ foodId: string }>();
  const navigate = useNavigate();
  const { isAuthenticated, user } = useAuth();
  const { addItem } = useCart();
  const isCustomer = isAuthenticated && user?.roles?.includes('ROLE_CUSTOMER');

  const [food, setFood] = useState<FoodDetailResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [adding, setAdding] = useState(false);

  useEffect(() => {
    const fetchFoodDetail = async () => {
      if (!foodId) return;
      setLoading(true);
      setError(null);
      try {
        const data = await foodApi.getFoodDetail(Number(foodId));
        setFood(data);
      } catch (err: unknown) {
        const errorResponse = err as { response?: { status?: number, data?: { message?: string } } };
        if (errorResponse?.response?.status === 404) {
          setError('Không tìm thấy món ăn này.');
        } else {
          setError(errorResponse?.response?.data?.message || 'Lỗi khi tải thông tin món ăn.');
        }
      } finally {
        setLoading(false);
      }
    };
    fetchFoodDetail();
  }, [foodId]);

  const handleAddToCart = async () => {
    if (!isCustomer || !food || !food.isAvailable) return;
    setAdding(true);
    try {
      await addItem(food.id, 1);
      alert('Đã thêm món vào giỏ hàng!');
    } catch (err) {
      const e = err as Error;
      alert(e.message || 'Lỗi thêm vào giỏ hàng');
    } finally {
      setAdding(false);
    }
  };

  if (loading) return <div className="detail-state">Đang tải thông tin món ăn...</div>;
  if (error) return (
    <div className="detail-state error">
      <p>{error}</p>
      <button onClick={() => navigate(backUrl)} className="btn-back">Quay lại thực đơn</button>
    </div>
  );
  if (!food) return <div className="detail-state">Không có thông tin.</div>;

  return (
    <div className="food-detail-page">
      <button onClick={() => navigate(backUrl)} className="btn-back">← Quay lại thực đơn</button>
      
      <div className="food-detail-container">
        <div className="food-detail-image-wrapper">
          {food.imageUrl ? (
            <img src={food.imageUrl} alt={food.name} className="food-detail-image" />
          ) : (
            <div className="food-detail-no-image">No Image Available</div>
          )}
          {!food.isAvailable && <span className="unavailable-badge">Hết món</span>}
        </div>
        
        <div className="food-detail-info">
          <h1>{food.name}</h1>
          <div className="category-tag">{food.category?.name || 'Uncategorized'}</div>
          <div className="price">{formatCurrency(food.price)}</div>
          
          <div className="description">
            <h3>Mô tả</h3>
            <p>{food.description || 'Chưa có mô tả cho món ăn này.'}</p>
          </div>
          
          {/* Cart logic */}
          <div className="cart-actions">
            {isCustomer ? (
              <button 
                className="btn-add-cart-detail" 
                onClick={handleAddToCart}
                disabled={!food.isAvailable || adding}
              >
                {adding ? 'Đang thêm...' : 'Thêm vào giỏ'}
              </button>
            ) : !isAuthenticated ? (
              <button className="btn-login-to-order-detail" onClick={() => navigate('/login')}>
                Đăng nhập để đặt món
              </button>
            ) : null}
          </div>
        </div>
      </div>
    </div>
  );
};
