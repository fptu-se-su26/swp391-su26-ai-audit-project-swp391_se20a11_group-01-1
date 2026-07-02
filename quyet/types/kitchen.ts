import type { OrderStatus, OrderType, OrderItemResponse } from './order';

export interface KitchenOrderResponse {
  id: number;
  tableId?: number;
  orderStatus: OrderStatus;
  orderType: OrderType;
  note?: string;
  createdAt: string;
  updatedAt: string;
  items: OrderItemResponse[];
}

export interface KitchenUpdateOrderStatusRequest {
  status: OrderStatus;
}
