import { useState } from 'react';
import { createReview } from '../../services/reviewService';

const ReviewForm = ({ foodId, onSuccess }) => {
  const [rating, setRating] = useState(0);
  const [hovered, setHovered] = useState(0);
  const [comment, setComment] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!rating) { setError('Vui lòng chọn số sao'); return; }
    setLoading(true);
    setError(null);
    try {
      await createReview({ foodId, rating, comment });
      setRating(0);
      setComment('');
      onSuccess?.();
    } catch (err) {
      setError(err.response?.data?.message || 'Gửi đánh giá thất bại');
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="bg-orange-50 rounded-2xl p-5">
      <h3 className="font-semibold text-gray-800 mb-4">Viết đánh giá của bạn</h3>

      {/* Stars */}
      <div className="flex gap-1 mb-4">
        {[1,2,3,4,5].map((s) => (
          <button
            key={s}
            type="button"
            onClick={() => setRating(s)}
            onMouseEnter={() => setHovered(s)}
            onMouseLeave={() => setHovered(0)}
            className="text-3xl transition-transform hover:scale-110"
          >
            <span className={(hovered || rating) >= s ? 'text-yellow-400' : 'text-gray-300'}>★</span>
          </button>
        ))}
      </div>

      <textarea
        value={comment}
        onChange={(e) => setComment(e.target.value)}
        placeholder="Nhận xét của bạn về món ăn này..."
        rows={3}
        className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-orange-400 text-sm resize-none mb-3"
      />

      {error && <p className="text-red-500 text-sm mb-3">{error}</p>}

      <button
        type="submit"
        disabled={loading}
        className="bg-orange-500 hover:bg-orange-600 text-white font-semibold px-5 py-2.5 rounded-xl transition-colors disabled:opacity-60"
      >
        {loading ? 'Đang gửi...' : 'Gửi đánh giá'}
      </button>
    </form>
  );
};

export default ReviewForm;
