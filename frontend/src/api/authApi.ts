import axiosClient from './axiosClient';
import type { AuthResponse, CurrentUserResponse, LoginRequest, ApiResponse } from '../types/auth';

export const authApi = {
  login: async (credentials: LoginRequest): Promise<AuthResponse> => {
    const response = await axiosClient.post<ApiResponse<AuthResponse>>('/auth/login', credentials);
    return response.data.data;
  },
  
  getCurrentUser: async (): Promise<CurrentUserResponse> => {
    const response = await axiosClient.get<ApiResponse<CurrentUserResponse>>('/auth/me');
    return response.data.data;
  }
};
