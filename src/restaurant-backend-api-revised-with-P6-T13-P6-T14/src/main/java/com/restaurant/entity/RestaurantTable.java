package com.restaurant.entity;
import com.restaurant.model.TableStatus; import jakarta.persistence.*; import lombok.*;
@Entity @Table(name="restaurant_tables") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RestaurantTable {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="table_id") private Long tableId;
 @Column(name="table_number", nullable=false, unique=true, length=20) private String tableNumber;
 @Column(name="capacity", nullable=false) private Integer capacity;
 @Enumerated(EnumType.STRING) @Column(name="table_status", nullable=false, length=30) private TableStatus tableStatus;
 @Column(name="location", length=100) private String location;
 @PrePersist void prePersist(){ if(tableStatus==null) tableStatus=TableStatus.AVAILABLE; }
}
