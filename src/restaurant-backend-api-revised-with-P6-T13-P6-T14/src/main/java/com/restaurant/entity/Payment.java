package com.restaurant.entity;
import com.restaurant.model.*; import jakarta.persistence.*; import lombok.*; import java.math.BigDecimal; import java.time.LocalDateTime;
@Entity @Table(name="payments") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="payment_id") private Long paymentId;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="order_id", nullable=false) private Order order;
 @Enumerated(EnumType.STRING) @Column(name="payment_method", nullable=false, length=30) private PaymentMethod paymentMethod;
 @Enumerated(EnumType.STRING) @Column(name="payment_status", nullable=false, length=30) private PaymentStatus paymentStatus;
 @Column(name="amount", nullable=false, precision=18, scale=2) private BigDecimal amount;
 @Column(name="transaction_code", length=100) private String transactionCode;
 @Column(name="paid_at") private LocalDateTime paidAt;
 @Column(name="created_at", nullable=false) private LocalDateTime createdAt;
 @PrePersist void prePersist(){ if(paymentStatus==null) paymentStatus=PaymentStatus.PENDING; if(createdAt==null) createdAt=LocalDateTime.now(); }
}
