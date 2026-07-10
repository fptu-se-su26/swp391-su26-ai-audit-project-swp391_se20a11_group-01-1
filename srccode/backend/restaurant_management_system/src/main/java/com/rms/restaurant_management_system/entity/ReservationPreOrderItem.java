package com.rms.restaurant_management_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "reservation_pre_order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationPreOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long preOrderItemId;

    private Long foodId;

    @Column(nullable = false, columnDefinition = "NVARCHAR(150)")
    private String foodName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;
}
