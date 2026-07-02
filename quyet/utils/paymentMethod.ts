import type { PaymentMethod } from '../types/payment';

export const getPaymentMethodLabel = (method: PaymentMethod): string => {
  switch (method) {
    case 'CASH':
      return 'Tiền mặt';
    case 'QR':
      return 'Chuyển khoản QR';
    case 'ONLINE_SIMULATION':
      return 'Thanh toán trực tuyến (Mô phỏng)';
    default:
      return method;
  }
};
