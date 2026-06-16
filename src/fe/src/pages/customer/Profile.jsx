import { useAuth } from '../../context/AuthContext';
import ProfileForm from '../../components/user/ProfileForm';
import { getInitials, formatDate } from '../../utils/helpers';

const Profile = () => {
  const { user } = useAuth();

  return (
    <div className="max-w-3xl mx-auto px-4 py-10">
      <h1 className="text-3xl font-bold text-gray-800 mb-8">Hồ sơ của tôi</h1>
      <div className="flex items-center gap-6 mb-8 bg-white rounded-3xl shadow-sm p-6">
        <div className="w-20 h-20 rounded-full bg-orange-100 text-orange-500 flex items-center justify-center text-3xl font-bold shrink-0">
          {getInitials(user?.fullName)}
        </div>
        <div>
          <h2 className="text-xl font-bold text-gray-800">{user?.fullName}</h2>
          <p className="text-sm text-gray-500">{user?.email}</p>
          <p className="text-xs text-gray-400 mt-1">
            Vai trò: <span className="text-orange-500 font-medium">{user?.roles?.join(', ').replace(/ROLE_/g, '')}</span>
          </p>
        </div>
      </div>
      <ProfileForm />
    </div>
  );
};

export default Profile;
