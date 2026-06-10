import { useEffect, useState } from 'react';
import ReviewCard from '../../components/review/ReviewCard';
import Loading from '../../components/common/Loading';
import { getAllReviews } from '../../services/reviewService';

const Reviews = () => {
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filterRating, setFilterRating] = useState(0);

  useEffect(() => {
    getAllReviews()
      .then(({ data }) => setReviews(data))
      .catch(() => setReviews([]))
      .finally(() => setLoading(false));
  }, []);

  const filtered = filterRating === 0 ? reviews : reviews.filter((r) => r.rating === filterRating);
  const avgRating = reviews.length ? (reviews.reduce((s, r) => s + r.rating, 0) / reviews.length).toFixed(1) : '—';

  if (loading) return <Loading />;

  return (
    <div className="max-w-5xl mx-auto px-4 py-10">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-800 mb-2">Đánh giá của khách hàng</h1>
        <p className="text-gray-500">Những nhận xét thực tế từ khách hàng đã trải nghiệm.</p>
      </div>

      {/* Stats */}
      <div className="bg-orange-50 border border-orange-100 rounded-2xl p-6 flex flex-col md:flex-row items-center gap-6 mb-8">
        <div className="text-center">
          <div className="text-5xl font-extrabold text-orange-500">{avgRating}</div>
          <div className="flex justify-center mt-1">
            {[1,2,3,4,5].map((s) => <span key={s} className="text-lg text-yellow-400">★</span>)}
          </div>
          <p className="text-sm text-gray-500 mt-1">{reviews.length} đánh giá</p>
        </div>
        <div className="flex-1 w-full">
          {[5,4,3,2,1].map((star) => {
            const count = reviews.filter((r) => r.rating === star).length;
            const percent = reviews.length ? Math.round((count / reviews.length) * 100) : 0;
            return (
              <div key={star} className="flex items-center gap-3 mb-1">
                <span className="text-sm text-gray-500 w-6">{star}★</span>
                <div className="flex-1 bg-gray-200 rounded-full h-2">
                  <div className="bg-yellow-400 h-2 rounded-full transition-all" style={{ width: `${percent}%` }} />
                </div>
                <span className="text-sm text-gray-400 w-8">{count}</span>
              </div>
            );
          })}
        </div>
      </div>

      {/* Filter */}
      <div className="flex items-center gap-2 mb-6 flex-wrap">
        <span className="text-sm text-gray-500">Lọc theo:</span>
        {[0,5,4,3,2,1].map((r) => (
          <button key={r} onClick={() => setFilterRating(r)}
            className={`px-3 py-1.5 rounded-full text-sm font-medium transition-colors ${
              filterRating === r ? 'bg-orange-500 text-white' : 'bg-white border border-gray-200 text-gray-600 hover:border-orange-300'
            }`}>
            {r === 0 ? 'Tất cả' : `${r} ★`}
          </button>
        ))}
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {filtered.map((r) => <ReviewCard key={r.id} review={r} />)}
      </div>
    </div>
  );
};

export default Reviews;
