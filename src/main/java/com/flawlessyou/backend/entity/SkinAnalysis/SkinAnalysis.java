package com.flawlessyou.backend.entity.SkinAnalysis;

import java.util.Map;
import java.util.UUID;



import com.flawlessyou.backend.entity.product.Type;
import com.flawlessyou.backend.entity.treatments.Problem;

public class SkinAnalysis {
    private String id;
    private String userId;
    private Type skintype;
    private Map<Problem, Double> problems; 
    private String treatmentId;
    private String imageUrl;
    public String getId() {
        return id;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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
    public Map<Problem, Double> getProblems() {
        return problems;
    }
    public void setProblems(Map<Problem, Double> problems) {
        this.problems = problems;
    }
    public String getTreatmentId() {
        return treatmentId;
    }
    public void setTreatmentId(String treatmentId) {
        this.treatmentId = treatmentId;
    }
    public SkinAnalysis(String userId, Type skintype, Map<Problem, Double> problems,String imageUrl) {
        this.userId = userId;
        this.id=UUID.randomUUID().toString();
        this.skintype = skintype;
        this.problems = problems;
        
        this.imageUrl=imageUrl;
    }

}
