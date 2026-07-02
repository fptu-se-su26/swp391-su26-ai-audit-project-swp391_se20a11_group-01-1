import type { ReservationStatus } from '../types/reservation';

export const getReservationStatusLabel = (status: ReservationStatus): string => {
  switch (status) {
    case 'PENDING':
      return 'Chờ xác nhận';
    case 'CONFIRMED':
      return 'Đã xác nhận';
    case 'CANCELLED':
      return 'Đã hủy';
    case 'COMPLETED':
      return 'Hoàn thành';
    case 'NO_SHOW':
      return 'Khách không đến';
    default:
      return status;
  }
};

export const getReservationStatusColor = (status: ReservationStatus): string => {
  switch (status) {
    case 'PENDING':
      return '#f39c12'; // orange
    case 'CONFIRMED':
      return '#3498db'; // blue
    case 'CANCELLED':
      return '#e74c3c'; // red
    case 'COMPLETED':
      return '#27ae60'; // green
    case 'NO_SHOW':
      return '#95a5a6'; // gray
    default:
      return '#34495e';
  }
};
