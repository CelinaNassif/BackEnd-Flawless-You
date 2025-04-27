package com.flawlessyou.backend.entity.SkinAnalysis;

import com.flawlessyou.backend.entity.cloudinary.CloudinaryResponse;
import com.flawlessyou.backend.entity.cloudinary.CloudinaryService;
import com.flawlessyou.backend.entity.product.Product;
import com.flawlessyou.backend.entity.product.Type;
import com.flawlessyou.backend.entity.treatments.Problem;
import com.flawlessyou.backend.entity.treatments.Treatment;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;

import jakarta.servlet.http.HttpServletRequest;

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


    public List<SkinAnalysis> getAllSkinAnalysesByUserId(String userId) throws ExecutionException, InterruptedException {
        try {
            // 1. إنشاء الاستعلام للحصول على جميع تحاليل الجلد للمستخدم
            ApiFuture<QuerySnapshot> future = firestore.collection("skinAnalysis")
                    .whereEqualTo("userId", userId)
                    .get();
    
            // 2. انتظار نتيجة الاستعلام
            QuerySnapshot querySnapshot = future.get();
    
            // 3. تحويل النتائج إلى قائمة من SkinAnalysis
            List<SkinAnalysis> skinAnalyses = new ArrayList<>();
            for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
                SkinAnalysis skinAnalysis = document.toObject(SkinAnalysis.class);
                skinAnalyses.add(skinAnalysis);
            }
    
            logger.info("Retrieved " + skinAnalyses.size() + " skin analyses for user ID: " + userId);
            return skinAnalyses;
        } catch (Exception e) {
            logger.severe("Error getting skin analyses for user ID " + userId + ": " + e.getMessage());
            throw e;
        }
    }
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
    
            List<Treatment> t = treatmentsBySkinType.stream()
                    .filter(treatment -> relevantProblems.contains(treatment.getProblem()))
                    .collect(Collectors.toList());
    
            // Step 4: Filter treatments that match the relevant problems
            skinAnalysis.setTreatmentId(t);
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

    public Map<String, List<Product>> getProductsBySkinAnalysisId(String skinAnalysisId) throws Exception {
        // 1. الحصول على SkinAnalysis من Firestore باستخدام skinAnalysisId
        DocumentReference skinAnalysisRef = firestore.collection("skinAnalysis").document(skinAnalysisId);
        ApiFuture<DocumentSnapshot> skinAnalysisFuture = skinAnalysisRef.get();
        DocumentSnapshot skinAnalysisDoc = skinAnalysisFuture.get();
    
        if (!skinAnalysisDoc.exists()) {
            throw new IllegalArgumentException("SkinAnalysis not found with id: " + skinAnalysisId);
        }
    
        // تحويل DocumentSnapshot إلى كائن SkinAnalysis
        SkinAnalysis skinAnalysis = skinAnalysisDoc.toObject(SkinAnalysis.class);
    
        // 2. استرداد Treatments المرتبطة بـ SkinAnalysis
        List<Treatment> treatments = skinAnalysis.getTreatmentId();
    
        // 3. إنشاء خريطة لتخزين المنتجات الخاصة بكل مشكلة
        Map<String, List<Product>> productsByProblem = new HashMap<>();
    
        // 4. جمع جميع productIds من Treatments وتجميعها حسب المشكلة
        for (Treatment treatment : treatments) {
            String problem = treatment.getProblem().name();
            Map<String ,String> productIds = treatment.getProductIds();
    
            // 5. استرداد تفاصيل المنتجات من Firestore باستخدام productIds
            List<Product> products = new ArrayList<>();
            for (String productId : productIds.keySet()) {
                DocumentReference productRef = firestore.collection("products").document(productId);
                ApiFuture<DocumentSnapshot> productFuture = productRef.get();
                DocumentSnapshot productDoc = productFuture.get();
    
                if (productDoc.exists()) {
                    Product product = productDoc.toObject(Product.class);
                    products.add(product);
                }
            }
    
            // 6. إضافة المنتجات إلى الخريطة حسب المشكلة
            productsByProblem.put(problem, products);
        }
    
        return productsByProblem;
    }


    public SkinAnalysis getLatestSkinAnalysisByUserId(String userId) throws ExecutionException, InterruptedException {
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection("skinAnalysis")
                    .whereEqualTo("userId", userId)
                    .get();
    
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            if (documents.isEmpty()) {
                return null;
            }
    
            // البحث عن أحدث وثيقة باستخدام Timestamp
            QueryDocumentSnapshot latestDoc = documents.stream()
                    .max(Comparator.comparing(doc -> doc.getTimestamp("createdAt")))
                    .orElseThrow();
    
            return latestDoc.toObject(SkinAnalysis.class);
        } catch (Exception e) {
            logger.severe("Error getting latest analysis: " + e.getMessage());
            throw e;
        }
    }




}