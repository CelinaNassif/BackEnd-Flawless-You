package com.flawlessyou.backend.entity.SkinAnalysis;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.flawlessyou.backend.entity.product.Type;
import com.flawlessyou.backend.entity.treatments.Problem;
import com.flawlessyou.backend.entity.treatments.Treatment;

public class SkinAnalysis {
    private String id;
    private String userId;
    private Type skintype;
    private Map<String, Double> problems; // تم تغيير النوع إلى Map<String, Double>
    private List<Treatment> treatmentId;
    private String imageUrl;

    // Constructor
    public SkinAnalysis(String userId, Type skintype, Map<Problem, Double> problems) {
        this.userId = userId;
        this.id = UUID.randomUUID().toString();
        this.skintype = skintype;
        this.problems = convertProblemsMap(problems); // تحويل Map<Problem, Double> إلى Map<String, Double>
    }

    public SkinAnalysis() {
    }

    // طريقة لتحويل Map<Problem, Double> إلى Map<String, Double>
    private Map<String, Double> convertProblemsMap(Map<Problem, Double> problems) {
        return problems.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().name(), // تحويل Enum إلى String
                        Map.Entry::getValue
                ));
    }

    // Getters and Setters
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

    public Map<String, Double> getProblems() {
        return problems;
    }

    public void setProblems(Map<String, Double> problems) {
        this.problems = problems;
    }

    public List<Treatment> getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(List<Treatment> treatmentId) {
        this.treatmentId = treatmentId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}