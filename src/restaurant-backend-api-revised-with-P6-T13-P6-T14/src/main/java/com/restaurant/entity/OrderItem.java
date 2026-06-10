package com.restaurant.entity;
import com.restaurant.model.OrderItemStatus; import jakarta.persistence.*; import lombok.*; import java.math.BigDecimal;
@Entity @Table(name="order_items") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderItem {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="order_item_id") private Long orderItemId;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="order_id", nullable=false) private Order order;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="food_id", nullable=false) private Food food;
 @Column(name="quantity", nullable=false) private Integer quantity;
 @Column(name="unit_price", nullable=false, precision=18, scale=2) private BigDecimal unitPrice;
 @Enumerated(EnumType.STRING) @Column(name="item_status", nullable=false, length=30) private OrderItemStatus itemStatus;
 @Column(name="kitchen_note", length=500) private String kitchenNote;
 @PrePersist void prePersist(){ if(itemStatus==null) itemStatus=OrderItemStatus.PENDING; }
}
