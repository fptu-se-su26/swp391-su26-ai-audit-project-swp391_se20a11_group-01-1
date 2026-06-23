import React, { useState, useEffect } from 'react';
import { adminUserApi } from '../../api/adminUserApi';
import { getApiErrorMessage } from '../../utils/getApiErrorMessage';
import type { UserListResponse, UserStatus } from '../../types/admin';
import type { Page } from '../../types/common';
import './AdminUsersPage.css';

export const AdminUsersPage: React.FC = () => {
  const [usersPage, setUsersPage] = useState<Page<UserListResponse> | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  
  // Filters
  const [keyword, setKeyword] = useState('');
  const [status, setStatus] = useState('');
  const [role, setRole] = useState('');
  const [page, setPage] = useState(0);
  
  const [updatingId, setUpdatingId] = useState<number | null>(null);

  const fetchUsers = async (pageIndex: number = 0) => {
    setLoading(true);
    setError(null);
    try {
      const data = await adminUserApi.getUsers(pageIndex, 10, keyword, status, role);
      setUsersPage(data);
      setPage(pageIndex);
    } catch (err) {
      setError(getApiErrorMessage(err, 'Lỗi khi tải danh sách người dùng.'));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchUsers(0);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleFilter = (e: React.FormEvent) => {
    e.preventDefault();
    fetchUsers(0);
  };

  const handleUpdateStatus = async (user: UserListResponse, newStatus: UserStatus) => {
    if (!window.confirm(`Bạn có chắc chắn muốn chuyển trạng thái người dùng ${user.username} thành ${newStatus}?`)) {
      return;
    }

    setUpdatingId(user.id);
    try {
      await adminUserApi.updateUserStatus(user.id, { status: newStatus });
      alert('Cập nhật trạng thái thành công.');
      // Refresh list
      fetchUsers(page);
    } catch (err) {
      alert(getApiErrorMessage(err, 'Lỗi khi cập nhật trạng thái.'));
    } finally {
      setUpdatingId(null);
    }
  };

  return (
    <div className="admin-users-page">
      <div className="admin-header">
        <h2>Quản lý Người dùng</h2>
      </div>

      <div className="filter-section">
        <form onSubmit={handleFilter} className="filter-form">
          <div className="filter-group">
            <label>Từ khóa (username, email, tên):</label>
            <input 
              type="text" 
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
              placeholder="Nhập từ khóa..."
            />
          </div>
          <div className="filter-group">
            <label>Trạng thái:</label>
            <select value={status} onChange={(e) => setStatus(e.target.value)}>
              <option value="">-- Tất cả --</option>
              <option value="ACTIVE">Hoạt động (ACTIVE)</option>
              <option value="LOCKED">Đã khóa (LOCKED)</option>
            </select>
          </div>
          <div className="filter-group">
            <label>Role:</label>
            <select value={role} onChange={(e) => setRole(e.target.value)}>
              <option value="">-- Tất cả --</option>
              <option value="ROLE_CUSTOMER">Customer</option>
              <option value="ROLE_ADMIN">Admin</option>
              <option value="ROLE_STAFF">Staff</option>
              <option value="ROLE_KITCHEN">Kitchen</option>
            </select>
          </div>
          <button type="submit" className="btn-filter" disabled={loading}>
            Tìm kiếm
          </button>
        </form>
      </div>

      <div className="users-list-section">
        {loading && !usersPage ? (
          <div className="admin-page-state">Đang tải dữ liệu...</div>
        ) : error ? (
          <div className="admin-page-state text-error">{error}</div>
        ) : !usersPage || usersPage.content.length === 0 ? (
          <div className="admin-page-state">Không tìm thấy người dùng nào.</div>
        ) : (
          <>
            <div className="table-responsive">
              <table className="admin-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Username</th>
                    <th>Email</th>
                    <th>Họ và Tên</th>
                    <th>Roles</th>
                    <th>Trạng thái</th>
                    <th>Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  {usersPage.content.map((user: UserListResponse) => (
                    <tr key={user.id}>
                      <td>{user.id}</td>
                      <td>{user.username}</td>
                      <td>{user.email}</td>
                      <td>{user.fullName || '-'}</td>
                      <td>
                        {user.roles.map((r: string) => (
                          <span key={r} className="role-badge">{r.replace('ROLE_', '')}</span>
                        ))}
                      </td>
                      <td>
                        <span className={`status-badge ${user.isActive ? 'active' : 'inactive'}`}>
                          {user.isActive ? 'Hoạt động' : 'Bị khóa'}
                        </span>
                      </td>
                      <td>
                        {user.isActive ? (
                          <button 
                            className="btn-action btn-danger"
                            onClick={() => handleUpdateStatus(user, 'LOCKED')}
                            disabled={updatingId === user.id}
                          >
                            {updatingId === user.id ? '...' : 'Khóa'}
                          </button>
                        ) : (
                          <button 
                            className="btn-action btn-success"
                            onClick={() => handleUpdateStatus(user, 'ACTIVE')}
                            disabled={updatingId === user.id}
                          >
                            {updatingId === user.id ? '...' : 'Mở khóa'}
                          </button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {/* Pagination */}
            {usersPage.totalPages > 1 && (
              <div className="pagination">
                <button 
                  disabled={usersPage.first || loading} 
                  onClick={() => fetchUsers(page - 1)}
                >
                  &laquo; Trước
                </button>
                <span>Trang {page + 1} / {usersPage.totalPages}</span>
                <button 
                  disabled={usersPage.last || loading} 
                  onClick={() => fetchUsers(page + 1)}
                >
                  Sau &raquo;
                </button>
              </div>
            )}
          </>
        )}
      </div>
      
      <div className="limitation-notice">
        <p>ℹ️ Chức năng Gán / Gỡ Role không khả dụng vì backend hiện không cung cấp endpoint cho tính năng này.</p>
      </div>
    </div>
  );
};
