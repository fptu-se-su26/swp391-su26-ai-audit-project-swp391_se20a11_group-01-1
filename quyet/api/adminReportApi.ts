import axiosClient from './axiosClient';
import type { ApiResponse } from '../types/auth';
import type { 
  RevenueReportResponse, 
  OrderReportResponse, 
  ReservationReportResponse, 
  TopFoodResponse 
} from '../types/report';

export const adminReportApi = {
  getRevenueReport: async (fromDate?: string, toDate?: string): Promise<RevenueReportResponse> => {
    const params = new URLSearchParams();
    if (fromDate) params.append('fromDate', fromDate);
    if (toDate) params.append('toDate', toDate);
    
    const response = await axiosClient.get<ApiResponse<RevenueReportResponse>>(`/admin/reports/revenue?${params.toString()}`);
    return response.data.data;
  },

  getOrderReport: async (fromDate?: string, toDate?: string, status?: string): Promise<OrderReportResponse> => {
    const params = new URLSearchParams();
    if (fromDate) params.append('fromDate', fromDate);
    if (toDate) params.append('toDate', toDate);
    if (status) params.append('status', status);
    
    const response = await axiosClient.get<ApiResponse<OrderReportResponse>>(`/admin/reports/orders?${params.toString()}`);
    return response.data.data;
  },

  getTopFoods: async (fromDate?: string, toDate?: string, limit = 10): Promise<TopFoodResponse[]> => {
    const params = new URLSearchParams();
    if (fromDate) params.append('fromDate', fromDate);
    if (toDate) params.append('toDate', toDate);
    if (limit) params.append('limit', limit.toString());
    
    const response = await axiosClient.get<ApiResponse<TopFoodResponse[]>>(`/admin/reports/top-foods?${params.toString()}`);
    return response.data.data;
  },

  getReservationReport: async (fromDate?: string, toDate?: string): Promise<ReservationReportResponse> => {
    const params = new URLSearchParams();
    if (fromDate) params.append('fromDate', fromDate);
    if (toDate) params.append('toDate', toDate);
    
    const response = await axiosClient.get<ApiResponse<ReservationReportResponse>>(`/admin/reports/reservations?${params.toString()}`);
    return response.data.data;
  }
};
