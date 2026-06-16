import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import Loading from '../../components/common/Loading';
import ReviewCard from '../../components/review/ReviewCard';
import ReviewForm from '../../components/review/ReviewForm';
import { getFoodById } from '../../services/foodService';
import { getAllReviews } from '../../services/reviewService';
import { formatPrice } from '../../utils/helpers';
import { useAuth } from '../../context/AuthContext';

const StarRating = ({ rating }) => (
  <div className="flex gap-0.5">
    {[1,2,3,4,5].map((s) => (
      <span key={s} className={`text-xl ${s <= Math.round(rating) ? 'text-yellow-400' : 'text-gray-200'}`}>★</span>
    ))}
  </div>
);

const FoodDetail = () => {
  const { id } = useParams();
  const { isAuthenticated } = useAuth();
  const [food, setFood] = useState(null);
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(true);

  const loadReviews = () => {
    getAllReviews().then(({ data }) => setReviews(data.filter(r => r.foodId === Number(id)))).catch(() => {});
  };

  useEffect(() => {
    setLoading(true);
    Promise.all([getFoodById(id), getAllReviews()])
      .then(([foodRes, reviewRes]) => {
        setFood(foodRes.data);
        setReviews(reviewRes.data.filter(r => r.foodId === Number(id)));
      })
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) return <Loading />;
  if (!food) return <div className="text-center py-20 text-gray-400">Không tìm thấy món ăn</div>;

  return (
    <div className="max-w-5xl mx-auto px-4 py-10">
      <nav className="text-sm text-gray-400 mb-6">
        <Link to="/" className="hover:text-orange-500">Trang chủ</Link>
        <span className="mx-2">/</span>
        <Link to="/menu" className="hover:text-orange-500">Thực đơn</Link>
        <span className="mx-2">/</span>
        <span className="text-gray-700">{food.name}</span>
      </nav>

      <div className="bg-white rounded-3xl shadow-sm overflow-hidden mb-10">
        <div className="md:flex">
          <div className="md:w-1/2">
            <img src={food.imageUrl || 'https://placehold.co/800x500?text=Food'} alt={food.name} className="w-full h-72 md:h-full object-cover" />
          </div>
          <div className="md:w-1/2 p-8 flex flex-col justify-between">
            <div>
              {food.categoryName && (
                <span className="inline-block bg-orange-100 text-orange-600 text-xs font-semibold px-3 py-1 rounded-full mb-3">{food.categoryName}</span>
              )}
              <h1 className="text-3xl font-bold text-gray-800 mb-3">{food.name}</h1>
              <div className="flex items-center gap-3 mb-4">
                <StarRating rating={food.rating} />
                <span className="text-sm text-gray-500">{food.rating?.toFixed(1)} ({food.totalReviews || reviews.length} đánh giá)</span>
              </div>
              <p className="text-gray-600 text-sm leading-relaxed mb-6">{food.description}</p>
            </div>
            <div className="flex items-center justify-between">
              <span className="text-3xl font-extrabold text-orange-500">{formatPrice(food.price)}</span>
              <span className={`text-sm font-semibold px-3 py-1 rounded-full ${food.status === 'AVAILABLE' ? 'bg-green-100 text-green-600' : 'bg-red-100 text-red-400'}`}>
                {food.status === 'AVAILABLE' ? 'Còn món' : 'Hết món'}
              </span>
            </div>
          </div>
        </div>
      </div>

      {/* Reviews */}
      <div>
        <h2 className="text-2xl font-bold text-gray-800 mb-6">Đánh giá ({reviews.length})</h2>
        {isAuthenticated && (
          <div className="mb-6">
            <ReviewForm foodId={id} onSuccess={loadReviews} />
          </div>
        )}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {reviews.map((r) => <ReviewCard key={r.id} review={r} />)}
        </div>
      </div>
    </div>
  );
};

export default FoodDetail;
