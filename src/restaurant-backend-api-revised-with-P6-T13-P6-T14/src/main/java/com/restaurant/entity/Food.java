package com.restaurant.entity;
import com.restaurant.model.AvailabilityStatus; import jakarta.persistence.*; import lombok.*; import java.math.BigDecimal; import java.time.LocalDateTime; import java.util.ArrayList; import java.util.List;
@Entity @Table(name="foods") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Food {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="food_id") private Long foodId;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="category_id", nullable=false) private Category category;
 @Column(name="food_name", nullable=false, length=150) private String foodName;
 @Column(name="description", columnDefinition="NVARCHAR(MAX)") private String description;
 @Column(name="price", nullable=false, precision=18, scale=2) private BigDecimal price;
 @Enumerated(EnumType.STRING) @Column(name="availability_status", nullable=false, length=20) private AvailabilityStatus availabilityStatus;
 @Column(name="estimated_cooking_time") private Integer estimatedCookingTime;
 @Column(name="created_at", nullable=false) private LocalDateTime createdAt;
 @Column(name="updated_at") private LocalDateTime updatedAt;
 @OneToMany(mappedBy="food", cascade=CascadeType.ALL, orphanRemoval=true) @Builder.Default private List<FoodImage> images = new ArrayList<>();
 @PrePersist void prePersist(){ if(availabilityStatus==null) availabilityStatus=AvailabilityStatus.AVAILABLE; if(createdAt==null) createdAt=LocalDateTime.now(); }
 @PreUpdate void preUpdate(){ updatedAt=LocalDateTime.now(); }
}
