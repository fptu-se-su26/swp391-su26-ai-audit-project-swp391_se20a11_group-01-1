import { useEffect, useState } from 'react';
import { getAllOrders, updateOrderStatus } from '../../services/orderService';
import { getAllFoods } from '../../services/foodService';
import { getAllTables } from '../../services/tableService';
import { createOrder } from '../../services/orderService';
import { formatPrice, formatDate } from '../../utils/helpers';

const STATUS_FLOW  = { PENDING: 'CONFIRMED', CONFIRMED: 'PREPARING', PREPARING: 'READY', READY: 'COMPLETED' };
const STATUS_MAP   = { PENDING:'Chờ xác nhận', CONFIRMED:'Đã xác nhận', PREPARING:'Đang chuẩn bị', READY:'Sẵn sàng', DELIVERING:'Đang giao', COMPLETED:'Hoàn thành', CANCELLED:'Đã hủy' };
const STATUS_COLOR = { PENDING:'bg-yellow-100 text-yellow-700', CONFIRMED:'bg-blue-100 text-blue-600', PREPARING:'bg-orange-100 text-orange-600', READY:'bg-green-100 text-green-600', COMPLETED:'bg-green-200 text-green-700', CANCELLED:'bg-red-100 text-red-500' };
const NEXT_LABEL   = { PENDING:'✓ Xác nhận', CONFIRMED:'✓ Vào bếp', PREPARING:'✓ Sẵn sàng', READY:'✓ Hoàn thành' };

function InvoiceModal({ order, onClose }) {
  const serviceFee = Math.round(Number(order.totalAmount || 0) * 0.05);
  const grand = Number(order.finalAmount || 0) + serviceFee;
  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4" onClick={onClose}>
      <div className="bg-white rounded-2xl p-6 w-80 shadow-xl" onClick={e => e.stopPropagation()}>
        <div className="text-center mb-4">
          <h2 className="font-bold text-lg">🍜 Nhà hàng</h2>
          <p className="text-xs text-gray-400">123 Đường ABC, Q.1, TP.HCM</p>
          <div className="my-2 border-t"></div>
          <h3 className="font-bold">HÓA ĐƠN THANH TOÁN</h3>
          <p className="text-xs text-gray-500">Mã đơn: #{order.id} • {order.tableNumber || 'Online'}</p>
        </div>
        <div className="border-t border-dashed my-2"></div>
        <div className="space-y-1.5 mb-3">
          {order.items?.map((item, i) => (
            <div key={i} className="flex justify-between text-sm">
              <span>{item.foodName} × {item.quantity}</span>
              <span>{formatPrice(item.subtotal)}</span>
            </div>
          ))}
        </div>
        <div className="border-t border-dashed my-2 space-y-1">
          <div className="flex justify-between text-sm text-gray-500"><span>Tạm tính</span><span>{formatPrice(order.totalAmount)}</span></div>
          <div className="flex justify-between text-sm text-gray-500"><span>Phí dịch vụ (5%)</span><span>{formatPrice(serviceFee)}</span></div>
          <div className="flex justify-between font-bold text-[#e85d04]"><span>TỔNG CỘNG</span><span>{formatPrice(grand)}</span></div>
        </div>
        <p className="text-center text-xs text-gray-400 mt-3">Cảm ơn quý khách! Hẹn gặp lại 🙏</p>
        <div className="flex gap-2 mt-4">
          <button onClick={() => window.print()} className="flex-1 bg-[#e85d04] text-white font-semibold py-2 rounded-xl text-sm">🖨️ In</button>
          <button onClick={onClose} className="flex-1 bg-gray-100 text-gray-700 font-semibold py-2 rounded-xl text-sm">Đóng</button>
        </div>
      </div>
    </div>
  );
}

