export interface DashboardSummaryResponse {
  totalRevenue: number;
  totalOrders: number;
  paidOrders: number;
  pendingOrders: number;
  cancelledOrders: number;
  todayRevenue: number;
  todayOrders: number;
  totalReservations: number;
  pendingReservations: number;
  availableTables: number;
}

export interface DailyRevenue {
  date: string;
  revenue: number;
}

export interface RevenueReportResponse {
  totalRevenue: number;
  totalPaidPayments: number;
  totalInvoices: number;
  dailyRevenue: DailyRevenue[];
}

export interface OrderReportResponse {
  totalOrders: number;
  byStatus: Record<string, number>;
  totalAmount: number;
}

export interface ReservationReportResponse {
  totalReservations: number;
  byStatus: Record<string, number>;
  totalGuests: number;
}

export interface TopFoodResponse {
  foodName: string;
  totalQuantity: number;
  totalRevenue: number;
}
