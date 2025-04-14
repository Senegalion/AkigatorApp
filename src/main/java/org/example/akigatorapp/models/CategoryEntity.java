package org.example.akigatorapp.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categories")
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "category")
    private Set<GameSessionEntity> gameSession;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "category")
    private Set<QuestionEntity> questions;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "category")
    private Set<EntityEntity> entities;

}
