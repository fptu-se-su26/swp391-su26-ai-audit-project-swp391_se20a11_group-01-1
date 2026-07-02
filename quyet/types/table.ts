export type TableStatus = 'AVAILABLE' | 'RESERVED' | 'OCCUPIED' | 'INACTIVE';

export interface TableResponse {
  id: number;
  tableNumber: string;
  capacity: number;
  status: TableStatus;
  location: string;
  createdAt: string;
  updatedAt: string;
}
