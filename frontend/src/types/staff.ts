import type { OrderStatus, OrderType, OrderItemResponse } from './order';

export interface StaffOrderItemRequest {
  foodItemId: number;
  quantity: number;
  note?: string;
}

export interface StaffCreateOrderRequest {
  orderType: OrderType;
  tableId?: number | null;
  note?: string;
  items: StaffOrderItemRequest[];
}

export interface StaffOrderResponse {
  id: number;
  customerId?: number;
  tableId?: number;
  orderStatus: OrderStatus;
  orderType: OrderType;
  subTotal: number;
  discountAmount: number;
  totalAmount: number;
  note?: string;
  createdAt: string;
  updatedAt: string;
  items: OrderItemResponse[];
}

export interface StaffUpdateOrderStatusRequest {
  status: OrderStatus;
}
