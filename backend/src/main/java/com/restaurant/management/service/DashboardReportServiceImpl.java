package com.restaurant.management.service;

import com.restaurant.management.dto.report.DailyRevenue;
import com.restaurant.management.dto.report.DashboardSummaryResponse;
import com.restaurant.management.dto.report.OrderReportResponse;
import com.restaurant.management.dto.report.ReservationReportResponse;
import com.restaurant.management.dto.report.RevenueReportResponse;
import com.restaurant.management.dto.report.TopFoodResponse;
import com.restaurant.management.entity.order.OrderStatus;
import com.restaurant.management.entity.payment.Payment;
import com.restaurant.management.repository.OrderItemRepository;
import com.restaurant.management.repository.OrderRepository;
import com.restaurant.management.repository.PaymentRepository;
import com.restaurant.management.repository.ReservationRepository;
import com.restaurant.management.repository.RestaurantTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardReportServiceImpl implements DashboardReportService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final RestaurantTableRepository tableRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryResponse getDashboardSummary() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);

        BigDecimal totalRevenue = paymentRepository.sumTotalRevenue();
        Long totalOrders = orderRepository.count();
        Long paidOrders = orderRepository.countByOrderStatus(OrderStatus.COMPLETED); // Or based on payment
        Long pendingOrders = orderRepository.countByOrderStatus(OrderStatus.PENDING);
        Long cancelledOrders = orderRepository.countByOrderStatus(OrderStatus.CANCELLED);

        BigDecimal todayRevenue = paymentRepository.sumRevenueByDateRange(todayStart, todayEnd);
        Long todayOrders = orderRepository.countByDateRange(todayStart, todayEnd);

        Long totalReservations = reservationRepository.countTotalReservations();
        Long pendingReservations = reservationRepository.countPendingReservations();
        Long availableTables = tableRepository.countAvailableTables();

        return DashboardSummaryResponse.builder()
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .totalOrders(totalOrders != null ? totalOrders : 0L)
                .paidOrders(paidOrders != null ? paidOrders : 0L)
                .pendingOrders(pendingOrders != null ? pendingOrders : 0L)
                .cancelledOrders(cancelledOrders != null ? cancelledOrders : 0L)
                .todayRevenue(todayRevenue != null ? todayRevenue : BigDecimal.ZERO)
                .todayOrders(todayOrders != null ? todayOrders : 0L)
                .totalReservations(totalReservations != null ? totalReservations : 0L)
                .pendingReservations(pendingReservations != null ? pendingReservations : 0L)
                .availableTables(availableTables != null ? availableTables : 0L)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public RevenueReportResponse getRevenueReport(LocalDateTime fromDate, LocalDateTime toDate) {
        LocalDateTime start = fromDate != null ? fromDate : LocalDateTime.now().minusDays(30);
        LocalDateTime end = toDate != null ? toDate : LocalDateTime.now();
        validateDateRange(start, end);

        BigDecimal totalRevenue = paymentRepository.sumRevenueByDateRange(start, end);
        Long totalPaidPayments = paymentRepository.countPaidPaymentsByDateRange(start, end);

        List<Payment> payments = paymentRepository.findPaidPaymentsByDateRange(start, end);
        Map<LocalDate, BigDecimal> dailyMap = new HashMap<>();
        for (Payment p : payments) {
            LocalDate date = p.getCreatedAt().toLocalDate();
            dailyMap.put(date, dailyMap.getOrDefault(date, BigDecimal.ZERO).add(p.getAmount()));
        }

        List<DailyRevenue> dailyRevenue = dailyMap.entrySet().stream()
                .map(e -> new DailyRevenue(e.getKey(), e.getValue()))
                .sorted((a, b) -> a.getDate().compareTo(b.getDate()))
                .collect(Collectors.toList());

        return RevenueReportResponse.builder()
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .totalPaidPayments(totalPaidPayments != null ? totalPaidPayments : 0L)
                .totalInvoices(totalPaidPayments != null ? totalPaidPayments : 0L) // Assuming 1 invoice per payment
                .dailyRevenue(dailyRevenue)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderReportResponse getOrderReport(LocalDateTime fromDate, LocalDateTime toDate, OrderStatus status) {
        LocalDateTime start = fromDate != null ? fromDate : LocalDateTime.now().minusDays(30);
        LocalDateTime end = toDate != null ? toDate : LocalDateTime.now();
        validateDateRange(start, end);

        Long totalOrders;
        BigDecimal totalAmount;
        Map<String, Long> byStatus = new HashMap<>();

        if (status != null) {
            totalOrders = orderRepository.countByDateRangeAndStatus(start, end, status);
            totalAmount = orderRepository.sumTotalAmountByDateRangeAndStatus(start, end, status);
            byStatus.put(status.name(), totalOrders != null ? totalOrders : 0L);
        } else {
            totalOrders = orderRepository.countByDateRange(start, end);
            totalAmount = orderRepository.sumTotalAmountByDateRange(start, end);
            List<Object[]> statusCounts = orderRepository.countGroupedByStatus(start, end);
            for (Object[] row : statusCounts) {
                OrderStatus os = (OrderStatus) row[0];
                Long count = (Long) row[1];
                byStatus.put(os.name(), count);
            }
        }

        return OrderReportResponse.builder()
                .totalOrders(totalOrders != null ? totalOrders : 0L)
                .byStatus(byStatus)
                .totalAmount(totalAmount != null ? totalAmount : BigDecimal.ZERO)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopFoodResponse> getTopFoods(LocalDateTime fromDate, LocalDateTime toDate, Integer limit) {
        LocalDateTime start = fromDate != null ? fromDate : LocalDateTime.now().minusDays(30);
        LocalDateTime end = toDate != null ? toDate : LocalDateTime.now();
        validateDateRange(start, end);
        int max = (limit != null && limit > 0) ? limit : 10;

        List<Object[]> topFoods = orderItemRepository.getTopFoods(start, end, PageRequest.of(0, max));
        List<TopFoodResponse> result = new ArrayList<>();
        for (Object[] row : topFoods) {
            String name = (String) row[0];
            Long qty = ((Number) row[1]).longValue();
            BigDecimal rev = (BigDecimal) row[2];
            result.add(new TopFoodResponse(name, qty, rev));
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationReportResponse getReservationReport(LocalDateTime fromDate, LocalDateTime toDate) {
        LocalDateTime start = fromDate != null ? fromDate : LocalDateTime.now().minusDays(30);
        LocalDateTime end = toDate != null ? toDate : LocalDateTime.now();
        validateDateRange(start, end);

        Long totalReservations = reservationRepository.countReservationsByDateRange(start, end);
        Long totalGuests = reservationRepository.sumGuestsByDateRange(start, end);

        Map<String, Long> byStatus = new HashMap<>();
        List<Object[]> statusCounts = reservationRepository.countGroupedByStatus(start, end);
        for (Object[] row : statusCounts) {
            Enum<?> os = (Enum<?>) row[0];
            Long count = (Long) row[1];
            byStatus.put(os.name(), count);
        }

        return ReservationReportResponse.builder()
                .totalReservations(totalReservations != null ? totalReservations : 0L)
                .byStatus(byStatus)
                .totalGuests(totalGuests != null ? totalGuests : 0L)
                .build();
    }

    private void validateDateRange(LocalDateTime from, LocalDateTime to) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("fromDate must be before or equal to toDate");
        }
    }
}
