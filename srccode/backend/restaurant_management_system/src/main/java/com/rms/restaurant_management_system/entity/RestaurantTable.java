package com.rms.restaurant_management_system.entity;

import com.rms.restaurant_management_system.enums.TableStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "restaurant_tables")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tableId;

    @Column(nullable = false, columnDefinition = "NVARCHAR(50)")
    private String tableName;

    @Column(nullable = false)
    private Integer capacity;

    @Builder.Default
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TableStatus status = TableStatus.EMPTY;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;

    // Mã đơn hàng hiện tại (nếu bàn đang có khách)
    @Column(length = 50)
    private String currentOrderCode;

    // Thông tin người đặt trước
    @Column(columnDefinition = "NVARCHAR(200)")
    private String reservedBy;

    // Bàn này đang được gộp vào bàn nào (tên bàn chính)
    @Column(columnDefinition = "NVARCHAR(50)")
    private String mergedInto;

    // Bàn chính đã gộp với những bàn nào (tên bàn phụ, phân cách bởi dấu phẩy)
    @Column(columnDefinition = "NVARCHAR(200)")
    private String mergedWith;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
