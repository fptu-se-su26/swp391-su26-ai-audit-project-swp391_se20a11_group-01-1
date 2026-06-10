package com.restaurant.entity;
import com.restaurant.model.*; import jakarta.persistence.*; import lombok.*; import java.math.BigDecimal; import java.time.LocalDateTime; import java.util.ArrayList; import java.util.List;
@Entity @Table(name="orders") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Order {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="order_id") private Long orderId;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="customer_id", nullable=false) private User customer;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="staff_id") private User staff;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="coupon_id") private Coupon coupon;
 @Enumerated(EnumType.STRING) @Column(name="order_type", nullable=false, length=20) private OrderType orderType;
 @Enumerated(EnumType.STRING) @Column(name="order_status", nullable=false, length=30) private OrderStatus orderStatus;
 @Column(name="subtotal_amount", nullable=false, precision=18, scale=2) private BigDecimal subtotalAmount;
 @Column(name="discount_amount", nullable=false, precision=18, scale=2) private BigDecimal discountAmount;
 @Column(name="total_amount", nullable=false, precision=18, scale=2) private BigDecimal totalAmount;
 @Column(name="note", length=500) private String note;
 @Column(name="created_at", nullable=false) private LocalDateTime createdAt;
 @OneToMany(mappedBy="order", cascade=CascadeType.ALL, orphanRemoval=true) @Builder.Default private List<OrderItem> items = new ArrayList<>();
 @PrePersist void prePersist(){ if(orderType==null) orderType=OrderType.DINE_IN; if(orderStatus==null) orderStatus=OrderStatus.PENDING; if(createdAt==null) createdAt=LocalDateTime.now(); if(subtotalAmount==null) subtotalAmount=BigDecimal.ZERO; if(discountAmount==null) discountAmount=BigDecimal.ZERO; if(totalAmount==null) totalAmount=subtotalAmount.subtract(discountAmount); }
}
