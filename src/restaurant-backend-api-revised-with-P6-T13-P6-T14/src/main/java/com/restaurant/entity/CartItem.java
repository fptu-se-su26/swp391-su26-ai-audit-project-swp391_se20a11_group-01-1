package com.restaurant.entity;
import jakarta.persistence.*; import lombok.*; import java.math.BigDecimal; import java.time.LocalDateTime;
@Entity @Table(name="cart_items", uniqueConstraints=@UniqueConstraint(name="uq_cart_items_cart_food", columnNames={"cart_id","food_id"})) @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CartItem {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="cart_item_id") private Long cartItemId;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="cart_id", nullable=false) private Cart cart;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="food_id", nullable=false) private Food food;
 @Column(name="quantity", nullable=false) private Integer quantity;
 @Column(name="unit_price", nullable=false, precision=18, scale=2) private BigDecimal unitPrice;
 @Column(name="created_at", nullable=false) private LocalDateTime createdAt;
 @PrePersist void prePersist(){ if(createdAt==null) createdAt=LocalDateTime.now(); }
}
