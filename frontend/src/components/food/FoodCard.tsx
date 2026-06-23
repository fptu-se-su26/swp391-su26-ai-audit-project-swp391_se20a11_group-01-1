import React from 'react';
import { Link } from 'react-router-dom';
import type { FoodResponse } from '../../types/food';
import { formatCurrency } from '../../utils/formatCurrency';
import './FoodCard.css';

interface FoodCardProps {
  food: FoodResponse;
  baseUrl: string;
}

export const FoodCard: React.FC<FoodCardProps> = ({ food, baseUrl }) => {
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
        </div>
      </div>
    </div>
  );
};
