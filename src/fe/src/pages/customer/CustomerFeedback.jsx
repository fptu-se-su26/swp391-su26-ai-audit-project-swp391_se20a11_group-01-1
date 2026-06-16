import { useEffect, useState } from 'react';
import { createReview, getAllReviews } from '../../services/reviewService';
import { getAllFoods } from '../../services/foodService';
import { useAuth } from '../../context/AuthContext';

function StarRating({ value, onChange, size = 'text-2xl' }) {
  const [hover, setHover] = useState(0);
  return (
    <div className={`flex gap-1 ${size}`}>
      {[1,2,3,4,5].map(n => (
        <button key={n} type="button"
          onMouseEnter={() => onChange && setHover(n)}
          onMouseLeave={() => onChange && setHover(0)}
          onClick={() => onChange && onChange(n)}
          style={{ color: n <= (hover || value) ? '#f6ad55' : '#e2e8f0' }}>★</button>
      ))}
    </div>
  );
}

export default function CustomerFeedback() {
  const { user } = useAuth();
  const [foods, setFoods] = useState([]);
  const [myReviews, setMyReviews] = useState([]);
  const [submitted, setSubmitted] = useState(false);
  const [loading, setLoading] = useState(false);
  const [form, setForm] = useState({ foodId: '', rating: 0, comment: '' });

  useEffect(() => {
    getAllFoods().then(({ data }) => setFoods(data)).catch(() => {});
    getAllReviews().then(({ data }) => setMyReviews(data.filter(r => r.userFullName === user?.fullName))).catch(() => {});
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.rating) { alert('Vui lòng chọn số sao'); return; }
    setLoading(true);
    try {
      await createReview({ foodId: Number(form.foodId), rating: form.rating, comment: form.comment });
      setSubmitted(true);
      const { data } = await getAllReviews();
      setMyReviews(data.filter(r => r.userFullName === user?.fullName));
    } catch (err) {
      alert(err.response?.data?.message || 'Gửi đánh giá thất bại');
    } finally { setLoading(false); }
  };

  return (
    <div className="max-w-4xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold text-gray-800 mb-6">Đánh giá & Phản hồi</h1>
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Form */}
        <div className="bg-white rounded-2xl p-6 shadow-sm">
          {submitted ? (
            <div className="text-center py-8">
              <div className="text-6xl mb-4">🙏</div>
              <h2 className="text-xl font-bold text-gray-800 mb-2">Cảm ơn bạn!</h2>
              <p className="text-gray-500 mb-5">Phản hồi của bạn giúp chúng tôi cải thiện dịch vụ.</p>
              <button onClick={() => { setSubmitted(false); setForm({ foodId:'', rating:0, comment:'' }); }}
                className="bg-[#e85d04] hover:bg-[#c44d00] text-white font-bold px-5 py-2.5 rounded-xl text-sm">
                Gửi đánh giá khác
              </button>
            </div>
          ) : (
            <>
              <h2 className="font-bold text-gray-700 mb-4">✍️ Gửi đánh giá</h2>
              <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-1.5">Chọn món ăn</label>
                  <select value={form.foodId} onChange={e => setForm({...form, foodId: e.target.value})} required
                    className="w-full px-3 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-[#e85d04]">
                    <option value="">-- Chọn món --</option>
                    {foods.map(f => <option key={f.id} value={f.id}>{f.name}</option>)}
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-2">Đánh giá tổng thể</label>
                  <StarRating value={form.rating} onChange={v => setForm({...form, rating: v})} size="text-4xl" />
                  <p className="text-sm text-gray-400 mt-1">{['','Tệ','Không tốt','Bình thường','Tốt','Xuất sắc'][form.rating] || 'Chưa đánh giá'}</p>
                </div>
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-1.5">Nhận xét</label>
                  <textarea rows={4} value={form.comment} onChange={e => setForm({...form, comment: e.target.value})}
                    placeholder="Chia sẻ trải nghiệm của bạn..." required
                    className="w-full px-3 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-[#e85d04] resize-none" />
                </div>
                <button type="submit" disabled={loading || !form.rating}
                  className="w-full bg-[#e85d04] hover:bg-[#c44d00] disabled:opacity-50 text-white font-bold py-3 rounded-xl text-sm flex items-center justify-center gap-2">
                  {loading ? <span className="spinner"></span> : '📤 Gửi đánh giá'}
                </button>
              </form>
            </>
          )}
        </div>

        {/* My reviews */}
        <div>
          <h2 className="font-bold text-gray-700 mb-4">📋 Đánh giá của tôi</h2>
          {myReviews.length === 0 ? (
            <div className="bg-white rounded-2xl p-6 shadow-sm text-center text-gray-400">Bạn chưa có đánh giá nào</div>
          ) : (
            <div className="space-y-3">
              {myReviews.map(r => (
                <div key={r.id} className="bg-white rounded-2xl p-4 shadow-sm">
                  <div className="flex items-center justify-between mb-1">
                    <StarRating value={r.rating} size="text-sm" />
                    <span className="text-xs text-gray-400">{new Date(r.createdAt).toLocaleDateString('vi-VN')}</span>
                  </div>
                  {r.foodName && <p className="text-xs text-[#e85d04] font-semibold mb-1">🍴 {r.foodName}</p>}
                  <p className="text-sm text-gray-600">"{r.comment}"</p>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
