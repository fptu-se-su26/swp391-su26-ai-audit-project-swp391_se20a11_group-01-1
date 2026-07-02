import axiosClient from './axiosClient';
import type { ApiResponse } from '../types/auth';
import type { Page } from '../types/common';
import type { FoodResponse, FoodRequest } from '../types/food';

export const adminFoodApi = {
  getFoods: async (page = 0, size = 10, keyword?: string, categoryId?: number): Promise<Page<FoodResponse>> => {
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
    });
    if (keyword) params.append('keyword', keyword);
    if (categoryId) params.append('categoryId', categoryId.toString());

    const response = await axiosClient.get<ApiResponse<Page<FoodResponse>>>(`/admin/foods?${params.toString()}`);
    return response.data.data;
  },

  createFood: async (data: FoodRequest): Promise<FoodResponse> => {
    const response = await axiosClient.post<ApiResponse<FoodResponse>>('/admin/foods', data);
    return response.data.data;
  },

  updateFood: async (id: number, data: FoodRequest): Promise<FoodResponse> => {
    const response = await axiosClient.put<ApiResponse<FoodResponse>>(`/admin/foods/${id}`, data);
    return response.data.data;
  },

  updateFoodStatus: async (id: number, isAvailable: boolean): Promise<void> => {
    await axiosClient.patch<ApiResponse<void>>(`/admin/foods/${id}/status`, { isAvailable });
  }
};
