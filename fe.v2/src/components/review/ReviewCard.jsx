import { getInitials, formatDate } from '../../utils/helpers';

const StarRating = ({ rating }) => (
  <div className="flex gap-0.5">
    {[1,2,3,4,5].map((s) => (
      <span key={s} className={`text-base ${s <= rating ? 'text-yellow-400' : 'text-gray-200'}`}>★</span>
    ))}
  </div>
);

const ReviewCard = ({ review }) => (
  <div className="bg-white rounded-2xl p-5 shadow-sm hover:shadow-md transition-shadow duration-300">
    <div className="flex items-center gap-3 mb-3">
      <div className="w-10 h-10 rounded-full bg-orange-100 text-orange-600 flex items-center justify-center font-bold text-sm shrink-0">
        {getInitials(review?.userName)}
      </div>
      <div>
        <p className="font-semibold text-gray-800 text-sm">{review?.userName || 'Ẩn danh'}</p>
        <p className="text-xs text-gray-400">{formatDate(review?.createdAt)}</p>
      </div>
      <div className="ml-auto"><StarRating rating={review?.rating || 0} /></div>
    </div>
    {review?.foodName && (
      <p className="text-xs text-orange-500 font-medium mb-2">🍴 {review.foodName}</p>
    )}
    <p className="text-sm text-gray-600 leading-relaxed line-clamp-3">{review?.comment || 'Không có nhận xét.'}</p>
  </div>
);

export default ReviewCard;
