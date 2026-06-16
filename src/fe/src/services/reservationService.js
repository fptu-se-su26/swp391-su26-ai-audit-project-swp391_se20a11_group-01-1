import axiosInstance from './axiosInstance';

/** POST /api/reservations */
export const createReservation = (data) => axiosInstance.post('/reservations', data);

/** GET /api/reservations/my */
export const getMyReservations = () => axiosInstance.get('/reservations/my');

/** GET /api/reservations — Admin/Staff */
export const getAllReservations = () => axiosInstance.get('/reservations');

/** PUT /api/reservations/{id}/status */
export const updateReservationStatus = (id, status) =>
  axiosInstance.put(`/reservations/${id}/status?status=${status}`);

/** DELETE /api/reservations/{id} */
export const cancelReservation = (id) => axiosInstance.delete(`/reservations/${id}`);
