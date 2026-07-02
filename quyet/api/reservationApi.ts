import axiosClient from './axiosClient';
import type { 
  ReservationRequest, 
  ReservationResponse, 
  ReservationDetailResponse, 
  CancelReservationRequest 
} from '../types/reservation';
import type { TableResponse } from '../types/table';
import type { ApiResponse } from '../types/auth';

export const reservationApi = {
  createReservation: async (data: ReservationRequest): Promise<ReservationResponse> => {
    const response = await axiosClient.post<ApiResponse<ReservationResponse>>('/api/reservations', data);
    return response.data.data;
  },

  getMyReservations: async (): Promise<ReservationResponse[]> => {
    const response = await axiosClient.get<ApiResponse<ReservationResponse[]>>('/api/reservations');
    return response.data.data;
  },

  getReservationById: async (id: number): Promise<ReservationDetailResponse> => {
    const response = await axiosClient.get<ApiResponse<ReservationDetailResponse>>(`/api/reservations/${id}`);
    return response.data.data;
  },

  cancelReservation: async (id: number, data: CancelReservationRequest): Promise<void> => {
    await axiosClient.patch<ApiResponse<void>>(`/api/reservations/${id}/cancel`, data);
  },

  getAvailableTables: async (): Promise<TableResponse[]> => {
    // Backend doesn't have an endpoint for specific available times.
    // It only has GET /api/tables which returns all tables.
    const response = await axiosClient.get<ApiResponse<TableResponse[]>>('/api/tables');
    return response.data.data.filter(t => t.status !== 'INACTIVE'); // FE filtering to hide inactive tables
  }
};
