package org.example.akigatorapp.models;

import java.time.OffsetDateTime;

public class GameSessionDto {
    private Long sessionId;
    private String categoryName;
    private String startTime;
    private String duration;
    private String result;

    public GameSessionDto(Long sessionId, String categoryName, String startTime, String duration, String result) {
        this.sessionId = sessionId;
        this.categoryName = categoryName;
        this.startTime = startTime;
        this.duration = duration;
        this.result = result;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
