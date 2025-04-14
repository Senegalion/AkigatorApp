package org.example.akigatorapp.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long questionId;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "question")
    private Set<GameQuestionEntity> gameQuestions;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "question")
    private Set<AnswerEntity> answers;

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }

    public Set<GameQuestionEntity> getGameQuestions() {
        return gameQuestions;
    }

    public void setGameQuestions(Set<GameQuestionEntity> gameQuestions) {
        this.gameQuestions = gameQuestions;
    }

    public Set<AnswerEntity> getAnswers() {
        return answers;
    }

    public void setAnswers(Set<AnswerEntity> answers) {
        this.answers = answers;
    }
}
