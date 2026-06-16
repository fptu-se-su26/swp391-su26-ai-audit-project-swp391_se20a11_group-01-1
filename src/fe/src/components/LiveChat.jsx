import { useEffect, useRef, useState } from 'react';
import { useAuth } from '../context/AuthContext';

const INITIAL_MESSAGES = [
  { id: 1, from: 'staff', name: 'Nhân viên', text: 'Xin chào! Chúng tôi có thể giúp gì cho bạn? 😊', time: '' },
];

export default function LiveChat() {
  const { user } = useAuth();
  const [open, setOpen]         = useState(false);
  const [messages, setMessages] = useState(INITIAL_MESSAGES);
  const [input, setInput]       = useState('');
  const [unread, setUnread]     = useState(1);
  const bottomRef               = useRef(null);

  useEffect(() => {
    if (open) { setUnread(0); }
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [open, messages]);

  const mockReply = () => {
    const replies = [
      'Cảm ơn bạn đã liên hệ! Chúng tôi sẽ hỗ trợ ngay.',
      'Bạn vui lòng cho biết thêm chi tiết nhé?',
      'Nhân viên của chúng tôi sẽ đến bàn bạn ngay!',
      'Dạ, chúng tôi đã ghi nhận yêu cầu của bạn 🙏',
    ];
    setTimeout(() => {
      setMessages(prev => [...prev, {
        id: Date.now(), from: 'staff', name: 'Nhân viên',
        text: replies[Math.floor(Math.random() * replies.length)],
        time: new Date().toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' }),
      }]);
      if (!open) setUnread(n => n + 1);
    }, 1500);
  };

  const send = () => {
    if (!input.trim()) return;
    setMessages(prev => [...prev, {
      id: Date.now(), from: 'customer',
      name: user?.fullName || 'Bạn', text: input.trim(),
      time: new Date().toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' }),
    }]);
    setInput('');
    mockReply();
  };

  return (
    <>
      {/* FAB */}
      <button onClick={() => setOpen(!open)}
        className="fixed bottom-6 right-6 z-50 w-14 h-14 bg-[#e85d04] hover:bg-[#c44d00] text-white text-2xl rounded-full shadow-lg flex items-center justify-center transition-colors">
        {open ? '✕' : '💬'}
        {!open && unread > 0 && (
          <span className="absolute -top-1 -right-1 bg-red-500 text-white text-xs w-5 h-5 rounded-full flex items-center justify-center font-bold">{unread}</span>
        )}
      </button>

      {open && (
        <div className="fixed bottom-24 right-6 z-50 w-80 bg-white rounded-2xl shadow-2xl flex flex-col overflow-hidden border border-gray-200" style={{ height: 420 }}>
          {/* Header */}
          <div className="bg-[#1a1a2e] px-4 py-3 flex items-center gap-3">
            <div className="w-9 h-9 bg-[#e85d04] rounded-full flex items-center justify-center text-lg">👨‍💼</div>
            <div>
              <p className="text-white font-semibold text-sm">Hỗ trợ trực tuyến</p>
              <p className="text-green-400 text-xs">● Đang trực tuyến</p>
            </div>
            <button onClick={() => setOpen(false)} className="ml-auto text-gray-400 hover:text-white">✕</button>
          </div>

          {/* Messages */}
          <div className="flex-1 overflow-y-auto p-3 space-y-3">
            {messages.map(msg => (
              <div key={msg.id} className={`flex gap-2 ${msg.from === 'customer' ? 'flex-row-reverse' : ''}`}>
                {msg.from === 'staff' && <div className="w-7 h-7 bg-[#e85d04] rounded-full flex items-center justify-center text-sm shrink-0">👨‍💼</div>}
                <div className={`max-w-[75%]`}>
                  <div className={`px-3 py-2 rounded-2xl text-sm ${msg.from === 'customer' ? 'bg-[#e85d04] text-white rounded-tr-none' : 'bg-gray-100 text-gray-800 rounded-tl-none'}`}>
                    {msg.text}
                  </div>
                  {msg.time && <p className="text-xs text-gray-400 mt-0.5 px-1">{msg.time}</p>}
                </div>
              </div>
            ))}
            <div ref={bottomRef} />
          </div>

          {/* Input */}
          <div className="border-t p-2 flex gap-2">
            <input value={input} onChange={e => setInput(e.target.value)}
              onKeyDown={e => { if (e.key === 'Enter' && !e.shiftKey) { e.preventDefault(); send(); }}}
              placeholder="Nhập tin nhắn..."
              className="flex-1 px-3 py-2 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-1 focus:ring-[#e85d04]" />
            <button onClick={send} disabled={!input.trim()}
              className="bg-[#e85d04] hover:bg-[#c44d00] disabled:opacity-50 text-white w-9 h-9 rounded-xl flex items-center justify-center">➤</button>
          </div>
        </div>
      )}
    </>
  );
}
