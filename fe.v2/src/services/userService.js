import axiosInstance from './axiosInstance';

/** GET /api/users — Admin only */
export const getAllUsers = () => axiosInstance.get('/users');

/** GET /api/users/{id} */
export const getUserById = (id) => axiosInstance.get(`/users/${id}`);

/** PUT /api/users/{id} — body: { name, email, phone, address } */
export const updateUser = (id, data) => axiosInstance.put(`/users/${id}`, data);

/** DELETE /api/users/{id} — Admin only */
export const deleteUser = (id) => axiosInstance.delete(`/users/${id}`);
