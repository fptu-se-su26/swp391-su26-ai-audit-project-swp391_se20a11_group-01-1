import API from './api';

export const getAllVouchers = async () => {
  const response = await API.get('/vouchers');
  return response.data;
};

export const createVoucher = async (voucherData) => {
  const response = await API.post('/vouchers', voucherData);
  return response.data;
};

export const updateVoucher = async (id, voucherData) => {
  const response = await API.put(`/vouchers/${id}`, voucherData);
  return response.data;
};

export const deleteVoucher = async (id) => {
  const response = await API.delete(`/vouchers/${id}`);
  return response.data;
};

export const validateVoucher = async (code, orderTotal) => {
  const response = await API.post('/vouchers/validate', { code, orderTotal });
  return response.data;
};
