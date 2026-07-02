import React from 'react';
import { useAuth } from '../../hooks/useAuth';
import './ProfilePage.css';

export const ProfilePage: React.FC = () => {
  const { user } = useAuth();

  if (!user) {
    return <div className="profile-state">Đang tải thông tin hồ sơ...</div>;
  }

  return (
    <div className="profile-page">
      <div className="page-header">
        <h2>Hồ sơ cá nhân</h2>
      </div>

      <div className="profile-card">
        <div className="profile-header">
          <div className="avatar-placeholder">
            {user.username.charAt(0).toUpperCase()}
          </div>
          <div className="profile-title">
            <h3>{user.fullName || user.username}</h3>
            <span className="role-badge">Khách hàng</span>
          </div>
        </div>

        <div className="profile-body">
          <div className="info-row">
            <span className="info-label">Tên đăng nhập:</span>
            <span className="info-value">{user.username}</span>
          </div>
          
          <div className="info-row">
            <span className="info-label">Email:</span>
            <span className="info-value">{user.email}</span>
          </div>
          
          <div className="info-row">
            <span className="info-label">Họ và tên:</span>
            <span className="info-value">{user.fullName || 'Chưa cập nhật'}</span>
          </div>
          
          <div className="info-row">
            <span className="info-label">Số điện thoại:</span>
            <span className="info-value">{user.phone || 'Chưa cập nhật'}</span>
          </div>
        </div>

        <div className="profile-footer">
          <div className="limitation-notice">
            <p>ℹ️ Chức năng cập nhật hồ sơ chưa được backend hỗ trợ.</p>
          </div>
        </div>
      </div>
    </div>
  );
};
