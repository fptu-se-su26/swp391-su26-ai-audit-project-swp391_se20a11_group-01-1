import axiosClient from './axiosClient';
import type { ApiResponse } from '../types/auth';
import type {
  KitchenOrderResponse,
  KitchenUpdateOrderStatusRequest
} from '../types/kitchen';

export const kitchenApi = {
  getActiveOrders: async (): Promise<KitchenOrderResponse[]> => {
    const response = await axiosClient.get<ApiResponse<KitchenOrderResponse[]>>('/kitchen/orders');
    return response.data.data;
  },

  getOrderById: async (id: number): Promise<KitchenOrderResponse> => {
    const response = await axiosClient.get<ApiResponse<KitchenOrderResponse>>(`/kitchen/orders/${id}`);
    return response.data.data;
  },

  updateOrderStatus: async (id: number, data: KitchenUpdateOrderStatusRequest): Promise<KitchenOrderResponse> => {
    const response = await axiosClient.patch<ApiResponse<KitchenOrderResponse>>(`/kitchen/orders/${id}/status`, data);
    return response.data.data;
  }
};
