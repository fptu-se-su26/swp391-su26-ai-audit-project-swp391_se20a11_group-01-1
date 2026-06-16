import { useEffect, useState } from 'react';
import { getAllReviews } from '../../services/reviewService';
import { formatDate } from '../../utils/helpers';

const STARS = [5, 4, 3, 2, 1];

function StarDisplay({ value, size = 'base' }) {
  return (
    <span className={`text-${size}`}>
      {[1,2,3,4,5].map(n => (
        <span key={n} style={{ color: n <= value ? '#f6ad55' : '#e2e8f0' }}>★</span>
      ))}
    </span>
  );
}

export default function AdminFeedback() {
  const [reviews, setReviews] = useState([]);
  const [filter, setFilter] = useState('all');
  const [starFilter, setStarFilter] = useState(0);
  const [search, setSearch] = useState('');
  const [replyingId, setReplyingId] = useState(null);
  const [replyText, setReplyText] = useState('');
  const [replies, setReplies] = useState({});

  useEffect(() => {
    getAllReviews().then(({ data }) => setReviews(data)).catch(() => {});
  }, []);

  const filtered = reviews.filter(r => {
    if (starFilter > 0 && r.rating !== starFilter) return false;
    if (search && !r.userFullName?.toLowerCase().includes(search.toLowerCase()) &&
        !r.comment?.toLowerCase().includes(search.toLowerCase())) return false;
    if (filter === 'pending' && replies[r.id]) return false;
    if (filter === 'replied' && !replies[r.id]) return false;
    return true;
  });

  const stats = {
    total:   reviews.length,
    pending: reviews.filter(r => !replies[r.id]).length,
    replied: reviews.filter(r => !!replies[r.id]).length,
    avg:     reviews.length ? (reviews.reduce((s, r) => s + r.rating, 0) / reviews.length).toFixed(1) : 0,
  };

  const handleReply = (id) => {
    if (!replyText.trim()) return;
    setReplies(prev => ({ ...prev, [id]: replyText.trim() }));
    setReplyingId(null);
    setReplyText('');
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-800">Quản lý phản hồi</h1>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 mb-5">
        {[
          { num: stats.total,   label: 'Tổng đánh giá',  color: '#e85d04' },
          { num: `⭐ ${stats.avg}`, label: 'Điểm trung bình', color: '#d69e2e' },
          { num: stats.pending, label: 'Chờ phản hồi',   color: '#e85d04' },
          { num: stats.replied, label: 'Đã phản hồi',    color: '#38a169' },
        ].map((s, i) => (
          <div key={i} className="bg-white rounded-xl p-5 shadow-sm text-center">
            <p className="text-2xl font-extrabold" style={{ color: s.color }}>{s.num}</p>
            <p className="text-xs text-gray-500 mt-1">{s.label}</p>
          </div>
        ))}
      </div>

      {/* Toolbar */}
      <div className="flex flex-wrap gap-3 mb-4">
        <input className="px-4 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-[#e85d04] bg-white min-w-64"
          placeholder="🔍 Tìm theo tên hoặc nội dung..." value={search} onChange={e => setSearch(e.target.value)} />
        <div className="flex gap-2 flex-wrap">
          {[['all','Tất cả'],['pending','Chờ phản hồi'],['replied','Đã phản hồi']].map(([v, l]) => (
            <button key={v} onClick={() => setFilter(v)}
              className={`px-3 py-2 rounded-full text-xs font-semibold border transition-colors ${filter === v ? 'bg-[#e85d04] text-white border-[#e85d04]' : 'bg-white text-gray-600 border-gray-200 hover:border-[#e85d04]'}`}>
              {l}
            </button>
          ))}
          <button onClick={() => setStarFilter(0)}
            className={`px-3 py-2 rounded-full text-xs font-semibold border transition-colors ${starFilter === 0 ? 'bg-[#e85d04] text-white border-[#e85d04]' : 'bg-white text-gray-600 border-gray-200 hover:border-[#e85d04]'}`}>
            Tất cả sao
          </button>
          {STARS.map(s => (
            <button key={s} onClick={() => setStarFilter(s)}
              className={`px-3 py-2 rounded-full text-xs font-semibold border transition-colors ${starFilter === s ? 'bg-[#e85d04] text-white border-[#e85d04]' : 'bg-white text-gray-600 border-gray-200 hover:border-[#e85d04]'}`}>
              {'★'.repeat(s)}
            </button>
          ))}
        </div>
      </div>

      {/* List */}
      <div className="space-y-4">
        {filtered.length === 0 && (
          <div className="text-center py-12 text-gray-400 bg-white rounded-2xl">
            <p className="text-4xl mb-2">💬</p><p>Không có đánh giá nào</p>
          </div>
        )}
        {filtered.map(fb => (
          <div key={fb.id} className={`bg-white rounded-2xl p-5 shadow-sm ${!replies[fb.id] ? 'border-l-4 border-[#e85d04]' : ''}`}>
            {/* Header */}
            <div className="flex items-start justify-between mb-3">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-full bg-orange-100 text-orange-600 flex items-center justify-center font-bold shrink-0">
                  {fb.userFullName?.charAt(0) || 'U'}
                </div>
                <div>
                  <p className="font-semibold text-gray-800">{fb.userFullName}</p>
                  <p className="text-xs text-gray-400">{formatDate(fb.createdAt)}</p>
                </div>
              </div>
              <div className="flex items-center gap-3">
                <StarDisplay value={fb.rating} />
                <span className={`text-xs font-semibold px-2.5 py-1 rounded-full ${replies[fb.id] ? 'bg-green-100 text-green-600' : 'bg-yellow-100 text-yellow-700'}`}>
                  {replies[fb.id] ? '✅ Đã phản hồi' : '⏳ Chờ phản hồi'}
                </span>
              </div>
            </div>

            {fb.foodName && <p className="text-xs text-[#e85d04] font-medium mb-2">🍴 {fb.foodName}</p>}
            <p className="text-sm text-gray-600 leading-relaxed mb-3">"{fb.comment}"</p>

            {/* Reply */}
            {replies[fb.id] && (
              <div className="bg-green-50 border border-green-200 rounded-xl px-4 py-3 mb-3">
                <p className="text-xs font-semibold text-green-700 mb-1">💬 Phản hồi từ nhà hàng:</p>
                <p className="text-sm text-gray-700">{replies[fb.id]}</p>
              </div>
            )}

            {replyingId === fb.id ? (
              <div className="space-y-2">
                <textarea rows={3} autoFocus
                  className="w-full px-3 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-[#e85d04] resize-none"
                  placeholder="Nhập phản hồi..."
                  value={replyText} onChange={e => setReplyText(e.target.value)} />
                <div className="flex gap-2">
                  <button onClick={() => handleReply(fb.id)} disabled={!replyText.trim()}
                    className="bg-[#e85d04] hover:bg-[#c44d00] disabled:opacity-50 text-white text-sm font-semibold px-4 py-2 rounded-xl">
                    📤 Gửi phản hồi
                  </button>
                  <button onClick={() => { setReplyingId(null); setReplyText(''); }}
                    className="bg-gray-100 hover:bg-gray-200 text-gray-700 text-sm font-semibold px-4 py-2 rounded-xl">
                    Hủy
                  </button>
                </div>
              </div>
            ) : (
              <button onClick={() => { setReplyingId(fb.id); setReplyText(replies[fb.id] || ''); }}
                className="text-sm text-[#e85d04] hover:text-[#c44d00] font-semibold">
                {replies[fb.id] ? '✏️ Sửa phản hồi' : '💬 Phản hồi'}
              </button>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}
