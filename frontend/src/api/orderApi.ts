import axiosClient from './axiosClient';
import type { 
  CheckoutRequest, 
  OrderDetailResponse, 
  OrderResponse, 
  CancelOrderRequest 
} from '../types/order';
import type { ApiResponse } from '../types/auth';

export const orderApi = {
  checkout: async (request: CheckoutRequest): Promise<OrderDetailResponse> => {
    const response = await axiosClient.post<ApiResponse<OrderDetailResponse>>('/orders', request);
    return response.data.data;
  },

  getMyOrders: async (): Promise<OrderResponse[]> => {
    const response = await axiosClient.get<ApiResponse<OrderResponse[]>>('/orders');
    return response.data.data;
  },

  getOrderDetail: async (id: number): Promise<OrderDetailResponse> => {
    const response = await axiosClient.get<ApiResponse<OrderDetailResponse>>(`/orders/${id}`);
    return response.data.data;
  },

  cancelOrder: async (id: number, request: CancelOrderRequest): Promise<OrderResponse> => {
    const response = await axiosClient.patch<ApiResponse<OrderResponse>>(`/orders/${id}/cancel`, request);
    return response.data.data;
  }
};
