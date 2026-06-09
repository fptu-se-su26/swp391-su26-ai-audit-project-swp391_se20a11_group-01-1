package com.restaurant.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "food_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category_name", nullable = false, unique = true, length = 100)
    private String categoryName;

    @Column(name = "description")
    private String description;

    @Column(name = "status", length = 20)
    private String status;

    @OneToMany(mappedBy = "category")
    @Builder.Default
    private List<FoodItem> foodItems = new ArrayList<>();
}
