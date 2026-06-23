import type { OrderStatus } from '../types/order';

export const getOrderStatusLabel = (status: OrderStatus): string => {
  switch (status) {
    case 'PENDING':
      return 'Chờ xác nhận';
    case 'PENDING_PAYMENT':
      return 'Chờ thanh toán';
    case 'CONFIRMED':
      return 'Đã xác nhận';
    case 'PREPARING':
      return 'Đang chuẩn bị';
    case 'READY':
      return 'Sẵn sàng';
    case 'COMPLETED':
      return 'Hoàn thành';
    case 'CANCELLED':
      return 'Đã hủy';
    default:
      return status;
  }
};

export const getOrderStatusColor = (status: OrderStatus): string => {
  switch (status) {
    case 'PENDING':
    case 'PENDING_PAYMENT':
      return '#f39c12'; // Orange
    case 'CONFIRMED':
    case 'PREPARING':
      return '#3498db'; // Blue
    case 'READY':
      return '#8e44ad'; // Purple
    case 'COMPLETED':
      return '#2ecc71'; // Green
    case 'CANCELLED':
      return '#e74c3c'; // Red
    default:
      return '#95a5a6'; // Gray
  }
};

export const getOrderStatusBadgeClass = (status: OrderStatus): string => {
  switch (status) {
    case 'PENDING': return 'badge-warning';
    case 'PENDING_PAYMENT': return 'badge-info';
    case 'CONFIRMED': return 'badge-primary';
    case 'PREPARING': return 'badge-secondary';
    case 'READY': return 'badge-success';
    case 'COMPLETED': return 'badge-dark';
    case 'CANCELLED': return 'badge-danger';
    default: return 'badge-default';
  }
};