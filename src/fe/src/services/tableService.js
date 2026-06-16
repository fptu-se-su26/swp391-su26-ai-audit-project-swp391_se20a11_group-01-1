import axiosInstance from './axiosInstance';

/** GET /api/tables */
export const getAllTables = () => axiosInstance.get('/tables');

/** GET /api/tables/available */
export const getAvailableTables = () => axiosInstance.get('/tables/available');

/** POST /api/tables — Admin */
export const createTable = (data) => axiosInstance.post('/tables', data);

/** PUT /api/tables/{id} — Admin */
export const updateTable = (id, data) => axiosInstance.put(`/tables/${id}`, data);

/** PUT /api/tables/{id}/status — Admin/Staff */
export const updateTableStatus = (id, status) =>
  axiosInstance.put(`/tables/${id}/status?status=${status}`);

/** DELETE /api/tables/{id} — Admin */
export const deleteTable = (id) => axiosInstance.delete(`/tables/${id}`);
