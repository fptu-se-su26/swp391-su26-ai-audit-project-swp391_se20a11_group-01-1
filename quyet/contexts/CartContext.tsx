import React, { createContext, useState, useEffect, useCallback } from 'react';
import type { ReactNode } from 'react';
import { cartApi } from '../api/cartApi';
import type { CartResponse } from '../types/cart';
import { useAuth } from '../hooks/useAuth';
import { getApiErrorMessage } from '../utils/getApiErrorMessage';

export interface CartContextType {
  cart: CartResponse | null;
  isLoading: boolean;
  error: string | null;
  itemCount: number;
  subtotal: number;
  loadCart: () => Promise<void>;
  addItem: (foodItemId: number, quantity?: number) => Promise<void>;
  updateQuantity: (itemId: number, quantity: number) => Promise<void>;
  removeItem: (itemId: number) => Promise<void>;
  clearCart: () => Promise<void>;
}

// eslint-disable-next-line react-refresh/only-export-components
export const CartContext = createContext<CartContextType | undefined>(undefined);

export const CartProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const { isAuthenticated, user } = useAuth();
  const [cart, setCart] = useState<CartResponse | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const isCustomer = isAuthenticated && user?.roles?.includes('ROLE_CUSTOMER');

  const loadCart = useCallback(async () => {
    if (!isCustomer) return;
    setIsLoading(true);
    setError(null);
    try {
      const data = await cartApi.getCart();
      setCart(data);
    } catch (err) {
      setError(getApiErrorMessage(err, 'Không thể tải giỏ hàng.'));
    } finally {
      setIsLoading(false);
    }
  }, [isCustomer]);

  // Load cart initially and when user changes
  useEffect(() => {
    if (isCustomer) {
      // eslint-disable-next-line react-hooks/set-state-in-effect
      loadCart();
    } else {
      setCart(null); // Reset when logged out or not customer
    }
  }, [isCustomer, loadCart]);

  const addItem = async (foodItemId: number, quantity: number = 1) => {
    if (!isCustomer) return;
    setIsLoading(true);
    setError(null);
    try {
      const data = await cartApi.addItem({ foodItemId, quantity });
      setCart(data);
    } catch (err) {
      const msg = getApiErrorMessage(err, 'Lỗi khi thêm món vào giỏ.');
      setError(msg);
      throw new Error(msg, { cause: err }); // rethrow for component to handle if needed
    } finally {
      setIsLoading(false);
    }
  };

  const updateQuantity = async (itemId: number, quantity: number) => {
    setIsLoading(true);
    setError(null);
    try {
      const data = await cartApi.updateItemQuantity(itemId, { quantity });
      setCart(data);
    } catch (err) {
      const msg = getApiErrorMessage(err, 'Lỗi khi cập nhật số lượng.');
      setError(msg);
      throw new Error(msg, { cause: err });
    } finally {
      setIsLoading(false);
    }
  };

  const removeItem = async (itemId: number) => {
    setIsLoading(true);
    setError(null);
    try {
      const data = await cartApi.removeItem(itemId);
      setCart(data);
    } catch (err) {
      const msg = getApiErrorMessage(err, 'Lỗi khi xóa món.');
      setError(msg);
      throw new Error(msg, { cause: err });
    } finally {
      setIsLoading(false);
    }
  };

  const clearCart = async () => {
    setIsLoading(true);
    setError(null);
    try {
      await cartApi.clearCart();
      setCart(null); // Wait, clearing the cart returns void, so the cart is empty. We should probably set it to empty cart instead of null, or null is fine. Let's load cart just to be sure or set items to [].
      await loadCart();
    } catch (err) {
      const msg = getApiErrorMessage(err, 'Lỗi khi xóa giỏ hàng.');
      setError(msg);
      throw new Error(msg, { cause: err });
    } finally {
      setIsLoading(false);
    }
  };

  const itemCount = cart?.items.reduce((total, item) => total + item.quantity, 0) || 0;
  const subtotal = cart?.totalAmount || 0;

  return (
    <CartContext.Provider value={{
      cart,
      isLoading,
      error,
      itemCount,
      subtotal,
      loadCart,
      addItem,
      updateQuantity,
      removeItem,
      clearCart
    }}>
      {children}
    </CartContext.Provider>
  );
};