function CreateOrderModal({ onClose, onCreate, foods, tables }) {
  const [table, setTable] = useState('');
  const [cart, setCart] = useState([]);
  const [catFilter, setCatFilter] = useState('Tất cả');

  const categories = ['Tất cả', ...new Set(foods.map(f => f.categoryName).filter(Boolean))];
  const filteredFoods = catFilter === 'Tất cả' ? foods : foods.filter(f => f.categoryName === catFilter);

  const addToCart = (food) => {
    setCart(prev => {
      const ex = prev.find(c => c.id === food.id);
      if (ex) return prev.map(c => c.id === food.id ? {...c, quantity: c.quantity + 1} : c);
      return [...prev, { ...food, quantity: 1 }];
    });
  };

  const updateQty = (id, qty) => {
    if (qty <= 0) setCart(prev => prev.filter(c => c.id !== id));
    else setCart(prev => prev.map(c => c.id === id ? {...c, quantity: qty} : c));
  };

  const total = cart.reduce((s, c) => s + Number(c.price) * c.quantity, 0);

  const handleCreate = async (e) => {
    e.preventDefault();
    if (cart.length === 0) return;
    await onCreate({
      orderType: table ? 'DINE_IN' : 'ONLINE',
      tableId: table ? Number(table) : null,
      items: cart.map(c => ({ foodId: c.id, quantity: c.quantity })),
    });
    onClose();
  };

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4" onClick={onClose}>
      <div className="bg-white rounded-2xl p-6 w-full max-w-3xl shadow-xl max-h-[90vh] overflow-auto" onClick={e => e.stopPropagation()}>
        <h2 className="text-lg font-bold text-gray-800 mb-4">📋 Tạo đơn hàng mới</h2>
        <form onSubmit={handleCreate}>
          <div className="mb-4">
            <label className="block text-sm font-semibold text-gray-700 mb-1.5">Bàn (để trống nếu giao hàng)</label>
            <select className="px-3 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-[#e85d04] w-48"
              value={table} onChange={e => setTable(e.target.value)}>
              <option value="">-- Giao hàng --</option>
              {tables.filter(t => t.status === 'AVAILABLE').map(t => (
                <option key={t.id} value={t.id}>Bàn {t.tableNumber} ({t.capacity} người)</option>
              ))}
            </select>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-2 gap-5">
            {/* Menu */}
            <div>
              <div className="flex gap-2 flex-wrap mb-3">
                {categories.map(c => (
                  <button key={c} type="button" onClick={() => setCatFilter(c)}
                    className={`px-3 py-1 rounded-full text-xs font-semibold border ${catFilter === c ? 'bg-[#e85d04] text-white border-[#e85d04]' : 'bg-white text-gray-600 border-gray-200'}`}>
                    {c}
                  </button>
                ))}
              </div>
              <div className="space-y-2 max-h-64 overflow-y-auto">
                {filteredFoods.filter(f => f.status === 'AVAILABLE').map(food => (
                  <div key={food.id} className="flex items-center justify-between px-3 py-2 bg-gray-50 rounded-xl">
                    <div>
                      <p className="text-sm font-medium text-gray-800">{food.name}</p>
                      <p className="text-xs text-[#e85d04]">{formatPrice(food.price)}</p>
                    </div>
                    <button type="button" onClick={() => addToCart(food)}
                      className="w-7 h-7 rounded-full bg-[#e85d04] text-white font-bold text-sm flex items-center justify-center hover:bg-[#c44d00]">+</button>
                  </div>
                ))}
              </div>
            </div>

            {/* Cart */}
            <div>
              <h4 className="text-sm font-bold text-gray-700 mb-3">Giỏ hàng {cart.length > 0 && `(${cart.length} món)`}</h4>
              {cart.length === 0 ? (
                <p className="text-sm text-gray-400 text-center py-8">Chưa chọn món nào</p>
              ) : (
                <div className="space-y-2 max-h-48 overflow-y-auto mb-3">
                  {cart.map(item => (
                    <div key={item.id} className="flex items-center gap-2">
                      <span className="flex-1 text-sm text-gray-700 truncate">{item.name}</span>
                      <div className="flex items-center gap-1">
                        <button type="button" onClick={() => updateQty(item.id, item.quantity - 1)}
                          className="w-6 h-6 rounded-full border border-gray-200 text-xs flex items-center justify-center">−</button>
                        <span className="w-6 text-center text-sm font-semibold">{item.quantity}</span>
                        <button type="button" onClick={() => updateQty(item.id, item.quantity + 1)}
                          className="w-6 h-6 rounded-full border border-gray-200 text-xs flex items-center justify-center">+</button>
                      </div>
                      <span className="text-xs text-[#e85d04] font-semibold w-20 text-right">{formatPrice(Number(item.price) * item.quantity)}</span>
                    </div>
                  ))}
                </div>
              )}
              {cart.length > 0 && (
                <div className="border-t pt-2 flex justify-between font-bold text-sm">
                  <span>Tổng:</span><span className="text-[#e85d04]">{formatPrice(total)}</span>
                </div>
              )}
            </div>
          </div>

          <div className="flex gap-3 mt-4">
            <button type="submit" disabled={cart.length === 0}
              className="flex-1 bg-[#e85d04] hover:bg-[#c44d00] disabled:opacity-50 text-white font-semibold py-2.5 rounded-xl text-sm">
              ✅ Tạo đơn hàng
            </button>
            <button type="button" onClick={onClose} className="flex-1 bg-gray-100 hover:bg-gray-200 text-gray-700 font-semibold py-2.5 rounded-xl text-sm">Hủy</button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default function StaffOrders() {
  const [orders, setOrders] = useState([]);
  const [foods, setFoods] = useState([]);
  const [tables, setTables] = useState([]);
  const [filter, setFilter] = useState('all');
  const [showCreate, setShowCreate] = useState(false);
  const [showInvoice, setShowInvoice] = useState(null);

  const load = () => { getAllOrders().then(({ data }) => setOrders(data)).catch(() => {}); };
  useEffect(() => {
    load();
    getAllFoods().then(({ data }) => setFoods(data)).catch(() => {});
    getAllTables().then(({ data }) => setTables(data)).catch(() => {});
  }, []);

  const advance = async (id, status) => { await updateOrderStatus(id, STATUS_FLOW[status]); load(); };
  const cancel  = async (id) => { await updateOrderStatus(id, 'CANCELLED'); load(); };
  const handleCreate = async (data) => { await createOrder(data); load(); };

  const filtered = filter === 'all' ? orders : orders.filter(o => o.status === filter);

  return (
    <div>
      <div className="flex items-center justify-between mb-5">
        <h1 className="text-2xl font-bold text-gray-800">Đơn hàng</h1>
        <button onClick={() => setShowCreate(true)} className="bg-[#e85d04] hover:bg-[#c44d00] text-white text-sm font-semibold px-4 py-2.5 rounded-xl">+ Tạo đơn mới</button>
      </div>

      <div className="flex gap-2 flex-wrap mb-5">
        {[['all','Tất cả'],['PENDING','Chờ xác nhận'],['CONFIRMED','Đã xác nhận'],['PREPARING','Đang chuẩn bị'],['READY','Sẵn sàng'],['COMPLETED','Hoàn thành'],['CANCELLED','Đã hủy']].map(([v,l]) => (
          <button key={v} onClick={() => setFilter(v)}
            className={`px-3 py-2 rounded-full text-xs font-semibold border transition-colors ${filter === v ? 'bg-[#e85d04] text-white border-[#e85d04]' : 'bg-white text-gray-600 border-gray-200 hover:border-[#e85d04]'}`}>
            {l}
          </button>
        ))}
      </div>

      <div className="space-y-3">
        {filtered.map(order => (
          <div key={order.id} className="bg-white rounded-2xl shadow-sm p-4 flex items-center gap-4">
            <div className="shrink-0 w-20">
              <p className="font-bold text-[#e85d04] text-sm">#{order.id}</p>
              <p className="text-xs text-gray-400">{order.tableNumber ? `🪑 ${order.tableNumber}` : '🚀 Online'}</p>
            </div>
            <div className="flex-1 min-w-0">
              <div className="flex flex-wrap gap-1">
                {order.items?.slice(0, 3).map((item, i) => (
                  <span key={i} className="text-xs bg-gray-100 text-gray-600 px-2 py-0.5 rounded-full">{item.foodName} ×{item.quantity}</span>
                ))}
                {order.items?.length > 3 && <span className="text-xs text-gray-400">+{order.items.length - 3} món</span>}
              </div>
            </div>
            <div className="shrink-0 text-right">
              <p className="font-bold text-gray-800">{formatPrice(order.finalAmount)}</p>
              <span className={`text-xs font-semibold px-2 py-0.5 rounded-full ${STATUS_COLOR[order.status] || 'bg-gray-100 text-gray-500'}`}>{STATUS_MAP[order.status]}</span>
            </div>
            <div className="shrink-0 flex gap-2">
              {STATUS_FLOW[order.status] && (
                <button onClick={() => advance(order.id, order.status)}
                  className="bg-[#e85d04] hover:bg-[#c44d00] text-white text-xs font-semibold px-3 py-1.5 rounded-lg">
                  {NEXT_LABEL[order.status]}
                </button>
              )}
              {!['COMPLETED','CANCELLED'].includes(order.status) && (
                <button onClick={() => cancel(order.id)}
                  className="bg-red-50 hover:bg-red-100 text-red-500 text-xs font-semibold px-3 py-1.5 rounded-lg">✕</button>
              )}
              <button onClick={() => setShowInvoice(order)}
                className="bg-gray-100 hover:bg-gray-200 text-gray-600 text-xs font-semibold px-3 py-1.5 rounded-lg">🖨️</button>
            </div>
          </div>
        ))}
        {filtered.length === 0 && (
          <div className="text-center py-12 text-gray-400 bg-white rounded-2xl">
            <p className="text-4xl mb-2">📋</p><p>Không có đơn hàng</p>
          </div>
        )}
      </div>

      {showCreate && <CreateOrderModal foods={foods} tables={tables} onClose={() => setShowCreate(false)} onCreate={handleCreate} />}
      {showInvoice && <InvoiceModal order={showInvoice} onClose={() => setShowInvoice(null)} />}
    </div>
  );
}
