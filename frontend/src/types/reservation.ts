import type { TableResponse } from './table';

export type ReservationStatus = 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'COMPLETED' | 'NO_SHOW';

export interface ReservationRequest {
  tableId?: number;
  reservationTime: string;
  guestCount: number;
  specialRequest?: string;
}

export interface CancelReservationRequest {
  reason?: string;
}

export interface ReservationResponse {
  id: number;
  customerId: number;
  tableId: number;
  reservationTime: string;
  guestCount: number;
  status: ReservationStatus;
  specialRequest?: string;
  createdAt: string;
  updatedAt?: string;
}

export interface ReservationDetailResponse {
  id: number;
  customerId: number;
  customerName: string;
  table: TableResponse;
  reservationTime: string;
  guestCount: number;
  status: ReservationStatus;
  specialRequest?: string;
  createdAt: string;
  updatedAt?: string;
}
