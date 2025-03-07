package com.flawlessyou.backend.entity.SkinAnalysis;


import com.flawlessyou.backend.entity.cloudinary.CloudinaryResponse;
import com.flawlessyou.backend.entity.cloudinary.CloudinaryService;
import com.flawlessyou.backend.entity.product.Type;
import com.flawlessyou.backend.entity.treatments.Problem;
import com.flawlessyou.backend.entity.treatments.Treatment;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class SkinAnalysisService {
    private static final Logger logger = LoggerFactory.getLogger(SkinAnalysisService.class);
    @Autowired
    private CloudinaryService cloudinaryService;

    private static final String SKIN_ANALYSIS_COLLECTION = "skinAnalysis";
    private static final String TREATMENTS_COLLECTION = "treatment";

    private final Firestore firestore;

    public SkinAnalysisService() {
        this.firestore = FirestoreClient.getFirestore();
    }

    public SkinAnalysis analyzeSkin(String userId, Type skinType, Map<Problem, Double> problems, MultipartFile imageFile) throws ExecutionException, InterruptedException {
        // 1. Upload image to Cloudinary
        CloudinaryResponse cloudinaryResponse = cloudinaryService.uploadFile(imageFile, userId + "_skin_analysis");
        String imageUrl = cloudinaryResponse.getUrl();

        // 2. Find the appropriate treatment
        Treatment treatment = findTreatmentBySkinTypeAndProblems(skinType, problems);

        // 3. Create SkinAnalysis and save it to Firestore
        SkinAnalysis skinAnalysis = new SkinAnalysis(userId, skinType, problems, imageUrl);
        skinAnalysis.setTreatmentId(treatment.getTreatmentId());

        // Save SkinAnalysis to Firestore
        DocumentReference docRef = firestore.collection(SKIN_ANALYSIS_COLLECTION).document();
        skinAnalysis.setId(docRef.getId());
        ApiFuture<WriteResult> result = docRef.set(skinAnalysis);
        result.get(); // Wait for the save operation to complete

        return skinAnalysis;
    }

    private Treatment findTreatmentBySkinTypeAndProblems(Type skinType, Map<Problem, Double> problems) throws ExecutionException, InterruptedException {
        CollectionReference treatmentsRef = firestore.collection(TREATMENTS_COLLECTION);
        Query query = treatmentsRef.whereEqualTo("skinType", skinType);

        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        logger.info("Found {} treatments for skin type: {}", documents.size(), skinType);

        for (QueryDocumentSnapshot document : documents) {
            Treatment treatment = document.toObject(Treatment.class);
            Problem treatmentProblem = treatment.getProblem();

            logger.info("Checking treatment: {} with problem: {}", treatment.getTreatmentId(), treatmentProblem);

            if (problems.containsKey(treatmentProblem) && problems.get(treatmentProblem) > 0) {
                logger.info("Found matching treatment: {}", treatment.getTreatmentId());
                return treatment;
            }
        }

        logger.error("No treatment found for skin type: {} and problems: {}", skinType, problems);
        throw new RuntimeException("No treatment found for the given skin type and problems");
    }
}