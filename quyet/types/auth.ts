export interface User {
  id: number;
  username: string;
  email: string;
  fullName?: string;
  phone?: string;
  roles: string[];
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  user: User;
}

export interface CurrentUserResponse {
  id: number;
  username: string;
  email: string;
  fullName: string;
  phone: string;
  roles: string[];
}

export interface LoginRequest {
  username?: string;
  password?: string;
}

export interface RegisterRequest {
  username?: string;
  email?: string;
  password?: string;
  fullName?: string;
  phone?: string;
  address?: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}
