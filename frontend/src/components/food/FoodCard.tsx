import React from 'react';
import { Link } from 'react-router-dom';
import type { FoodResponse } from '../../types/food';
import { formatCurrency } from '../../utils/formatCurrency';
import { useAuth } from '../../hooks/useAuth';
import { useCart } from '../../hooks/useCart';
import './FoodCard.css';

interface FoodCardProps {
  food: FoodResponse;
  baseUrl: string;
}

export const FoodCard: React.FC<FoodCardProps> = ({ food, baseUrl }) => {
  const { isAuthenticated, user } = useAuth();
  const { addItem } = useCart();
  const isCustomer = isAuthenticated && user?.roles?.includes('ROLE_CUSTOMER');

  const [adding, setAdding] = React.useState(false);

  const handleAddToCart = async () => {
    if (!isCustomer || !food.isAvailable) return;
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

  return (
    <div className={`food-card ${!food.isAvailable ? 'unavailable' : ''}`}>
      <div className="food-image-container">
        {food.imageUrl ? (
          <img src={food.imageUrl} alt={food.name} className="food-image" />
        ) : (
          <div className="food-image-placeholder">No Image Available</div>
        )}
        {!food.isAvailable && <div className="food-unavailable-badge">Hết món</div>}
      </div>
      <div className="food-info">
        <h3 className="food-name">{food.name}</h3>
        <p className="food-category">{food.categoryName || 'Uncategorized'}</p>
        <p className="food-price">{formatCurrency(food.price)}</p>
        <div className="food-actions">
          <Link to={`${baseUrl}/${food.id}`} className="btn-detail">Xem chi tiết</Link>
          {isCustomer ? (
            <button 
              className="btn-add-cart" 
              onClick={handleAddToCart}
              disabled={!food.isAvailable || adding}
            >
              {adding ? 'Đang thêm...' : 'Thêm vào giỏ'}
            </button>
          ) : !isAuthenticated ? (
            <Link to="/login" className="btn-login-to-order">Đăng nhập để đặt món</Link>
          ) : null}
        </div>
      </div>
    </div>
  );
};
