import axiosInstance from './axiosInstance';

/** GET /api/foods */
export const getAllFoods = (params) => axiosInstance.get('/foods', { params });

/** GET /api/foods/{id} */
export const getFoodById = (id) => axiosInstance.get(`/foods/${id}`);

/** POST /api/foods — Admin only */
export const createFood = (data) => axiosInstance.post('/foods', data);

/** PUT /api/foods/{id} — Admin only */
export const updateFood = (id, data) => axiosInstance.put(`/foods/${id}`, data);

/** DELETE /api/foods/{id} — Admin only */
export const deleteFood = (id) => axiosInstance.delete(`/foods/${id}`);
