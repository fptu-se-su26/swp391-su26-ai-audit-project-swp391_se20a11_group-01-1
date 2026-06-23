export const getApiErrorMessage = (err: unknown, defaultMessage: string = 'Lỗi hệ thống. Vui lòng thử lại sau.'): string => {
  const errorObj = err as { response?: { data?: { message?: string } } };
  return errorObj?.response?.data?.message || defaultMessage;
};
