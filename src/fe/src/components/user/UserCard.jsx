import { getInitials, formatDate } from '../../utils/helpers';

const UserCard = ({ user, onEdit, onDelete }) => (
  <div className="bg-white rounded-2xl p-5 shadow-sm flex items-center gap-4">
    <div className="w-12 h-12 rounded-full bg-orange-100 text-orange-600 flex items-center justify-center font-bold shrink-0">
      {getInitials(user?.name)}
    </div>
    <div className="flex-1 min-w-0">
      <p className="font-semibold text-gray-800 truncate">{user?.name}</p>
      <p className="text-sm text-gray-400 truncate">{user?.email}</p>
      <p className="text-xs text-gray-400">Tham gia: {formatDate(user?.joinDate)}</p>
    </div>
    <div className="flex gap-2">
      {onEdit && <button onClick={() => onEdit(user)} className="text-xs text-blue-500 hover:text-blue-700 font-medium px-2 py-1 rounded hover:bg-blue-50">Sửa</button>}
      {onDelete && <button onClick={() => onDelete(user.id)} className="text-xs text-red-400 hover:text-red-600 font-medium px-2 py-1 rounded hover:bg-red-50">Xóa</button>}
    </div>
  </div>
);

export default UserCard;
