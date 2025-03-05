package com.flawlessyou.backend.entity.treatments;

import java.util.List;

public class treatment {

    private String treatmentId;
    private String skinType;
    private String problem;
    private List<String> productIds;

    
    public treatment() {
    }

    
    public treatment(String treatmentId, String skinType, String problem, List<String> productIds) {
        this.treatmentId = treatmentId;
        this.skinType = skinType;
        this.problem = problem;
        this.productIds = productIds;
    }
    public String getTreatmentId() {
        return treatmentId;
    }
    public void setTreatmentId(String treatmentId) {
        this.treatmentId = treatmentId;
    }
    public String getSkinType() {
        return skinType;
    }
    public void setSkinType(String skinType) {
        this.skinType = skinType;
    }
    public String getProblem() {
        return problem;
    }
    public void setProblem(String problem) {
        this.problem = problem;
    }
    public List<String> getProductIds() {
        return productIds;
    }
    public void setProductIds(List<String> productIds) {
        this.productIds = productIds;
    }

    

    
}
