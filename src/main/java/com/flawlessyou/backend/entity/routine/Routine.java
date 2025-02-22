package com.flawlessyou.backend.entity.routine;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.sql.Timestamp;
import java.util.Map;


public class Routine {
    private String routineId;

    private String userId;
    private Map<String, String> productIds;
    private Timestamp timeAnalysis;
    private String description;
    private String analysisId;

    public Routine( Map<String, String> productIds, Timestamp timeAnalysis, String description,
            String analysisId) {
        this.routineId = UUID.randomUUID().toString();
        this.productIds = productIds;
        this.timeAnalysis = timeAnalysis;
        this.description = description;
        this.analysisId = analysisId;
    }
    
  public Routine() {
    this.routineId = UUID.randomUUID().toString();
    }

// Getters and setters (required for Firestore serialization/deserialization)
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

public Map<String, String> getProductIds() {
    return productIds;
}

public void setProductIds(Map<String, String> productIds) {
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