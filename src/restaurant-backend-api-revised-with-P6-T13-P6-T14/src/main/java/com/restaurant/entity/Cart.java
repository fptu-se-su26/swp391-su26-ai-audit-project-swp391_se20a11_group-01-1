package com.restaurant.entity;
import com.restaurant.model.CartStatus; import jakarta.persistence.*; import lombok.*; import java.time.LocalDateTime; import java.util.ArrayList; import java.util.List;
@Entity @Table(name="carts") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Cart {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="cart_id") private Long cartId;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="customer_id", nullable=false) private User customer;
 @Enumerated(EnumType.STRING) @Column(name="cart_status", nullable=false, length=20) private CartStatus cartStatus;
 @Column(name="created_at", nullable=false) private LocalDateTime createdAt;
 @Column(name="updated_at") private LocalDateTime updatedAt;
 @OneToMany(mappedBy="cart", cascade=CascadeType.ALL, orphanRemoval=true) @Builder.Default private List<CartItem> items = new ArrayList<>();
 @PrePersist void prePersist(){ if(cartStatus==null) cartStatus=CartStatus.ACTIVE; if(createdAt==null) createdAt=LocalDateTime.now(); }
 @PreUpdate void preUpdate(){ updatedAt=LocalDateTime.now(); }
}
