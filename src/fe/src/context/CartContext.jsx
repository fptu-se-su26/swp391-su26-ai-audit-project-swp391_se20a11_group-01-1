import { createContext, useContext, useState } from 'react';

const CartContext = createContext(null);

export const CartProvider = ({ children }) => {
  const [items, setItems] = useState([]);
  const [coupon, setCoupon] = useState(null);

  const addItem = (food, quantity = 1, note = '') => {
    setItems((prev) => {
      const existing = prev.find((i) => i.food.id === food.id);
      if (existing) {
        return prev.map((i) =>
          i.food.id === food.id ? { ...i, quantity: i.quantity + quantity } : i
        );
      }
      return [...prev, { food, quantity, note }];
    });
  };

  const removeItem = (foodId) => {
    setItems((prev) => prev.filter((i) => i.food.id !== foodId));
  };

  const updateQuantity = (foodId, quantity) => {
    if (quantity <= 0) { removeItem(foodId); return; }
    setItems((prev) =>
      prev.map((i) => (i.food.id === foodId ? { ...i, quantity } : i))
    );
  };

  const clearCart = () => {
    setItems([]);
    setCoupon(null);
  };

  const totalAmount = items.reduce(
    (sum, i) => sum + Number(i.food.price) * i.quantity, 0
  );

  const discountAmount = coupon
    ? coupon.discountPercent
      ? (totalAmount * coupon.discountPercent) / 100
      : Number(coupon.discountAmount || 0)
    : 0;

  const finalAmount = Math.max(totalAmount - discountAmount, 0);

  const totalItems = items.reduce((sum, i) => sum + i.quantity, 0);

  return (
    <CartContext.Provider value={{
      items, addItem, removeItem, updateQuantity, clearCart,
      coupon, setCoupon,
      totalAmount, discountAmount, finalAmount, totalItems
    }}>
      {children}
    </CartContext.Provider>
  );
};

export const useCart = () => {
  const ctx = useContext(CartContext);
  if (!ctx) throw new Error('useCart must be used inside CartProvider');
  return ctx;
};
