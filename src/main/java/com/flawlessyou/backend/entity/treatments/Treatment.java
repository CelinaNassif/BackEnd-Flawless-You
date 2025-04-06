package com.flawlessyou.backend.entity.treatments;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.flawlessyou.backend.entity.product.Type;

public class Treatment {

    private String treatmentId;
    private Type skinType;
    private String description;
    private Problem problem;
    private Map<String ,String> productIds;
   
   
    public Treatment( Type skinType, String description, Map<String ,String> productIds,Problem problem) {
        this.treatmentId = UUID.randomUUID().toString();
        this.skinType = skinType;
        this.description = description;
        this.productIds = productIds;
        this.problem=problem;
    }

    
    
    public Treatment() {
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
    public String getdescription() {
        return description;
    }
    public void setdescription(String description) {
        this.description = description;
    }
    public Map<String, String> getProductIds() {
        return productIds;
    }
    public void setProductIds(Map<String, String> productIds2 ) {
        this.productIds = productIds2;
    }

    public Problem getProblem() {
        return problem;
    }



    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    
}
