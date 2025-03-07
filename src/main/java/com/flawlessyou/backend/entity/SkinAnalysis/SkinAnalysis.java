package com.flawlessyou.backend.entity.SkinAnalysis;

import java.util.Map;
import java.util.UUID;

import com.flawlessyou.backend.entity.product.Type;

public class SkinAnalysis {
    private String id;
    private String userId;
    private Type skintype;
    private Map<String, Double> problem; 
    private String treatmentId;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public Type getSkintype() {
        return skintype;
    }
    public void setSkintype(Type skintype) {
        this.skintype = skintype;
    }
    public Map<String, Double> getProblem() {
        return problem;
    }
    public void setProblem(Map<String, Double> problem) {
        this.problem = problem;
    }
    public String getTreatmentId() {
        return treatmentId;
    }
    public void setTreatmentId(String treatmentId) {
        this.treatmentId = treatmentId;
    }
    public SkinAnalysis(String userId, Type skintype, Map<String, Double> problem, String treatmentId) {
        this.userId = userId;
        this.id=UUID.randomUUID().toString();
        this.skintype = skintype;
        this.problem = problem;
        this.treatmentId = treatmentId;
    }

}
