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
