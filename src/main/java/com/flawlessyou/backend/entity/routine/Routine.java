package com.flawlessyou.backend.entity.routine;

import com.google.cloud.Timestamp;
import java.util.Map;
import java.util.UUID;

public class Routine {
    private String routineId;
    private String userId;
    private Map<String, Timestamp> productIds;
    private Timestamp timeAnalysis;
    private String description;
    private String analysisId;

    public Routine(Map<String, Timestamp> productIds, Timestamp timeAnalysis, String description, String analysisId) {
        this.routineId = UUID.randomUUID().toString();
        this.productIds = productIds;
        this.timeAnalysis = timeAnalysis;
        this.description = description;
        this.analysisId = analysisId;
    }

    public Routine() {
        // No-argument constructor required for Firestore serialization/deserialization
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

    public Map<String, Timestamp> getProductIds() {
        return productIds;
    }

    public void setProductIds(Map<String, Timestamp> productIds) {
        this.productIds = productIds;
    }

    public Timestamp getTimeAnalysis() {
        return timeAnalysis;
    }

    public void setTimeAnalysis(Timestamp timeAnalysis) {
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