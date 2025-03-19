package com.flawlessyou.backend.entity.routine;

import java.util.List;
import java.util.UUID;

public class Routine {
    private String routineId;
    private String userId;
    private List<String> productIds;
    private String timeAnalysis;
    private String description;
    private String analysisId;

    public Routine() {
        this.routineId = UUID.randomUUID().toString();
    }

    public Routine(List<String> productIds, String timeAnalysis, String description, String analysisId) {
        this.routineId = UUID.randomUUID().toString();
        this.productIds = productIds;
        this.timeAnalysis = timeAnalysis;
        this.description = description;
        this.analysisId = analysisId;
    }

    // Getters and setters
    public String getRoutineId() {
        return routineId;
    }

    public void setRoutineId(String routineId) {
        this.routineId = routineId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<String> productIds) {
        this.productIds = productIds;
    }

    public String getTimeAnalysis() {
        return timeAnalysis;
    }

    public void setTimeAnalysis(String timeAnalysis) {
        this.timeAnalysis = timeAnalysis;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(String analysisId) {
        this.analysisId = analysisId;
    }
}