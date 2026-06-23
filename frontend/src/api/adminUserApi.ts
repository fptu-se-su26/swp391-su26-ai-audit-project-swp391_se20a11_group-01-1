import axiosClient from './axiosClient';
import type { ApiResponse } from '../types/auth';
import type { Page } from '../types/common';
import type { UserListResponse, UserDetailResponse, UpdateUserStatusRequest } from '../types/admin';

export const adminUserApi = {
  getUsers: async (page = 0, size = 10, keyword?: string, status?: string, role?: string): Promise<Page<UserListResponse>> => {
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
    });
    if (keyword) params.append('keyword', keyword);
    if (status) params.append('status', status);
    if (role) params.append('role', role);

    const response = await axiosClient.get<ApiResponse<Page<UserListResponse>>>(`/admin/users?${params.toString()}`);
    return response.data.data;
  },

  getUserDetail: async (id: number): Promise<UserDetailResponse> => {
    const response = await axiosClient.get<ApiResponse<UserDetailResponse>>(`/admin/users/${id}`);
    return response.data.data;
  },

  updateUserStatus: async (id: number, data: UpdateUserStatusRequest): Promise<void> => {
    await axiosClient.patch<ApiResponse<void>>(`/admin/users/${id}/status`, data);
  }
};
