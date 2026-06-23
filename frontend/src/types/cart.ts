export interface CartItemResponse {
  id: number;
  foodItemId: number;
  foodName: string;
  foodImageUrl: string;
  quantity: number;
  unitPrice: number;
  subTotal: number;
}

export interface CartResponse {
  id: number;
  userId: number;
  items: CartItemResponse[];
  totalAmount: number;
}

export interface AddCartItemRequest {
  foodItemId: number;
  quantity: number;
}

export interface UpdateCartItemRequest {
  quantity: number;
}
