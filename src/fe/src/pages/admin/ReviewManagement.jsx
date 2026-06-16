import { useEffect, useState } from 'react';
import { getAllReviews } from '../../services/reviewService';
import SearchBar from '../../components/common/SearchBar';
import Loading from '../../components/common/Loading';
import { formatDate } from '../../utils/helpers';

const ReviewManagement = () => {
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');

  useEffect(() => {
    setLoading(true);
    getAllReviews().then(({ data }) => setReviews(data)).catch(() => {}).finally(() => setLoading(false));
  }, []);

  const filtered = reviews.filter((r) =>
    r.userName?.toLowerCase().includes(search.toLowerCase()) ||
    r.foodName?.toLowerCase().includes(search.toLowerCase())
  );

  if (loading) return <Loading />;

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h2 className="text-2xl font-bold text-gray-800">Quản lý Đánh giá</h2>
          <p className="text-sm text-gray-500 mt-1">{reviews.length} đánh giá</p>
        </div>
      </div>

      <div className="mb-5 max-w-sm">
        <SearchBar value={search} onChange={setSearch} placeholder="Tìm theo user hoặc món ăn..." />
      </div>

      <div className="bg-white rounded-2xl shadow-sm overflow-hidden">
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-gray-50 text-left text-gray-500 text-xs uppercase tracking-wider">
              <th className="px-5 py-4 font-semibold">Người dùng</th>
              <th className="px-5 py-4 font-semibold">Món ăn</th>
              <th className="px-5 py-4 font-semibold">Sao</th>
              <th className="px-5 py-4 font-semibold">Nội dung</th>
              <th className="px-5 py-4 font-semibold">Ngày</th>
              <th className="px-5 py-4 font-semibold text-right"></th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-50">
            {filtered.map((r) => (
              <tr key={r.id} className="hover:bg-gray-50 transition-colors">
                <td className="px-5 py-4 font-medium text-gray-800">{r.userName}</td>
                <td className="px-5 py-4 text-orange-500 font-medium">{r.foodName}</td>
                <td className="px-5 py-4 text-yellow-400 font-semibold">{'★'.repeat(r.rating)}</td>
                <td className="px-5 py-4 text-gray-500 max-w-xs truncate">{r.comment}</td>
                <td className="px-5 py-4 text-gray-400">{formatDate(r.createdAt)}</td>
                <td></td>
              </tr>
            ))}
          </tbody>
        </table>
        {filtered.length === 0 && (
          <div className="text-center py-12 text-gray-400"><p className="text-4xl mb-2">⭐</p><p>Không tìm thấy đánh giá</p></div>
        )}
      </div>
    </div>
  );
};

export default ReviewManagement;
