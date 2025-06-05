package org.example.akigatorapp.models;

public class QuestionDto {
    private Long questionId;
    private String content;

    public QuestionDto(Long questionId, String content) {
        this.questionId = questionId;
        this.content = content;
    }

    public QuestionDto(QuestionEntity entity) {
        this.questionId = entity.getQuestionId();
        this.content = entity.getContent();
    }

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
}
