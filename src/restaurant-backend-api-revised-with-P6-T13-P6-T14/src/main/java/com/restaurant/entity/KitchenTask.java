package com.restaurant.entity;

import com.restaurant.model.KitchenTaskStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "kitchen_tasks")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class KitchenTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kitchen_task_id")
    private Long kitchenTaskId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kitchen_user_id", nullable = false)
    private User kitchenUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_status", nullable = false, length = 30)
    private KitchenTaskStatus taskStatus;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Column(name = "note", length = 500)
    private String note;

    @PrePersist
    void prePersist() {
        if (taskStatus == null) taskStatus = KitchenTaskStatus.WAITING;
    }
}
