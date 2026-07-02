export type OrderStatus = 'PENDING' | 'PENDING_PAYMENT' | 'CONFIRMED' | 'PREPARING' | 'READY' | 'COMPLETED' | 'CANCELLED';

export type OrderType = 'DINE_IN' | 'TAKEAWAY' | 'DELIVERY';

export interface OrderItemResponse {
  id: number;
  foodItemId: number;
  foodName: string;
  quantity: number;
  unitPrice: number;
  note: string | null;
}

export interface OrderResponse {
  id: number;
  customerId: number;
  tableId: number | null;
  orderStatus: OrderStatus;
  orderType: OrderType;
  subTotal: number;
  discountAmount: number;
  totalAmount: number;
  note: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface OrderDetailResponse extends OrderResponse {
  couponCode: string | null;
  items: OrderItemResponse[];
}

export interface CheckoutRequest {
  orderType: OrderType;
  tableId?: number | null;
  couponCode?: string | null;
  note?: string | null;
}

export interface CancelOrderRequest {
  reason: string;
}
