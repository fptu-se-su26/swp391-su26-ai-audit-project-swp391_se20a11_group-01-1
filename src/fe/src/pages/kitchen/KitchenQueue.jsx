import { useEffect, useRef, useState } from 'react';

const INITIAL_QUEUE = [
  { id: '#S013', table: 'Bàn 3', time: '18:40', arrivedAt: Date.now() - 12 * 60000,
    items: [{ name: 'Lẩu bò nhúng dấm', qty: 2, status: 'cooking' }, { name: 'Mực chiên giòn', qty: 2, status: 'pending' }, { name: 'Bia Tiger', qty: 4, status: 'done' }] },
  { id: '#S016', table: 'Bàn 5', time: '18:45', arrivedAt: Date.now() - 7 * 60000,
    items: [{ name: 'Gà nướng muối ớt', qty: 1, status: 'cooking' }, { name: 'Sườn nướng BBQ', qty: 2, status: 'pending' }, { name: 'Rau muống xào tỏi', qty: 1, status: 'done' }] },
  { id: '#S017', table: 'Bàn 9', time: '18:52', arrivedAt: Date.now() - 3 * 60000,
    items: [{ name: 'Bò lúc lắc', qty: 2, status: 'pending' }, { name: 'Nhậu hải sản mix', qty: 1, status: 'pending' }, { name: 'Đậu hũ chiên', qty: 2, status: 'pending' }] },
];

const ITEM_STATUS = {
  pending: { label: 'Chờ', cls: 'bg-gray-100 text-gray-600', next: 'cooking' },
  cooking: { label: 'Đang nấu', cls: 'bg-orange-100 text-orange-600', next: 'done' },
  done:    { label: 'Xong', cls: 'bg-green-100 text-green-700', next: null },
};

function elapsed(arrivedAt) {
  const mins = Math.floor((Date.now() - arrivedAt) / 60000);
  return mins < 1 ? '< 1 phút' : `${mins} phút`;
}

function urgency(arrivedAt) {
  const mins = Math.floor((Date.now() - arrivedAt) / 60000);
  if (mins >= 20) return 'border-red-400 bg-red-50';
  if (mins >= 10) return 'border-yellow-400 bg-yellow-50';
  return 'border-gray-200 bg-white';
}

