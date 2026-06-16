import axiosInstance from './axiosInstance';

/** GET /api/coupons — Admin */
export const getAllCoupons = () => axiosInstance.get('/coupons');

/** POST /api/coupons — Admin */
export const createCoupon = (data) => axiosInstance.post('/coupons', data);

/** GET /api/coupons/validate?code=XX&orderAmount=YY */
export const validateCoupon = (code, orderAmount) =>
  axiosInstance.get('/coupons/validate', { params: { code, orderAmount } });

/** DELETE /api/coupons/{id} — Admin */
export const deleteCoupon = (id) => axiosInstance.delete(`/coupons/${id}`);
