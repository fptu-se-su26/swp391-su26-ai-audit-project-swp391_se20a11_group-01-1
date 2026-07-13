import React, { useEffect, useState } from 'react';
import API from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import './AdminAccounts.css';

const ROLES = ['ADMIN', 'STAFF', 'KITCHEN', 'CUSTOMER'];

const roleLabel = {
  ADMIN: 'Admin',
  STAFF: 'Nhân viên',
  KITCHEN: 'Bếp',
  CUSTOMER: 'Khách hàng'
};

const roleColor = {
  ADMIN: '#fed7c3',
  STAFF: '#e6f4ff',
  KITCHEN: '#fefcbf',
  CUSTOMER: '#c6f6d5'
};

const roleIcon = {
  ADMIN: '👑',
  STAFF: '🧑‍💼',
  KITCHEN: '👨‍🍳',
  CUSTOMER: '👤'
};

function AdminAccounts() {
  const { user: currentUser } = useAuth();

  const [accounts, setAccounts] = useState([]);
  const [search, setSearch] = useState('');
  const [roleFilter, setRoleFilter] = useState('all');
  const [statusFilter, setStatusFilter] = useState('all');

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [toast, setToast] = useState(null);

  useEffect(() => {
    fetchAccounts();
  }, []);

  const showToast = (msg, ok = true) => {
    setToast({ msg, ok });
    setTimeout(() => setToast(null), 2500);
  };

  const getApiMessage = (data, fallback) => {
    if (!data) return fallback;

    if (typeof data === 'string') {
      return data;
    }

    if (typeof data === 'object') {
      return data.message || data.error || data.detail || fallback;
    }

    return fallback;
  };

  const fetchAccounts = async () => {
    setLoading(true);
    setError('');

    try {
      const response = await API.get('/users');
      setAccounts(response.data || []);
    } catch (error) {
      console.error('Fetch accounts error:', error);

      setError(
        getApiMessage(
          error.response?.data,
          'Không thể tải danh sách người dùng'
        )
      );
    } finally {
      setLoading(false);
    }
  };

  const getUserId = (account) => {
    return account.userId || account.id;
  };

  const getName = (account) => {
    return (
      account.name ||
      account.fullName ||
      account.username ||
      account.email ||
      'Người dùng'
    );
  };

  const getUsername = (account) => {
    return account.username || account.email || '';
  };

  const getEmail = (account) => {
    return account.email || '';
  };

  const getPhone = (account) => {
    return account.phone || account.phoneNumber || '';
  };

  const getRole = (account) => {
    return String(
      account.roleName ||
      account.role ||
      account.roleCode ||
      'CUSTOMER'
    ).toUpperCase();
  };

  const isBanned = (account) => {
    return account.banned === true || account.isBanned === true || account.active === false;
  };

  const isSelf = (account) => {
    const accountUsername = getUsername(account);
    const accountEmail = getEmail(account);

    return (
      accountUsername === currentUser?.username ||
      accountEmail === currentUser?.email
    );
  };

  const filtered = accounts.filter((account) => {
    const keyword = search.trim().toLowerCase();

    const name = getName(account).toLowerCase();
    const username = getUsername(account).toLowerCase();
    const email = getEmail(account).toLowerCase();
    const phone = getPhone(account).toLowerCase();
    const role = getRole(account);

    const matchSearch =
      !keyword ||
      name.includes(keyword) ||
      username.includes(keyword) ||
      email.includes(keyword) ||
      phone.includes(keyword);

    const matchRole =
      roleFilter === 'all' ||
      role === roleFilter;

    const banned = isBanned(account);

    const matchStatus =
      statusFilter === 'all' ||
      (statusFilter === 'banned' && banned) ||
      (statusFilter === 'active' && !banned);

    return matchSearch && matchRole && matchStatus;
  });

  const stats = {
    total: accounts.length,
    active: accounts.filter(account => !isBanned(account)).length,
    banned: accounts.filter(account => isBanned(account)).length,
    byRole: ROLES.reduce((acc, role) => {
      acc[role] = accounts.filter(account => getRole(account) === role).length;
      return acc;
    }, {})
  };

  if (loading) {
    return (
      <div className="admin-accounts">
        <h1 className="page-title">Quản lý người dùng</h1>

        <div className="accounts-table card" style={{ padding: 32, textAlign: 'center' }}>
          ⏳ Đang tải danh sách người dùng...
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="admin-accounts">
        <div className="page-header">
          <h1 className="page-title">Quản lý người dùng</h1>

          <button className="btn-primary" onClick={fetchAccounts}>
            🔄 Thử lại
          </button>
        </div>

        <div className="accounts-table card" style={{ padding: 32, color: '#dc2626' }}>
          ⚠️ {error}
        </div>
      </div>
    );
  }

  return (
    <div className="admin-accounts">
      {toast && (
        <div className={`ac-toast ${toast.ok ? 'toast-ok' : 'toast-err'}`}>
          {toast.msg}
        </div>
      )}

      <div className="page-header">
        <h1 className="page-title">Quản lý người dùng</h1>

        <button
          className="btn-primary"
          onClick={() => {
            fetchAccounts();
            showToast('Đã làm mới danh sách tài khoản');
          }}
        >
          🔄 Làm mới
        </button>
      </div>

      <div className="ac-stats">
        <div className="ac-stat-card" onClick={() => setStatusFilter('all')}>
          <span className="ac-stat-num">{stats.total}</span>
          <span className="ac-stat-label">Tổng tài khoản</span>
        </div>

        <div className="ac-stat-card active-card" onClick={() => setStatusFilter('active')}>
          <span className="ac-stat-num">{stats.active}</span>
          <span className="ac-stat-label">Đang hoạt động</span>
        </div>

        <div className="ac-stat-card banned-card" onClick={() => setStatusFilter('banned')}>
          <span className="ac-stat-num">{stats.banned}</span>
          <span className="ac-stat-label">Đã khóa</span>
        </div>

        {ROLES.map(role => (
          <div
            key={role}
            className="ac-stat-card role-card"
            onClick={() => setRoleFilter(role)}
          >
            <span className="ac-stat-num">{stats.byRole[role]}</span>
            <span className="ac-stat-label">
              {roleIcon[role]} {roleLabel[role]}
            </span>
          </div>
        ))}
      </div>

      <div className="accounts-toolbar">
        <input
          className="search-input"
          placeholder="🔍 Tìm theo tên, username, email hoặc SĐT..."
          value={search}
          onChange={e => setSearch(e.target.value)}
        />

        <div className="filter-row">
          <div className="filter-tabs">
            {['all', ...ROLES].map(role => (
              <button
                key={role}
                className={`filter-tab ${roleFilter === role ? 'active' : ''}`}
                onClick={() => setRoleFilter(role)}
              >
                {role === 'all'
                  ? 'Tất cả role'
                  : `${roleIcon[role]} ${roleLabel[role]}`}
              </button>
            ))}
          </div>

          <div className="filter-tabs">
            {[
              ['all', 'Tất cả'],
              ['active', 'Hoạt động'],
              ['banned', 'Đã khóa']
            ].map(([value, label]) => (
              <button
                key={value}
                className={`filter-tab ${statusFilter === value ? 'active' : ''}`}
                onClick={() => setStatusFilter(value)}
              >
                {label}
              </button>
            ))}
          </div>
        </div>
      </div>

      <div className="accounts-table card">
        <table className="data-table">
          <thead>
            <tr>
              <th>Người dùng</th>
              <th>Username</th>
              <th>Email</th>
              <th>Vai trò</th>
              <th>SĐT</th>
              <th>Trạng thái</th>
            </tr>
          </thead>

          <tbody>
            {filtered.length === 0 && (
              <tr>
                <td
                  colSpan={6}
                  style={{
                    textAlign: 'center',
                    color: '#a0aec0',
                    padding: 24
                  }}
                >
                  Không có tài khoản nào
                </td>
              </tr>
            )}

            {filtered.map(account => {
              const userId = getUserId(account);
              const name = getName(account);
              const username = getUsername(account);
              const email = getEmail(account);
              const phone = getPhone(account);
              const role = getRole(account);
              const banned = isBanned(account);

              return (
                <tr key={userId || username || email} className={banned ? 'row-banned' : ''}>
                  <td>
                    <div className="user-cell">
                      <div className="user-avatar-sm">
                        {name?.charAt(0)?.toUpperCase() || '?'}
                      </div>

                      <div>
                        <strong>{name}</strong>

                        {isSelf(account) && (
                          <span className="self-badge">Bạn</span>
                        )}
                      </div>
                    </div>
                  </td>

                  <td>
                    <code className="username-code">
                      {username || '—'}
                    </code>
                  </td>

                  <td>
                    <span className="phone-text">
                      {email || '—'}
                    </span>
                  </td>

                  <td>
                    <span
                      className="role-select"
                      style={{
                        background: roleColor[role] || '#e2e8f0',
                        display: 'inline-block'
                      }}
                    >
                      {roleIcon[role] || '👤'} {roleLabel[role] || role}
                    </span>
                  </td>

                  <td>
                    <span className="phone-text">
                      {phone || '—'}
                    </span>
                  </td>

                  <td>
                    <span className={`status-pill ${banned ? 'pill-banned' : 'pill-active'}`}>
                      {banned ? '🔒 Đã khóa' : '✅ Hoạt động'}
                    </span>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default AdminAccounts;