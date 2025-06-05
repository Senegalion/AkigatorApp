package org.example.akigatorapp.models;

public class AnswerDto {
    private Long answerId;
    private String content;

    public AnswerDto(Long answerId, String content) {
        this.answerId = answerId;
        this.content = content;
    }

    public AnswerDto(AnswerEntity entity) {
        this.answerId = entity.getAnswerId();
        this.content = entity.getResponse();
    }

    public Long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
