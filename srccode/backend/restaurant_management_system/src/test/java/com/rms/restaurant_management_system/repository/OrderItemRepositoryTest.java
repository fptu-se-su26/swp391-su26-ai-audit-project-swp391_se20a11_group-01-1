package com.rms.restaurant_management_system.repository;

import com.rms.restaurant_management_system.entity.Order;
import com.rms.restaurant_management_system.entity.OrderItem;
import com.rms.restaurant_management_system.enums.OrderItemStatus;
import com.rms.restaurant_management_system.enums.OrderStatus;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(properties = {
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.jpa.properties.hibernate.generate_statistics=true"
})
class OrderItemRepositoryTest {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    void kitchenQueryFetchesOrdersAndFoodSnapshotsWithoutNPlusOne() {
        LocalDateTime baseTime = LocalDateTime.of(2026, 7, 22, 10, 0);
        Order firstOrder = order("ORD-1");
        Order secondOrder = order("ORD-2");
        addItem(firstOrder, "Soup", baseTime.plusMinutes(1));
        addItem(secondOrder, "Rice", baseTime.plusMinutes(2));

        entityManager.persist(firstOrder);
        entityManager.persist(secondOrder);
        entityManager.flush();
        entityManager.clear();

        Statistics statistics = entityManagerFactory
                .unwrap(SessionFactory.class)
                .getStatistics();
        statistics.clear();

        List<OrderItem> items = orderItemRepository
                .findByStatusInOrderByCreatedAtAsc(List.of(OrderItemStatus.CONFIRMED));

        List<String> orderCodes = items.stream()
                .map(item -> item.getOrder().getOrderCode())
                .toList();
        List<String> foodNames = items.stream()
                .map(OrderItem::getFoodName)
                .toList();

        assertEquals(2, items.size());
        assertNotEquals(items.get(0).getOrderItemId(), items.get(1).getOrderItemId());
        assertEquals(List.of("ORD-1", "ORD-2"), orderCodes);
        assertEquals(List.of("Soup", "Rice"), foodNames);
        assertTrue(items.get(0).getCreatedAt().isBefore(items.get(1).getCreatedAt()));
        assertEquals(1L, statistics.getPrepareStatementCount());
    }

    private Order order(String orderCode) {
        return Order.builder()
                .orderCode(orderCode)
                .status(OrderStatus.CONFIRMED)
                .totalAmount(new BigDecimal("10000"))
                .items(new ArrayList<>())
                .build();
    }

    private void addItem(Order order, String foodName, LocalDateTime createdAt) {
        OrderItem item = OrderItem.builder()
                .order(order)
                .foodId((long) foodName.hashCode() & 0xffffffffL)
                .foodName(foodName)
                .unitPrice(new BigDecimal("10000"))
                .quantity(1)
                .subtotal(new BigDecimal("10000"))
                .imageUrl(foodName.toLowerCase() + ".jpg")
                .emoji("F")
                .status(OrderItemStatus.CONFIRMED)
                .createdAt(createdAt)
                .statusUpdatedAt(createdAt)
                .build();
        order.getItems().add(item);
    }
}
