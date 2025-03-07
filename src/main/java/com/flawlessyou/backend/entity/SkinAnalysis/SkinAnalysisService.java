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

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class SkinAnalysisService {

    private static final Logger logger = Logger.getLogger(SkinAnalysisService.class.getName());
    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private Firestore firestore;

    public SkinAnalysis getRecommendedTreatments(String userId, Type skinType, Map<Problem, Double> problems) throws ExecutionException, InterruptedException {
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
                    List<Treatment> t=     treatmentsBySkinType.stream()
                    .filter(treatment -> relevantProblems.contains(treatment.getProblem()))
                    .collect(Collectors.toList());
                
            // Step 4: Filter treatments that match the relevant problems
            skinAnalysis.setTreatmentId( t);
                    saveSkinAnalysis(skinAnalysis);
                    return skinAnalysis; 
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


    private void saveSkinAnalysis(SkinAnalysis skinAnalysis) throws ExecutionException, InterruptedException {
        try {
            // Save the SkinAnalysis object to Firestore
            ApiFuture<WriteResult> future = firestore.collection("skinAnalysis")
                    .document(skinAnalysis.getId())
                    .set(skinAnalysis);

            future.get(); // Wait for the operation to complete
            logger.info("SkinAnalysis saved with ID: " + skinAnalysis.getId());
        } catch (Exception e) {
            logger.severe("Error saving SkinAnalysis: " + e.getMessage());
            throw e;
        }
    }
        public boolean uploadImageAndUpdateSkinAnalysis(String id, MultipartFile imageFile) throws ExecutionException, InterruptedException {
        try {
            // 1. التأكد من أن الـ ID غير فارغ
            if (id == null || id.isEmpty()) {
                logger.warning("Invalid ID provided for updating image.");
                return false;
            }

            // 2. التأكد من أن ملف الصورة غير فارغ
            if (imageFile == null || imageFile.isEmpty()) {
                logger.warning("Invalid image file provided.");
                return false;
            }

            // 3. رفع الصورة إلى Cloudinary
            CloudinaryResponse cloudinaryResponse = cloudinaryService.uploadFile(imageFile, id + "_skin_analysis");
            String imageUrl = cloudinaryResponse.getUrl();

            // 4. تحديث رابط الصورة في Firestore
            ApiFuture<WriteResult> future = firestore.collection("skinAnalysis")
                    .document(id)
                    .update("imageUrl", imageUrl);

            future.get(); // انتظار اكتمال العملية
            logger.info("Image uploaded and updated successfully for SkinAnalysis with ID: " + id);
            return true;
        } catch (Exception e) {
            logger.severe("Error uploading image or updating SkinAnalysis: " + e.getMessage());
            throw e;
        }
    }
}