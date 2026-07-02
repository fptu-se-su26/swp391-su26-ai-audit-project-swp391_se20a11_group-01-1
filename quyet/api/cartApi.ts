import axiosClient from './axiosClient';
import type { CartResponse, AddCartItemRequest, UpdateCartItemRequest } from '../types/cart';
import type { ApiResponse } from '../types/auth';

export const cartApi = {
  getCart: async (): Promise<CartResponse> => {
    const response = await axiosClient.get<ApiResponse<CartResponse>>('/cart');
    return response.data.data;
  },

  addItem: async (request: AddCartItemRequest): Promise<CartResponse> => {
    const response = await axiosClient.post<ApiResponse<CartResponse>>('/cart/items', request);
    return response.data.data;
  },

  updateItemQuantity: async (itemId: number, request: UpdateCartItemRequest): Promise<CartResponse> => {
    const response = await axiosClient.put<ApiResponse<CartResponse>>(`/cart/items/${itemId}`, request);
    return response.data.data;
  },

  removeItem: async (itemId: number): Promise<CartResponse> => {
    const response = await axiosClient.delete<ApiResponse<CartResponse>>(`/cart/items/${itemId}`);
    return response.data.data;
  },

  clearCart: async (): Promise<void> => {
    await axiosClient.delete<ApiResponse<void>>('/cart');
  }
};
