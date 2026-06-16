import axiosInstance from './axiosInstance';

/** POST /api/orders */
export const createOrder = (data) => axiosInstance.post('/orders', data);

/** GET /api/orders/my */
export const getMyOrders = () => axiosInstance.get('/orders/my');

/** GET /api/orders/{id} */
export const getOrderById = (id) => axiosInstance.get(`/orders/${id}`);

/** GET /api/orders — Admin/Staff */
export const getAllOrders = () => axiosInstance.get('/orders');

/** PUT /api/orders/{id}/status */
export const updateOrderStatus = (id, status) =>
  axiosInstance.put(`/orders/${id}/status?status=${status}`);

/** PUT /api/orders/{id}/payment */
export const confirmPayment = (id) => axiosInstance.put(`/orders/${id}/payment`);

/** DELETE /api/orders/{id} */
export const cancelOrder = (id) => axiosInstance.delete(`/orders/${id}`);
