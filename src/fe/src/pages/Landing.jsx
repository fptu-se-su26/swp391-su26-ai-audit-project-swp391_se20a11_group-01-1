import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

const DISHES = [
  { img: '🥩', name: 'Lẩu bò nhúng dấm',  price: '280.000đ', cat: 'Lẩu' },
  { img: '🐔', name: 'Gà nướng muối ớt',   price: '220.000đ', cat: 'Nướng' },
  { img: '🦑', name: 'Mực chiên giòn',     price: '180.000đ', cat: 'Chiên' },
  { img: '🥓', name: 'Sườn nướng BBQ',     price: '250.000đ', cat: 'Nướng' },
  { img: '🐟', name: 'Nhậu hải sản mix',   price: '350.000đ', cat: 'Hải sản' },
  { img: '🍺', name: 'Bia & Đồ nhậu set',  price: '150.000đ', cat: 'Đồ uống' },
];

const REVIEWS = [
  { name: 'Nguyễn Văn An',  stars: 5, text: 'Đồ nhậu ngon tuyệt, bò lúc lắc mềm tan, bia lạnh tê tái. Không gian thoáng mát, nhân viên nhiệt tình.' },
  { name: 'Trần Thị Bích',  stars: 5, text: 'Quán nhậu chuẩn vị, sườn nướng BBQ đậm đà. Sẽ dẫn cả nhóm bạn quay lại vào cuối tuần.' },
  { name: 'Lê Minh Khoa',   stars: 5, text: 'Không gian đẹp, đồ ăn ngon, phục vụ nhanh. Giá cả hợp lý, xứng đáng từng đồng bỏ ra.' },
];

