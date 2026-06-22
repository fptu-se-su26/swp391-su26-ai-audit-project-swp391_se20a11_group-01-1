import React, { useState } from 'react';
import { useNavigate, Link, Navigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import { authApi } from '../../api/authApi';
import { getDefaultRouteByRoles } from '../../utils/routeUtils';

export const HomePage: React.FC = () => {
  return (
    <div style={{ textAlign: 'center', marginTop: '50px' }}>
      <h1>Welcome to the Restaurant Management System</h1>
      <p>
        <Link to="/login">Login</Link> | <Link to="/register">Register</Link>
      </p>
    </div>
  );
};

export const LoginPage: React.FC = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const { login, isAuthenticated, user } = useAuth();

  if (isAuthenticated && user) {
    return <Navigate to={getDefaultRouteByRoles(user.roles)} replace />;
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const trimmedUsername = username.trim();
    
    if (!trimmedUsername || !password) {
      setError('Username and password are required');
      return;
    }

    setIsSubmitting(true);
    setError('');
    
    try {
      await login({ username: trimmedUsername, password });
      // The redirect will happen automatically due to AuthContext state change or we can navigate
    } catch (err: unknown) {
      const errorResponse = err as { response?: { data?: { message?: string } } };
      setError(errorResponse?.response?.data?.message || 'Login failed. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="auth-container">
      <h2>Login</h2>
      {error && <div className="error-message">{error}</div>}
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Username</label>
          <input 
            type="text" 
            value={username} 
            onChange={(e) => setUsername(e.target.value)} 
            disabled={isSubmitting} 
          />
        </div>
        <div className="form-group">
          <label>Password</label>
          <input 
            type="password" 
            value={password} 
            onChange={(e) => setPassword(e.target.value)} 
            disabled={isSubmitting} 
          />
        </div>
        <button type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Logging in...' : 'Login'}
        </button>
      </form>
      <p>Don't have an account? <Link to="/register">Register here</Link></p>
    </div>
  );
};

export const RegisterPage: React.FC = () => {
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
    fullName: '',
    phone: ''
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const { isAuthenticated, user } = useAuth();
  const navigate = useNavigate();

  if (isAuthenticated && user) {
    return <Navigate to={getDefaultRouteByRoles(user.roles)} replace />;
  }

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const trimmedUsername = formData.username.trim();
    
    if (!trimmedUsername || !formData.password || !formData.fullName || !formData.email) {
      setError('Username, email, password and full name are required');
      return;
    }

    if (formData.password !== formData.confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    setIsSubmitting(true);
    setError('');
    
    try {
      await authApi.register({
        username: trimmedUsername,
        email: formData.email,
        password: formData.password,
        fullName: formData.fullName,
        phone: formData.phone
      });
      setSuccess(true);
      setTimeout(() => {
        navigate('/login');
      }, 2000);
    } catch (err: unknown) {
      const errorResponse = err as { response?: { data?: { message?: string } } };
      setError(errorResponse?.response?.data?.message || 'Registration failed.');
    } finally {
      setIsSubmitting(false);
    }
  };

  if (success) {
    return (
      <div className="auth-container">
        <h2>Registration Successful!</h2>
        <p>Redirecting to login...</p>
      </div>
    );
  }

  return (
    <div className="auth-container">
      <h2>Register</h2>
      {error && <div className="error-message">{error}</div>}
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Username *</label>
          <input name="username" type="text" value={formData.username} onChange={handleChange} disabled={isSubmitting} />
        </div>
        <div className="form-group">
          <label>Full Name *</label>
          <input name="fullName" type="text" value={formData.fullName} onChange={handleChange} disabled={isSubmitting} />
        </div>
        <div className="form-group">
          <label>Email *</label>
          <input name="email" type="email" value={formData.email} onChange={handleChange} disabled={isSubmitting} />
        </div>
        <div className="form-group">
          <label>Phone</label>
          <input name="phone" type="text" value={formData.phone} onChange={handleChange} disabled={isSubmitting} />
        </div>
        <div className="form-group">
          <label>Password *</label>
          <input name="password" type="password" value={formData.password} onChange={handleChange} disabled={isSubmitting} />
        </div>
        <div className="form-group">
          <label>Confirm Password *</label>
          <input name="confirmPassword" type="password" value={formData.confirmPassword} onChange={handleChange} disabled={isSubmitting} />
        </div>
        <button type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Registering...' : 'Register'}
        </button>
      </form>
      <p>Already have an account? <Link to="/login">Login here</Link></p>
    </div>
  );
};

export const UnauthorizedPage: React.FC = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  return (
    <div className="auth-container" style={{ textAlign: 'center', marginTop: '50px' }}>
      <h2>Unauthorized Access</h2>
      <p>You do not have permission to view this page.</p>
      {user && (
        <p>Your current role(s): <strong>{user.roles.join(', ')}</strong></p>
      )}
      <div style={{ marginTop: '20px', display: 'flex', gap: '10px', justifyContent: 'center' }}>
        <button onClick={() => navigate(getDefaultRouteByRoles(user?.roles))}>Go to Homepage</button>
        <button onClick={() => { logout(); navigate('/login'); }} style={{ backgroundColor: '#e74c3c' }}>Logout</button>
      </div>
    </div>
  );
};

export const NotFoundPage: React.FC = () => {
  return <div style={{ textAlign: 'center', marginTop: '50px' }}><h2>404 - Page Not Found</h2></div>;
};
