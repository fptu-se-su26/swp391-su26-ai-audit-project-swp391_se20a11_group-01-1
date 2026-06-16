import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import FoodList from '../../components/food/FoodList';
import { getAllFoods } from '../../services/foodService';

const Home = () => {
  const [foods, setFoods] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getAllFoods()
      .then(({ data }) => setFoods(data.slice(0, 8)))
      .catch(() => setFoods([]))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div>
      {/* Hero */}
      <section className="bg-gradient-to-br from-orange-500 to-amber-400 text-white">
        <div className="max-w-6xl mx-auto px-4 py-20 flex flex-col md:flex-row items-center gap-10">
          <div className="flex-1">
            <h1 className="text-4xl md:text-5xl font-extrabold leading-tight mb-4">Món ngon<br />giao tận nơi 🚀</h1>
            <p className="text-orange-100 text-lg mb-8 max-w-md">Hàng trăm món ăn đặc sắc, đặt hàng nhanh chóng và nhận ngay tại nhà bạn.</p>
            <div className="flex gap-3 flex-wrap">
              <Link to="/menu" className="bg-white text-orange-500 font-bold px-6 py-3 rounded-full hover:bg-orange-50 transition-colors shadow-md">Xem thực đơn</Link>
              <Link to="/register" className="border-2 border-white text-white font-bold px-6 py-3 rounded-full hover:bg-white hover:text-orange-500 transition-colors">Đăng ký miễn phí</Link>
            </div>
          </div>
          <div className="text-[120px] select-none hidden md:block">🍜</div>
        </div>
      </section>

      {/* Features */}
      <section className="max-w-6xl mx-auto px-4 py-16">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {[
            { icon: '⚡', title: 'Giao hàng nhanh', desc: 'Nhận đơn trong vòng 30 phút tại khu vực nội thành.' },
            { icon: '🍽️', title: 'Đa dạng món ăn', desc: 'Hơn 500 món ăn từ các nhà hàng uy tín được tuyển chọn.' },
            { icon: '⭐', title: 'Đánh giá thực', desc: 'Nhận xét từ khách hàng thực tế, minh bạch và tin cậy.' },
          ].map((f) => (
            <div key={f.title} className="bg-white rounded-2xl p-6 shadow-sm text-center">
              <div className="text-4xl mb-3">{f.icon}</div>
              <h3 className="font-bold text-gray-800 mb-2">{f.title}</h3>
              <p className="text-sm text-gray-500">{f.desc}</p>
            </div>
          ))}
        </div>
      </section>

      {/* Popular foods */}
      <section className="max-w-6xl mx-auto px-4 pb-20">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-2xl font-bold text-gray-800">Món phổ biến</h2>
          <Link to="/menu" className="text-sm text-orange-500 font-semibold hover:underline">Xem tất cả →</Link>
        </div>
        <FoodList foods={foods} loading={loading} />
      </section>
    </div>
  );
};

export default Home;
