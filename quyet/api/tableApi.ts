import axiosClient from './axiosClient';
import type { TableResponse } from '../types/table';
import type { ApiResponse } from '../types/auth';

export const tableApi = {
  getAllTables: async (): Promise<TableResponse[]> => {
    const response = await axiosClient.get<ApiResponse<TableResponse[]>>('/tables');
    return response.data.data;
  }
};
