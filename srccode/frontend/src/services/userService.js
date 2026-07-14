import API from './api';

export const getAllUsers = async () => {
  const response = await API.get('/users');
  return response.data;
};

export const getStaffCustomers = async () => {
  const response = await API.get('/users/customers');
  return response.data;
};