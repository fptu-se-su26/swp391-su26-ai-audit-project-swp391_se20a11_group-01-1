const history = [
  { id: '#S012', table: 'Bàn 2', completedAt: '18:35', items: ['Lẩu bò nhúng dấm ×2', 'Bò lúc lắc ×1', 'Bia Tiger ×4'] },
  { id: '#S011', table: 'Bàn 7', completedAt: '18:20', items: ['Sườn nướng BBQ ×3', 'Mực chiên giòn ×1', 'Rau muống xào tỏi ×2'] },
  { id: '#S010', table: 'Bàn 4', completedAt: '18:05', items: ['Gà nướng muối ớt ×2', 'Nhậu hải sản mix ×1', 'Đậu hũ chiên ×2'] },
];

export default function KitchenHistory() {
  return (
    <div>
      <h1 className="text-2xl font-bold text-white mb-6">Đơn đã hoàn thành hôm nay</h1>
      <div className="space-y-3">
        {history.map(order => (
          <div key={order.id} className="bg-gray-800 rounded-2xl p-5">
            <div className="flex items-center gap-4 mb-3">
              <span className="font-bold text-[#e85d04] text-lg">{order.id}</span>
              <span className="text-gray-300">🪑 {order.table}</span>
              <span className="text-green-400 ml-auto">✅ {order.completedAt}</span>
            </div>
            <div className="flex flex-wrap gap-2">
              {order.items.map((item, i) => (
                <span key={i} className="text-xs bg-gray-700 text-gray-300 px-3 py-1 rounded-full">{item}</span>
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
