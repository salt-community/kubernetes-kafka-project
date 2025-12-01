package com.example.restaurant.kitchen_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "ingredients")
public class Ingredient {

    @Id
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer availableQuantity;

    @ManyToMany(mappedBy = "ingredients")
    private List<Recipe> recipes;

}
