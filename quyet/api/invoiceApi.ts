import axiosClient from './axiosClient';
import type { InvoiceResponse, InvoiceDetailResponse } from '../types/invoice';
import type { ApiResponse } from '../types/auth';

export const invoiceApi = {
  generateInvoice: async (paymentId: number): Promise<InvoiceResponse> => {
    const response = await axiosClient.post<ApiResponse<InvoiceResponse>>(`/api/invoices/generate/${paymentId}`);
    return response.data.data;
  },

  getInvoiceById: async (invoiceId: number): Promise<InvoiceDetailResponse> => {
    const response = await axiosClient.get<ApiResponse<InvoiceDetailResponse>>(`/api/invoices/${invoiceId}`);
    return response.data.data;
  },

  getInvoiceByPaymentId: async (paymentId: number): Promise<InvoiceDetailResponse> => {
    const response = await axiosClient.get<ApiResponse<InvoiceDetailResponse>>(`/api/invoices/payment/${paymentId}`);
    return response.data.data;
  }
};
