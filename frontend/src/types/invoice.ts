import type { OrderDetailResponse } from './order';
import type { PaymentResponse } from './payment';

export interface InvoiceResponse {
  id: number;
  paymentId: number;
  invoiceNumber: string;
  taxAmount: number;
  totalAmount: number;
  issuedAt: string;
}

export interface InvoiceDetailResponse {
  id: number;
  invoiceNumber: string;
  taxAmount: number;
  totalAmount: number;
  issuedAt: string;
  payment: PaymentResponse;
  order: OrderDetailResponse;
}
