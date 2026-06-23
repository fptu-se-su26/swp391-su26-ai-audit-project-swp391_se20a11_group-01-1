import axiosClient from './axiosClient';
import type { ApiResponse } from '../types/auth';
import type { CouponResponse, CouponRequest, CouponStatus } from '../types/coupon';

export const adminCouponApi = {
  getCoupons: async (): Promise<CouponResponse[]> => {
    const response = await axiosClient.get<ApiResponse<CouponResponse[]>>('/admin/coupons');
    return response.data.data;
  },

  getCoupon: async (id: number): Promise<CouponResponse> => {
    const response = await axiosClient.get<ApiResponse<CouponResponse>>(`/admin/coupons/${id}`);
    return response.data.data;
  },

  createCoupon: async (data: CouponRequest): Promise<CouponResponse> => {
    const response = await axiosClient.post<ApiResponse<CouponResponse>>('/admin/coupons', data);
    return response.data.data;
  },

  updateCoupon: async (id: number, data: CouponRequest): Promise<CouponResponse> => {
    const response = await axiosClient.put<ApiResponse<CouponResponse>>(`/admin/coupons/${id}`, data);
    return response.data.data;
  },

  updateCouponStatus: async (id: number, status: CouponStatus): Promise<void> => {
    await axiosClient.patch<ApiResponse<void>>(`/admin/coupons/${id}/status`, { status });
  }
};
