export interface CouponValidationRequest {
  code: string;
  orderAmount: number;
}

export interface CouponValidationResponse {
  valid: boolean;
  discountAmount: number;
  finalAmount: number;
  couponCode: string;
}

export type DiscountType = 'PERCENTAGE' | 'FIXED_AMOUNT';
export type CouponStatus = 'ACTIVE' | 'INACTIVE' | 'EXPIRED';

export interface CouponResponse {
  id: number;
  code: string;
  name: string;
  description: string;
  discountType: DiscountType;
  discountValue: number;
  minOrderValue: number;
  maxDiscountAmount: number;
  startDate: string;
  endDate: string;
  usageLimit: number;
  usedCount: number;
  status: CouponStatus;
}

export interface CouponRequest {
  code: string;
  name?: string;
  description?: string;
  discountType: DiscountType;
  discountValue: number;
  minOrderValue: number;
  maxDiscountAmount?: number;
  startDate: string;
  endDate: string;
  usageLimit: number;
  status: CouponStatus;
}
