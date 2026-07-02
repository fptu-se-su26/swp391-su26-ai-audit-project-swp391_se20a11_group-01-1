import type { PaymentStatus } from '../types/payment';

export const getPaymentStatusLabel = (status: PaymentStatus): string => {
  switch (status) {
    case 'PENDING':
      return 'Chờ thanh toán';
    case 'PAID':
      return 'Đã thanh toán';
    case 'FAILED':
      return 'Thanh toán thất bại';
    default:
      return status;
  }
};

export const getPaymentStatusColor = (status: PaymentStatus): string => {
  switch (status) {
    case 'PENDING':
      return '#f39c12'; // orange
    case 'PAID':
      return '#27ae60'; // green
    case 'FAILED':
      return '#c0392b'; // red
    default:
      return '#95a5a6'; // gray
  }
};
