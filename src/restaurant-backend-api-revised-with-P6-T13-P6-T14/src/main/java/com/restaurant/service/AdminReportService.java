package com.restaurant.service;

import com.restaurant.dto.admin.*;
import com.restaurant.entity.OrderItem;
import com.restaurant.model.*;
import com.restaurant.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminReportService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderReportRepository orderReportRepository;
    private final PaymentReportRepository paymentReportRepository;
    private final FoodRepository foodRepository;
    private final CouponRepository couponRepository;
    private final OrderItemRepository orderItemRepository;

    public DashboardResponse dashboard() {
        return DashboardResponse.builder()
                .totalUsers(userRepository.count())
                .totalOrders(orderRepository.count())
                .pendingOrders(orderReportRepository.countByOrderStatus(OrderStatus.PENDING))
                .completedOrders(orderReportRepository.countByOrderStatus(OrderStatus.COMPLETED))
                .paidPayments(paymentReportRepository.countByPaymentStatus(PaymentStatus.PAID))
                .totalRevenue(paymentReportRepository.sumPaidAmount())
                .availableFoods(foodRepository.findByAvailabilityStatus(AvailabilityStatus.AVAILABLE).size())
                .activeCoupons(couponRepository.findByStatus(CouponStatus.ACTIVE).size())
                .build();
    }

    public RevenueResponse revenue(LocalDate from, LocalDate to) {
        LocalDateTime fromDate = from == null ? null : from.atStartOfDay();
        LocalDateTime toDate = to == null ? null : to.atTime(LocalTime.MAX);
        return RevenueResponse.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .totalRevenue(paymentReportRepository.sumPaidAmountBetween(fromDate, toDate))
                .paidPaymentCount(paymentReportRepository.countPaidBetween(fromDate, toDate))
                .completedOrderCount(orderReportRepository.countCompletedBetween(fromDate, toDate))
                .build();
    }

    public List<BestSellingFoodResponse> bestSellingFoods(int limit) {
        int safeLimit = limit <= 0 ? 10 : Math.min(limit, 50);
        Map<Long, List<OrderItem>> grouped = orderItemRepository.findAll().stream()
                .filter(item -> item.getItemStatus() != OrderItemStatus.CANCELLED && item.getItemStatus() != OrderItemStatus.REJECTED)
                .collect(Collectors.groupingBy(item -> item.getFood().getFoodId()));

        return grouped.values().stream()
                .map(items -> {
                    OrderItem first = items.get(0);
                    long quantity = items.stream().mapToLong(OrderItem::getQuantity).sum();
                    BigDecimal revenue = items.stream()
                            .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return BestSellingFoodResponse.builder()
                            .foodId(first.getFood().getFoodId())
                            .foodName(first.getFood().getFoodName())
                            .soldQuantity(quantity)
                            .revenue(revenue)
                            .build();
                })
                .sorted(Comparator.comparing(BestSellingFoodResponse::getSoldQuantity).reversed())
                .limit(safeLimit)
                .toList();
    }
}
