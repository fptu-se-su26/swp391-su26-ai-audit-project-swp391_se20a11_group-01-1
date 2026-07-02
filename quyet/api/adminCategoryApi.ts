import axiosClient from './axiosClient';
import type { ApiResponse } from '../types/auth';
import type { CategoryResponse, CategoryRequest } from '../types/category';

export const adminCategoryApi = {
  // NOTE: Backend limitation - there is no endpoint to fetch ALL categories including inactive ones.
  // We use the public active categories endpoint.
  getCategories: async (): Promise<CategoryResponse[]> => {
    const response = await axiosClient.get<ApiResponse<CategoryResponse[]>>('/categories');
    return response.data.data;
  },

  createCategoryResponse: async (data: CategoryRequest): Promise<CategoryResponse> => {
    const response = await axiosClient.post<ApiResponse<CategoryResponse>>('/admin/categories', data);
    return response.data.data;
  },

  updateCategoryResponse: async (id: number, data: CategoryRequest): Promise<CategoryResponse> => {
    const response = await axiosClient.put<ApiResponse<CategoryResponse>>(`/admin/categories/${id}`, data);
    return response.data.data;
  },

  updateCategoryResponseStatus: async (id: number, isActive: boolean): Promise<void> => {
    await axiosClient.patch<ApiResponse<void>>(`/admin/categories/${id}/status`, { isActive });
  }
};
