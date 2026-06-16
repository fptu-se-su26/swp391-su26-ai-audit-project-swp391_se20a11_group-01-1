import axiosInstance from './axiosInstance';

/** GET /api/categories */
export const getAllCategories = () => axiosInstance.get('/categories');

/** GET /api/categories/{id} */
export const getCategoryById = (id) => axiosInstance.get(`/categories/${id}`);

/** POST /api/categories — Admin only */
export const createCategory = (data) => axiosInstance.post('/categories', data);

/** PUT /api/categories/{id} — Admin only */
export const updateCategory = (id, data) => axiosInstance.put(`/categories/${id}`, data);

/** DELETE /api/categories/{id} — Admin only */
export const deleteCategory = (id) => axiosInstance.delete(`/categories/${id}`);
