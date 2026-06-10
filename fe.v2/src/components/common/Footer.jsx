import { Link } from 'react-router-dom';

const Footer = () => (
  <footer className="bg-gray-900 text-gray-300 mt-auto">
    <div className="max-w-6xl mx-auto px-4 py-12">
      <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
        <div>
          <div className="flex items-center gap-2 mb-3">
            <span className="text-2xl">🍜</span>
            <span className="text-xl font-bold text-white">FoodApp</span>
          </div>
          <p className="text-sm text-gray-400 leading-relaxed">Khám phá hàng trăm món ăn ngon, đặt hàng nhanh chóng và nhận ngay tại nhà.</p>
        </div>
        <div>
          <h4 className="text-white font-semibold mb-3">Khám phá</h4>
          <ul className="space-y-2 text-sm">
            <li><Link to="/" className="hover:text-orange-400 transition-colors">Trang chủ</Link></li>
            <li><Link to="/menu" className="hover:text-orange-400 transition-colors">Thực đơn</Link></li>
            <li><Link to="/reviews" className="hover:text-orange-400 transition-colors">Đánh giá</Link></li>
          </ul>
        </div>
        <div>
          <h4 className="text-white font-semibold mb-3">Liên hệ</h4>
          <ul className="space-y-2 text-sm">
            <li>📧 support@foodapp.vn</li>
            <li>📞 0909 123 456</li>
            <li>📍 TP. Hồ Chí Minh, Việt Nam</li>
          </ul>
        </div>
      </div>
      <div className="border-t border-gray-700 mt-10 pt-6 text-center text-sm text-gray-500">
        © {new Date().getFullYear()} FoodApp. All rights reserved.
      </div>
    </div>
  </footer>
);

export default Footer;
