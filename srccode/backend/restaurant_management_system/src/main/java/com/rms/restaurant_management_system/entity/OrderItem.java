package com.rms.restaurant_management_system.entity;

import com.rms.restaurant_management_system.enums.OrderItemStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    @Column(nullable = false)
    private Long foodId;

    @Column(nullable = false, columnDefinition = "NVARCHAR(150)")
    private String foodName;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private Integer quantity;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal subtotal;

    @Column(columnDefinition = "NVARCHAR(500)")
    private String imageUrl;

    @Column(columnDefinition = "NVARCHAR(50)")
    private String emoji;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    @Builder.Default
    private OrderItemStatus status = OrderItemStatus.CONFIRMED;

    @Column(columnDefinition = "NVARCHAR(500)")
    private String note;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "status_updated_at")
    @Builder.Default
    private LocalDateTime statusUpdatedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        if (status == null) {
            status = OrderItemStatus.CONFIRMED;
        }
        if (createdAt == null) {
            createdAt = now;
        }
        if (statusUpdatedAt == null) {
            statusUpdatedAt = now;
        }
    }
}
