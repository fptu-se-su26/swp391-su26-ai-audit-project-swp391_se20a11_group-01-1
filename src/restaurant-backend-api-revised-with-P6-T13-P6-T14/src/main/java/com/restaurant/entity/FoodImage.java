package com.restaurant.entity;
import jakarta.persistence.*; import lombok.*; import java.time.LocalDateTime;
@Entity @Table(name="food_images") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FoodImage {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="image_id") private Long imageId;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="food_id", nullable=false) private Food food;
 @Column(name="image_url", nullable=false, length=500) private String imageUrl;
 @Column(name="is_primary", nullable=false) private Boolean isPrimary;
 @Column(name="uploaded_at", nullable=false) private LocalDateTime uploadedAt;
 @PrePersist void prePersist(){ if(isPrimary==null) isPrimary=false; if(uploadedAt==null) uploadedAt=LocalDateTime.now(); }
}
