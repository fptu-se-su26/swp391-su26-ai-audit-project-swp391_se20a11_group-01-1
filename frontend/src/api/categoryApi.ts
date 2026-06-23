import axiosClient from './axiosClient';
import type { CategoryResponse } from '../types/category';
import type { ApiResponse } from '../types/auth';

export const categoryApi = {
  getActiveCategories: async (): Promise<CategoryResponse[]> => {
    const response = await axiosClient.get<ApiResponse<CategoryResponse[]>>('/categories');
    return response.data.data;
  }
};
