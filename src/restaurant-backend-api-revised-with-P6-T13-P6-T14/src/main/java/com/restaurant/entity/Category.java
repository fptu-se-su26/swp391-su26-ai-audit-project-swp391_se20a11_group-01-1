package com.restaurant.entity;
import com.restaurant.model.CategoryStatus; import jakarta.persistence.*; import lombok.*;
@Entity @Table(name="categories") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Category {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="category_id") private Long categoryId;
 @Column(name="category_name", nullable=false, unique=true, length=100) private String categoryName;
 @Column(name="description", length=255) private String description;
 @Enumerated(EnumType.STRING) @Column(name="status", nullable=false, length=20) private CategoryStatus status;
 @PrePersist void prePersist(){ if(status==null) status=CategoryStatus.ACTIVE; }
}
