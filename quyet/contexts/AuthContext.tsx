import React, { createContext, useState, useEffect } from 'react';
import type { ReactNode } from 'react';
import type { User, LoginRequest } from '../types/auth';
import { authApi } from '../api/authApi';
import { tokenStorage } from '../utils/tokenStorage';

interface AuthContextType {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (credentials: LoginRequest) => Promise<void>;
  logout: () => void;
  hasRole: (role: string) => boolean;
  restoreSession: () => Promise<void>;
}

// eslint-disable-next-line react-refresh/only-export-components
export const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(tokenStorage.getUser());
  const [isLoading, setIsLoading] = useState<boolean>(!!tokenStorage.getAccessToken());

  const restoreSession = async () => {
    const token = tokenStorage.getAccessToken();
    if (!token) {
      return;
    }
    
    try {
      setIsLoading(true);
      const currentUserInfo = await authApi.getCurrentUser();
      const mappedUser: User = {
        id: currentUserInfo.id,
        username: currentUserInfo.username,
        email: currentUserInfo.email,
        fullName: currentUserInfo.fullName,
        phone: currentUserInfo.phone,
        roles: currentUserInfo.roles
      };
      setUser(mappedUser);
      tokenStorage.setUser(mappedUser);
    } catch (error: unknown) {
      console.error("Failed to fetch user info", error);
      const errorResponse = error as { response?: { status?: number } };
      if (errorResponse?.response?.status === 401) {
        tokenStorage.clear();
        setUser(null);
      }
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    restoreSession();
  }, []);

  useEffect(() => {
    const handle401 = () => {
      setUser(null);
    };
    window.addEventListener('auth:401', handle401);
    return () => window.removeEventListener('auth:401', handle401);
  }, []);

  const login = async (credentials: LoginRequest) => {
    setIsLoading(true);
    try {
      const data = await authApi.login(credentials);
      tokenStorage.setAccessToken(data.accessToken);
      if (data.refreshToken) {
        tokenStorage.setRefreshToken(data.refreshToken);
      }
      setUser(data.user);
      tokenStorage.setUser(data.user);
    } finally {
      setIsLoading(false);
    }
  };

  const logout = () => {
    tokenStorage.clear();
    setUser(null);
  };

  const hasRole = (role: string) => {
    return user?.roles?.includes(role) || false;
  };

  return (
    <AuthContext.Provider value={{
      user,
      isAuthenticated: !!user,
      isLoading,
      login,
      logout,
      hasRole,
      restoreSession
    }}>
      {children}
    </AuthContext.Provider>
  );
};
