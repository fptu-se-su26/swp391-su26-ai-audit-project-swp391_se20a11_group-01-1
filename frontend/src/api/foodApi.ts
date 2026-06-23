import axiosClient from './axiosClient';
import type { FoodResponse, FoodDetailResponse, FoodSearchParams } from '../types/food';
import type { Page } from '../types/common';
import type { ApiResponse } from '../types/auth';

export const foodApi = {
  getPublicFoods: async (params?: FoodSearchParams): Promise<Page<FoodResponse>> => {
    const response = await axiosClient.get<ApiResponse<Page<FoodResponse>>>('/foods', { params });
    return response.data.data;
  },

  getFoodDetail: async (id: number): Promise<FoodDetailResponse> => {
    const response = await axiosClient.get<ApiResponse<FoodDetailResponse>>(`/foods/${id}`);
    return response.data.data;
  }
};
