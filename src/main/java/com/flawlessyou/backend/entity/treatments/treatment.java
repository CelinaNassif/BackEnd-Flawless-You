package com.flawlessyou.backend.entity.treatments;

import java.util.List;
import java.util.UUID;

import com.flawlessyou.backend.entity.product.Type;

public class treatment {

    private String treatmentId;
    private Type skinType;
    private String problem;
    private List<String> productIds;



    public treatment( Type skinType, String problem, List<String> productIds) {
        this.treatmentId = UUID.randomUUID().toString();
        this.skinType = skinType;
        this.problem = problem;
        this.productIds = productIds;
    }

    
    
    public treatment() {
        this.treatmentId = UUID.randomUUID().toString();
    }

    
    public String getTreatmentId() {
        return treatmentId;
    }
    public void setTreatmentId(String treatmentId) {
        this.treatmentId = treatmentId;
    }
    public Type getSkinType() {
        return skinType;
    }
    public void setSkinType(Type skinType) {
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
