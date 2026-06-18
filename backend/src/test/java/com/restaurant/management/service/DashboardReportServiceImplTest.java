package com.restaurant.management.service;

import com.restaurant.management.dto.report.DashboardSummaryResponse;
import com.restaurant.management.dto.report.OrderReportResponse;
import com.restaurant.management.dto.report.ReservationReportResponse;
import com.restaurant.management.dto.report.RevenueReportResponse;
import com.restaurant.management.dto.report.TopFoodResponse;
import com.restaurant.management.entity.order.OrderStatus;
import com.restaurant.management.entity.payment.Payment;
import com.restaurant.management.entity.reservation.ReservationStatus;
import com.restaurant.management.repository.OrderItemRepository;
import com.restaurant.management.repository.OrderRepository;
import com.restaurant.management.repository.PaymentRepository;
import com.restaurant.management.repository.ReservationRepository;
import com.restaurant.management.repository.RestaurantTableRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardReportServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private RestaurantTableRepository tableRepository;
    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private DashboardReportServiceImpl dashboardReportService;

    @Test
    void getDashboardSummary_success() {
        when(paymentRepository.sumTotalRevenue()).thenReturn(new BigDecimal("1000.00"));
        when(orderRepository.count()).thenReturn(100L);
        when(orderRepository.countByOrderStatus(OrderStatus.COMPLETED)).thenReturn(80L);
        when(orderRepository.countByOrderStatus(OrderStatus.PENDING)).thenReturn(10L);
        when(orderRepository.countByOrderStatus(OrderStatus.CANCELLED)).thenReturn(10L);
        when(paymentRepository.sumRevenueByDateRange(any(), any())).thenReturn(new BigDecimal("200.00"));
        when(orderRepository.countByDateRange(any(), any())).thenReturn(5L);
        when(reservationRepository.countTotalReservations()).thenReturn(50L);
        when(reservationRepository.countPendingReservations()).thenReturn(5L);
        when(tableRepository.countAvailableTables()).thenReturn(15L);

        DashboardSummaryResponse response = dashboardReportService.getDashboardSummary();

        assertNotNull(response);
        assertEquals(new BigDecimal("1000.00"), response.getTotalRevenue());
        assertEquals(100L, response.getTotalOrders());
        assertEquals(80L, response.getPaidOrders());
        assertEquals(10L, response.getPendingOrders());
        assertEquals(15L, response.getAvailableTables());
    }

    @Test
    void getRevenueReport_success() {
        LocalDateTime start = LocalDateTime.now().minusDays(5);
        LocalDateTime end = LocalDateTime.now();

        when(paymentRepository.sumRevenueByDateRange(start, end)).thenReturn(new BigDecimal("500.00"));
        when(paymentRepository.countPaidPaymentsByDateRange(start, end)).thenReturn(10L);
        
        Payment p = new Payment();
        p.setAmount(new BigDecimal("500.00"));
        p.setCreatedAt(LocalDateTime.now());
        when(paymentRepository.findPaidPaymentsByDateRange(start, end)).thenReturn(List.of(p));

        RevenueReportResponse response = dashboardReportService.getRevenueReport(start, end);

        assertNotNull(response);
        assertEquals(new BigDecimal("500.00"), response.getTotalRevenue());
        assertEquals(10L, response.getTotalPaidPayments());
        assertEquals(10L, response.getTotalInvoices());
        assertEquals(1, response.getDailyRevenue().size());
    }

    @Test
    void getOrderReport_byStatus() {
        LocalDateTime start = LocalDateTime.now().minusDays(5);
        LocalDateTime end = LocalDateTime.now();

        when(orderRepository.countByDateRangeAndStatus(start, end, OrderStatus.COMPLETED)).thenReturn(5L);
        when(orderRepository.sumTotalAmountByDateRangeAndStatus(start, end, OrderStatus.COMPLETED)).thenReturn(new BigDecimal("200.00"));

        OrderReportResponse response = dashboardReportService.getOrderReport(start, end, OrderStatus.COMPLETED);

        assertNotNull(response);
        assertEquals(5L, response.getTotalOrders());
        assertEquals(new BigDecimal("200.00"), response.getTotalAmount());
        assertEquals(5L, response.getByStatus().get(OrderStatus.COMPLETED.name()));
    }

    @Test
    void getTopFoods_sorted() {
        LocalDateTime start = LocalDateTime.now().minusDays(5);
        LocalDateTime end = LocalDateTime.now();

        Object[] row1 = new Object[]{"Pizza", 20L, new BigDecimal("400.00")};
        Object[] row2 = new Object[]{"Burger", 15L, new BigDecimal("150.00")};

        when(orderItemRepository.getTopFoods(eq(start), eq(end), any(PageRequest.class)))
                .thenReturn(List.of(row1, row2));

        List<TopFoodResponse> response = dashboardReportService.getTopFoods(start, end, 10);

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("Pizza", response.get(0).getFoodName());
        assertEquals(20L, response.get(0).getTotalQuantity());
    }

    @Test
    void getReservationReport_byStatus() {
        LocalDateTime start = LocalDateTime.now().minusDays(5);
        LocalDateTime end = LocalDateTime.now();

        when(reservationRepository.countReservationsByDateRange(start, end)).thenReturn(10L);
        when(reservationRepository.sumGuestsByDateRange(start, end)).thenReturn(40L);
        
        Object[] row1 = new Object[]{ReservationStatus.CONFIRMED, 6L};
        Object[] row2 = new Object[]{ReservationStatus.CANCELLED, 4L};
        when(reservationRepository.countGroupedByStatus(start, end)).thenReturn(List.of(row1, row2));

        ReservationReportResponse response = dashboardReportService.getReservationReport(start, end);

        assertNotNull(response);
        assertEquals(10L, response.getTotalReservations());
        assertEquals(40L, response.getTotalGuests());
        assertEquals(6L, response.getByStatus().get("CONFIRMED"));
    }

    @Test
    void invalidDateRange_fails() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().minusDays(5);

        assertThrows(IllegalArgumentException.class, () -> {
            dashboardReportService.getRevenueReport(start, end);
        });
    }
}
