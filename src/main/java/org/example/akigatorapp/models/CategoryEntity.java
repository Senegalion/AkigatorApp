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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<GameSessionEntity> getGameSession() {
        return gameSession;
    }

    public void setGameSession(Set<GameSessionEntity> gameSession) {
        this.gameSession = gameSession;
    }

    public Set<QuestionEntity> getQuestions() {
        return questions;
    }

    public void setQuestions(Set<QuestionEntity> questions) {
        this.questions = questions;
    }

    public Set<EntityEntity> getEntities() {
        return entities;
    }

    public void setEntities(Set<EntityEntity> entities) {
        this.entities = entities;
    }
}
