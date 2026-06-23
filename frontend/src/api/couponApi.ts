import axiosClient from './axiosClient';
import type { CouponValidationRequest, CouponValidationResponse } from '../types/coupon';
import type { ApiResponse } from '../types/auth';

export const couponApi = {
  validateCoupon: async (request: CouponValidationRequest): Promise<CouponValidationResponse> => {
    const response = await axiosClient.post<ApiResponse<CouponValidationResponse>>('/coupons/validate', request);
    return response.data.data;
  }
};
