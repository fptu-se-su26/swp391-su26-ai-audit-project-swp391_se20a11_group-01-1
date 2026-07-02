import axiosClient from './axiosClient';
import type { ApiResponse } from '../types/auth';
import type {
  StaffCreateOrderRequest,
  StaffOrderResponse,
  StaffUpdateOrderStatusRequest
} from '../types/staff';

export const staffOrderApi = {
  getAllOrders: async (): Promise<StaffOrderResponse[]> => {
    const response = await axiosClient.get<ApiResponse<StaffOrderResponse[]>>('/staff/orders');
    return response.data.data;
  },

  getOrderById: async (id: number): Promise<StaffOrderResponse> => {
    const response = await axiosClient.get<ApiResponse<StaffOrderResponse>>(`/staff/orders/${id}`);
    return response.data.data;
  },

  createOrder: async (data: StaffCreateOrderRequest): Promise<StaffOrderResponse> => {
    const response = await axiosClient.post<ApiResponse<StaffOrderResponse>>('/staff/orders', data);
    return response.data.data;
  },

  updateOrderStatus: async (id: number, data: StaffUpdateOrderStatusRequest): Promise<StaffOrderResponse> => {
    const response = await axiosClient.patch<ApiResponse<StaffOrderResponse>>(`/staff/orders/${id}/status`, data);
    return response.data.data;
  }
};
