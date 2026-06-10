import { Link } from 'react-router-dom';
import { formatPrice } from '../../utils/helpers';

const FoodCard = ({ food }) => (
  <Link
    to={`/food/${food?.id}`}
    className="group bg-white rounded-2xl shadow-sm hover:shadow-md overflow-hidden transition-shadow duration-300 flex flex-col"
  >
    <div className="relative overflow-hidden h-48 bg-gray-100">
      <img
        src={food?.image || 'https://placehold.co/400x300?text=Food'}
        alt={food?.name}
        className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
      />
      {food?.categoryName && (
        <span className="absolute top-3 left-3 bg-orange-500 text-white text-xs font-semibold px-2 py-1 rounded-full">
          {food.categoryName}
        </span>
      )}
      {food?.status === 'UNAVAILABLE' && (
        <div className="absolute inset-0 bg-black/40 flex items-center justify-center">
          <span className="text-white font-semibold text-sm bg-black/60 px-3 py-1 rounded-full">Hết món</span>
        </div>
      )}
    </div>
    <div className="p-4 flex flex-col flex-1">
      <h3 className="font-semibold text-gray-800 text-base mb-1 line-clamp-1 group-hover:text-orange-500 transition-colors">
        {food?.name}
      </h3>
      <p className="text-sm text-gray-500 line-clamp-2 flex-1 mb-3">{food?.description}</p>
      <div className="flex items-center justify-between mt-auto">
        <span className="text-orange-500 font-bold text-lg">{formatPrice(food?.price)}</span>
        <div className="flex items-center gap-1 text-sm text-gray-400">
          <span className="text-yellow-400">★</span>
          <span>{food?.rating?.toFixed(1) || '—'}</span>
        </div>
      </div>
    </div>
  </Link>
);

export default FoodCard;
