export type PaymentMethod = 'CASH' | 'QR' | 'ONLINE_SIMULATION';
export type PaymentStatus = 'PENDING' | 'PAID' | 'FAILED';

export interface PaymentConfirmRequest {
  paymentMethod: PaymentMethod;
  amount: number;
  paymentReference?: string;
}

export interface PaymentResponse {
  id: number;
  orderId: number;
  paymentMethod: PaymentMethod;
  paymentStatus: PaymentStatus;
  amount: number;
  transactionCode?: string;
  createdAt: string;
  updatedAt?: string;
}
