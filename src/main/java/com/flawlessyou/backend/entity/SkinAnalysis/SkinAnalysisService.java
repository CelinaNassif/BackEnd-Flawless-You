package com.flawlessyou.backend.entity.SkinAnalysis;

import com.flawlessyou.backend.entity.product.Type;
import com.flawlessyou.backend.entity.treatments.Problem;
import com.flawlessyou.backend.entity.treatments.Treatment;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class SkinAnalysisService {

    private static final Logger logger = Logger.getLogger(SkinAnalysisService.class.getName());

    @Autowired
    private Firestore firestore;

    public List<Treatment> getRecommendedTreatments(String userId, Type skinType, Map<Problem, Double> problems) throws ExecutionException, InterruptedException {
        try {
            // Step 1: Create a SkinAnalysis object
            SkinAnalysis skinAnalysis = new SkinAnalysis(userId, skinType, problems);
         

            // Step 2: Get treatments based on skin type
            List<Treatment> treatmentsBySkinType = getTreatmentsBySkinType(skinAnalysis.getSkintype());

            // Step 3: Filter treatments based on problems with values greater than 0
            Set<Problem> relevantProblems = problems.entrySet().stream()
                    .filter(entry -> entry.getValue() > 0)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());

            // Step 4: Filter treatments that match the relevant problems
            return treatmentsBySkinType.stream()
                    .filter(treatment -> relevantProblems.contains(treatment.getProblem()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.severe("Error in getRecommendedTreatments: " + e.getMessage());
            throw e;
        }
    }

    public List<Treatment> getTreatmentsBySkinType(Type skinType) throws ExecutionException, InterruptedException {
        List<Treatment> treatments = new ArrayList<>();

        try {
            // Query Firestore to get treatments by skin type
            ApiFuture<QuerySnapshot> future = firestore.collection("treatment")
                    .whereEqualTo("skinType", skinType.toString())
                    .get();

            QuerySnapshot querySnapshot = future.get();
            for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
                Treatment treatment = document.toObject(Treatment.class);
                treatments.add(treatment);
            }
        } catch (Exception e) {
            logger.severe("Error in getTreatmentsBySkinType: " + e.getMessage());
            throw e;
        }

        return treatments;
    }
}