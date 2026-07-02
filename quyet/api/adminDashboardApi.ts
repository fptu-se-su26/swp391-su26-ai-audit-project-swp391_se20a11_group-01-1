import axiosClient from './axiosClient';
import type { ApiResponse } from '../types/auth';
import type { DashboardSummaryResponse } from '../types/report';

export const adminDashboardApi = {
  getSummary: async (): Promise<DashboardSummaryResponse> => {
    const response = await axiosClient.get<ApiResponse<DashboardSummaryResponse>>('/admin/dashboard/summary');
    return response.data.data;
  }
};