export default function KitchenQueue() {
  const [queue, setQueue] = useState(INITIAL_QUEUE);
  const [done, setDone] = useState([]);
  const [clock, setClock] = useState(new Date());
  const [filter, setFilter] = useState('all');

  useEffect(() => {
    const t = setInterval(() => setClock(new Date()), 1000);
    return () => clearInterval(t);
  }, []);

  const advanceItem = (orderId, itemIdx) => {
    setQueue(prev => prev.map(order => {
      if (order.id !== orderId) return order;
      return { ...order, items: order.items.map((item, i) => {
        if (i !== itemIdx || !ITEM_STATUS[item.status].next) return item;
        return { ...item, status: ITEM_STATUS[item.status].next };
      })};
    }));
  };

  const completeOrder = (orderId) => {
    const order = queue.find(o => o.id === orderId);
    setDone(prev => [{ ...order, completedAt: new Date().toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' }) }, ...prev]);
    setQueue(prev => prev.filter(o => o.id !== orderId));
  };

  const allDone = (order) => order.items.every(i => i.status === 'done');
  const cookingCount = queue.filter(o => o.items.some(i => i.status === 'cooking')).length;
  const pendingCount = queue.filter(o => o.items.some(i => i.status === 'pending')).length;

  const filtered = queue.filter(order => {
    if (filter === 'cooking') return order.items.some(i => i.status === 'cooking');
    if (filter === 'pending') return order.items.some(i => i.status === 'pending');
    if (filter === 'ready')   return allDone(order);
    return true;
  });

  return (
    <div>
      {/* Topbar */}
      <div className="flex items-center justify-between mb-5">
        <div className="flex items-center gap-2 text-white">
          <span className="text-lg">🕐</span>
          <span className="font-bold text-xl">{clock.toLocaleTimeString('vi-VN')}</span>
        </div>
        <div className="flex gap-2">
          {[
            { label: `${queue.length} đơn`, cls: 'bg-gray-700 text-white' },
            { label: `🔥 ${cookingCount} đang nấu`, cls: 'bg-orange-500 text-white' },
            { label: `⏳ ${pendingCount} chờ`, cls: 'bg-yellow-500 text-white' },
            { label: `✅ ${done.length} hoàn thành`, cls: 'bg-green-600 text-white' },
          ].map((b, i) => (
            <span key={i} className={`text-sm font-semibold px-3 py-1.5 rounded-full ${b.cls}`}>{b.label}</span>
          ))}
        </div>
      </div>

      {/* Filter */}
      <div className="flex gap-2 mb-5">
        {[['all','Tất cả'],['pending','Chờ nấu'],['cooking','Đang nấu'],['ready','Sẵn sàng']].map(([v,l]) => (
          <button key={v} onClick={() => setFilter(v)}
            className={`px-4 py-2 rounded-full text-sm font-semibold border transition-colors ${filter === v ? 'bg-[#e85d04] text-white border-[#e85d04]' : 'bg-gray-700 text-gray-300 border-gray-600 hover:border-[#e85d04]'}`}>
            {l}
          </button>
        ))}
      </div>

      {/* Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {filtered.length === 0 && (
          <div className="col-span-3 text-center py-16 text-gray-400">
            <p className="text-5xl mb-3">🎉</p><p className="text-lg">Không có đơn nào đang chờ</p>
          </div>
        )}
        {filtered.map(order => {
          const ready = allDone(order);
          const doneCount = order.items.filter(i => i.status === 'done').length;
          return (
            <div key={order.id} className={`rounded-2xl border-2 p-5 transition-all ${ready ? 'border-green-400 bg-green-900/20' : urgency(order.arrivedAt)}`}>
              <div className="flex items-center justify-between mb-4">
                <div>
                  <span className="font-bold text-[#e85d04] text-lg">{order.id}</span>
                  <span className="ml-2 text-gray-300 text-sm">🪑 {order.table}</span>
                </div>
                <div className="text-right">
                  <p className="text-gray-300 text-sm">⏱ {order.time}</p>
                  <p className={`text-xs font-semibold ${Math.floor((Date.now()-order.arrivedAt)/60000) >= 20 ? 'text-red-400' : Math.floor((Date.now()-order.arrivedAt)/60000) >= 10 ? 'text-yellow-400' : 'text-gray-400'}`}>
                    {elapsed(order.arrivedAt)} {Math.floor((Date.now()-order.arrivedAt)/60000) >= 20 ? '⚠️' : ''}
                  </p>
                </div>
              </div>

              <div className="space-y-2 mb-4">
                {order.items.map((item, i) => (
                  <div key={i} className="flex items-center justify-between bg-gray-800/50 rounded-xl px-3 py-2">
                    <div>
                      <span className="text-sm font-medium text-white">{item.name}</span>
                      <span className="text-xs text-gray-400 ml-2">×{item.qty}</span>
                    </div>
                    <div className="flex items-center gap-2">
                      <span className={`text-xs font-semibold px-2 py-0.5 rounded-full ${ITEM_STATUS[item.status].cls}`}>{ITEM_STATUS[item.status].label}</span>
                      {ITEM_STATUS[item.status].next && (
                        <button onClick={() => advanceItem(order.id, i)}
                          className="text-xs bg-[#e85d04] hover:bg-[#c44d00] text-white font-semibold px-2.5 py-1 rounded-lg">
                          {item.status === 'pending' ? '🔥 Bắt đầu' : '✓ Xong'}
                        </button>
                      )}
                    </div>
                  </div>
                ))}
              </div>

              {ready ? (
                <div className="flex items-center justify-between bg-green-500/20 rounded-xl px-4 py-2">
                  <span className="text-green-400 font-semibold text-sm">✅ Tất cả món sẵn sàng!</span>
                  <button onClick={() => completeOrder(order.id)}
                    className="bg-green-500 hover:bg-green-600 text-white text-sm font-bold px-4 py-1.5 rounded-xl">
                    🚀 Đã phục vụ
                  </button>
                </div>
              ) : (
                <div>
                  <div className="w-full bg-gray-700 rounded-full h-1.5 mb-1">
                    <div className="bg-[#e85d04] h-1.5 rounded-full transition-all" style={{ width: `${Math.round(doneCount / order.items.length * 100)}%` }}></div>
                  </div>
                  <p className="text-xs text-gray-400">{doneCount}/{order.items.length} món xong</p>
                </div>
              )}
            </div>
          );
        })}
      </div>

      {done.length > 0 && (
        <div className="mt-6">
          <h3 className="text-gray-300 font-bold mb-3">✅ Đã hoàn thành hôm nay ({done.length})</h3>
          <div className="flex flex-wrap gap-2">
            {done.map((o, i) => (
              <div key={i} className="bg-gray-800 rounded-xl px-4 py-2 flex items-center gap-3 text-sm">
                <span className="text-[#e85d04] font-bold">{o.id}</span>
                <span className="text-gray-300">🪑 {o.table}</span>
                <span className="text-gray-400">{o.items.length} món</span>
                <span className="text-green-400">✅ {o.completedAt}</span>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
