import axiosClient from './axiosClient';
import type { PaymentConfirmRequest, PaymentResponse } from '../types/payment';
import type { ApiResponse } from '../types/auth';

export const paymentApi = {
  confirmPayment: async (orderId: number, data: PaymentConfirmRequest): Promise<PaymentResponse> => {
    const response = await axiosClient.post<ApiResponse<PaymentResponse>>(`/api/payments/${orderId}/confirm`, data);
    return response.data.data;
  },

  getPaymentsByOrderId: async (orderId: number): Promise<PaymentResponse[]> => {
    const response = await axiosClient.get<ApiResponse<PaymentResponse[]>>(`/api/payments/${orderId}`);
    return response.data.data;
  }
};
