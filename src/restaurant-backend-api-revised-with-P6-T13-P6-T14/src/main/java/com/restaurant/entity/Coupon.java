package com.restaurant.entity;
import com.restaurant.model.*; import jakarta.persistence.*; import lombok.*; import java.math.BigDecimal; import java.time.LocalDateTime;
@Entity @Table(name="coupons") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Coupon {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="coupon_id") private Long couponId;
 @Column(name="code", nullable=false, unique=true, length=50) private String code;
 @Enumerated(EnumType.STRING) @Column(name="discount_type", nullable=false, length=20) private DiscountType discountType;
 @Column(name="discount_value", nullable=false, precision=18, scale=2) private BigDecimal discountValue;
 @Column(name="min_order_amount", nullable=false, precision=18, scale=2) private BigDecimal minOrderAmount;
 @Column(name="start_date", nullable=false) private LocalDateTime startDate;
 @Column(name="end_date", nullable=false) private LocalDateTime endDate;
 @Enumerated(EnumType.STRING) @Column(name="status", nullable=false, length=20) private CouponStatus status;
 @PrePersist void prePersist(){ if(status==null) status=CouponStatus.ACTIVE; if(minOrderAmount==null) minOrderAmount=BigDecimal.ZERO; }
}