export default function Landing() {
  const navigate = useNavigate();
  const [scrolled, setScrolled] = useState(false);
  const [menuOpen, setMenuOpen] = useState(false);

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 60);
    window.addEventListener('scroll', onScroll);
    return () => window.removeEventListener('scroll', onScroll);
  }, []);

  const scrollTo = (id) => { document.getElementById(id)?.scrollIntoView({ behavior: 'smooth' }); setMenuOpen(false); };

  return (
    <div className="bg-[#0d0d1a] text-white">
      {/* Navbar */}
      <nav className={`fixed top-0 left-0 right-0 z-50 transition-all ${scrolled ? 'bg-[#0d0d1a]/95 backdrop-blur shadow-lg' : ''}`}>
        <div className="max-w-6xl mx-auto px-4 h-16 flex items-center justify-between">
          <div className="flex items-center gap-2 cursor-pointer" onClick={() => window.scrollTo({top:0,behavior:'smooth'})}>
            <span className="text-2xl">🍜</span>
            <span className="font-extrabold text-white">Cái Gì Cũng Không Có</span>
          </div>
          <div className="hidden md:flex items-center gap-8">
            {[['about','Về chúng tôi'],['menu-section','Thực đơn'],['reviews','Đánh giá'],['contact','Liên hệ']].map(([id,l]) => (
              <button key={id} onClick={() => scrollTo(id)} className="text-sm text-gray-300 hover:text-[#e85d04] transition-colors">{l}</button>
            ))}
          </div>
          <div className="hidden md:flex items-center gap-3">
            <button onClick={() => navigate('/login')} className="text-sm text-gray-300 hover:text-white border border-gray-600 hover:border-white px-4 py-2 rounded-full transition-colors">Đăng nhập</button>
            <button onClick={() => navigate('/register')} className="text-sm bg-[#e85d04] hover:bg-[#c44d00] text-white font-semibold px-4 py-2 rounded-full transition-colors">Đặt bàn ngay</button>
          </div>
          <button className="md:hidden text-xl" onClick={() => setMenuOpen(!menuOpen)}>{menuOpen ? '✕' : '☰'}</button>
        </div>
        {menuOpen && (
          <div className="md:hidden bg-[#0d0d1a] border-t border-gray-800 px-4 py-4 flex flex-col gap-3">
            {[['about','Về chúng tôi'],['menu-section','Thực đơn'],['reviews','Đánh giá'],['contact','Liên hệ']].map(([id,l]) => (
              <button key={id} onClick={() => scrollTo(id)} className="text-sm text-gray-300 text-left">{l}</button>
            ))}
            <hr className="border-gray-700" />
            <button onClick={() => navigate('/login')} className="text-sm text-gray-300 text-left">Đăng nhập</button>
            <button onClick={() => navigate('/register')} className="text-sm text-[#e85d04] font-semibold text-left">Đặt bàn ngay</button>
          </div>
        )}
      </nav>

      {/* Hero */}
      <section className="min-h-screen flex items-center justify-center relative pt-16">
        <div className="absolute inset-0 bg-gradient-to-b from-[#e85d04]/5 via-transparent to-transparent"></div>
        <div className="absolute inset-0" style={{ backgroundImage: 'radial-gradient(circle at 50% 50%, rgba(232,93,4,0.05) 0%, transparent 70%)' }}></div>
        <div className="text-center px-4 relative">
          <div className="inline-block bg-[#e85d04]/10 border border-[#e85d04]/30 text-[#e85d04] text-sm font-semibold px-4 py-1.5 rounded-full mb-6">
            ✦ Quán Nhậu · Est. 2026 ✦
          </div>
          <h1 className="text-5xl md:text-7xl font-black mb-4 leading-tight">
            Cái Gì<br />
            <span className="text-[#e85d04]">Cũng Không Có</span>
          </h1>
          <p className="text-gray-400 text-lg max-w-xl mx-auto mb-10">
            Trải nghiệm ẩm thực nhậu đích thực với những món ăn ngon nhất,<br />
            không gian thoải mái và đội ngũ phục vụ tận tâm tại TP.HCM.
          </p>
          <div className="flex gap-4 justify-center flex-wrap">
            <button onClick={() => navigate('/register')}
              className="bg-[#e85d04] hover:bg-[#c44d00] text-white font-bold px-8 py-3.5 rounded-full text-sm transition-colors shadow-lg shadow-[#e85d04]/30">
              📅 Đặt bàn ngay
            </button>
            <button onClick={() => scrollTo('menu-section')}
              className="border border-gray-600 hover:border-white text-gray-300 hover:text-white font-bold px-8 py-3.5 rounded-full text-sm transition-colors">
              🍽️ Xem thực đơn
            </button>
          </div>
          <div className="flex items-center justify-center gap-8 mt-12">
            {[['500+','Món ăn'],['4.9★','Đánh giá'],['10K+','Khách hàng']].map(([num,label]) => (
              <div key={label} className="text-center">
                <p className="text-2xl font-extrabold text-white">{num}</p>
                <p className="text-xs text-gray-500">{label}</p>
              </div>
            ))}
          </div>
        </div>
        <button onClick={() => scrollTo('about')} className="absolute bottom-8 left-1/2 -translate-x-1/2 text-gray-500 hover:text-white animate-bounce">↓</button>
      </section>

      {/* About */}
      <section id="about" className="py-20 px-4">
        <div className="max-w-4xl mx-auto text-center">
          <div className="text-[#e85d04] text-sm font-semibold mb-3">✦ Câu chuyện của chúng tôi ✦</div>
          <h2 className="text-4xl font-extrabold mb-4">Nghệ thuật ẩm thực<br /><span className="text-[#e85d04]">nhậu đỉnh cao</span></h2>
          <p className="text-gray-400 mb-12 max-w-2xl mx-auto">
            Được thành lập năm 2026, <strong className="text-white">Cái Gì Cũng Không Có</strong> là điểm đến nhậu uy tín tại TP.HCM.
            Chúng tôi mang đến những món ăn ngon, đồ uống đa dạng và không gian thoải mái nhất.
          </p>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-5">
            {[
              { icon:'👨‍🍳', title:'Đầu bếp kinh nghiệm', desc:'Đội ngũ đầu bếp với 10 năm kinh nghiệm' },
              { icon:'🌿', title:'Nguyên liệu tươi', desc:'Nhập hàng ngày từ các chợ đầu mối lớn' },
              { icon:'🏮', title:'Không gian đẹp', desc:'Thiết kế nội thất độc đáo, thoáng mát' },
              { icon:'🍺', title:'Bia & Đồ uống', desc:'Hơn 50 loại bia và đồ uống đặc sắc' },
            ].map((f,i) => (
              <div key={i} className="bg-white/5 border border-white/10 rounded-2xl p-5 text-center">
                <div className="text-3xl mb-3">{f.icon}</div>
                <h3 className="font-bold text-sm mb-1">{f.title}</h3>
                <p className="text-xs text-gray-400">{f.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Menu preview */}
      <section id="menu-section" className="py-20 px-4 bg-white/2">
        <div className="max-w-5xl mx-auto">
          <div className="text-center mb-12">
            <div className="text-[#e85d04] text-sm font-semibold mb-3">✦ Thực đơn nổi bật ✦</div>
            <h2 className="text-4xl font-extrabold">Những món <span className="text-[#e85d04]">được yêu thích nhất</span></h2>
          </div>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5 mb-10">
            {DISHES.map((d,i) => (
              <div key={i} className="bg-white/5 border border-white/10 rounded-2xl p-5 hover:border-[#e85d04]/50 transition-colors">
                <div className="text-5xl mb-3">{d.img}</div>
                <span className="text-xs text-[#e85d04] font-semibold">{d.cat}</span>
                <h3 className="font-bold text-white mb-2">{d.name}</h3>
                <div className="flex items-center justify-between">
                  <span className="text-[#e85d04] font-extrabold">{d.price}</span>
                  <button onClick={() => navigate('/register')} className="text-xs bg-[#e85d04] hover:bg-[#c44d00] text-white font-semibold px-3 py-1.5 rounded-full">Đặt ngay</button>
                </div>
              </div>
            ))}
          </div>
          <div className="text-center">
            <button onClick={() => navigate('/register')} className="bg-[#e85d04] hover:bg-[#c44d00] text-white font-bold px-8 py-3 rounded-full text-sm">
              Xem toàn bộ thực đơn →
            </button>
          </div>
        </div>
      </section>

      {/* Reviews */}
      <section id="reviews" className="py-20 px-4">
        <div className="max-w-4xl mx-auto">
          <div className="text-center mb-12">
            <div className="text-[#e85d04] text-sm font-semibold mb-3">✦ Khách hàng nói gì ✦</div>
            <h2 className="text-4xl font-extrabold">Đánh giá <span className="text-[#e85d04]">từ thực khách</span></h2>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
            {REVIEWS.map((r,i) => (
              <div key={i} className="bg-white/5 border border-white/10 rounded-2xl p-5">
                <p className="text-yellow-400 text-lg mb-3">{'★'.repeat(r.stars)}</p>
                <p className="text-gray-300 text-sm italic mb-4">"{r.text}"</p>
                <div className="flex items-center gap-2">
                  <div className="w-8 h-8 rounded-full bg-[#e85d04] flex items-center justify-center font-bold text-xs">{r.name.charAt(0)}</div>
                  <span className="text-sm font-semibold">{r.name}</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="py-20 px-4">
        <div className="max-w-2xl mx-auto text-center">
          <h2 className="text-4xl font-extrabold mb-4">Sẵn sàng trải nghiệm?</h2>
          <p className="text-gray-400 mb-8">Đặt bàn ngay hôm nay và nhận ưu đãi đặc biệt cho lần đầu tiên</p>
          <div className="flex gap-4 justify-center">
            <button onClick={() => navigate('/register')} className="bg-[#e85d04] hover:bg-[#c44d00] text-white font-bold px-8 py-3.5 rounded-full text-sm">📅 Đặt bàn ngay</button>
            <button onClick={() => navigate('/login')} className="border border-gray-600 hover:border-white text-gray-300 hover:text-white font-bold px-8 py-3.5 rounded-full text-sm">🔑 Đăng nhập</button>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer id="contact" className="border-t border-white/10 py-12 px-4">
        <div className="max-w-5xl mx-auto grid grid-cols-1 md:grid-cols-3 gap-8 mb-8">
          <div>
            <div className="flex items-center gap-2 mb-3">
              <span className="text-2xl">🍜</span>
              <span className="font-extrabold">Cái Gì Cũng Không Có</span>
            </div>
            <p className="text-gray-400 text-sm">Trải nghiệm nhậu đỉnh cao tại TP.HCM</p>
          </div>
          <div>
            <h4 className="font-bold mb-3">Thông tin</h4>
            <div className="space-y-2 text-sm text-gray-400">
              <p>📍 Ngũ Hành Sơn, TP. Đà Nẵng</p>
              <p>📞 028-xxxx-xxxx</p>
              <p>✉️ hello@cgkc.vn</p>
              <p>🕐 10:00 – 24:00 hàng ngày</p>
            </div>
          </div>
          <div>
            <h4 className="font-bold mb-3">Truy cập nhanh</h4>
            <div className="space-y-2">
              {[['Đăng nhập','/login'],['Đăng ký','/register']].map(([l,p]) => (
                <button key={p} onClick={() => navigate(p)} className="block text-sm text-gray-400 hover:text-[#e85d04]">{l}</button>
              ))}
            </div>
          </div>
        </div>
        <div className="border-t border-white/10 pt-6 flex flex-col md:flex-row items-center justify-between text-xs text-gray-500 gap-2">
          <span>© 2026 Cái Gì Cũng Không Có. All rights reserved.</span>
          <span>Made with ❤️ in TP.Đà Nẵng</span>
        </div>
      </footer>
    </div>
  );
}
