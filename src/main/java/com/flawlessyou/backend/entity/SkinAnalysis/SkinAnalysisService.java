package com.flawlessyou.backend.entity.SkinAnalysis;

import com.flawlessyou.backend.entity.SkinAnalysis.SkinAnalysis;
import com.flawlessyou.backend.entity.cloudinary.CloudinaryResponse;
import com.flawlessyou.backend.entity.cloudinary.CloudinaryService;
import com.flawlessyou.backend.entity.product.Type;
import com.flawlessyou.backend.entity.treatments.Problem;
import com.flawlessyou.backend.entity.treatments.treatment;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class SkinAnalysisService {

    @Autowired
    private CloudinaryService cloudinaryService;

    private static final String SKIN_ANALYSIS_COLLECTION = "skinAnalysis";
    private static final String TREATMENTS_COLLECTION = "treatments";

    private final Firestore firestore;

    public SkinAnalysisService() {
        this.firestore = FirestoreClient.getFirestore();
    }

    // تحليل البشرة
    public SkinAnalysis analyzeSkin(String userId, Type skinType, Map<String, Double> problems, MultipartFile imageFile) throws ExecutionException, InterruptedException {
        // 1. رفع الصورة إلى Cloudinary
        CloudinaryResponse cloudinaryResponse = cloudinaryService.uploadFile(imageFile, userId + "_skin_analysis");
        String imageUrl = cloudinaryResponse.getUrl();

        // 2. البحث عن العلاج المناسب
        treatment treatment = findTreatmentBySkinTypeAndProblems(skinType, problems);

        // 3. إنشاء SkinAnalysis وحفظه في Firestore
        SkinAnalysis skinAnalysis = new SkinAnalysis(userId, skinType, problems, imageUrl);
        skinAnalysis.setTreatmentId(treatment.getTreatmentId());

        // حفظ SkinAnalysis في Firestore
        DocumentReference docRef = firestore.collection(SKIN_ANALYSIS_COLLECTION).document();
        skinAnalysis.setId(docRef.getId());
        ApiFuture<WriteResult> result = docRef.set(skinAnalysis);
        result.get(); // انتظر حتى يتم الحفظ

        return skinAnalysis;
    }

    // البحث عن العلاج المناسب
    private treatment findTreatmentBySkinTypeAndProblems(Type skinType, Map<String, Double> problems) throws ExecutionException, InterruptedException {
        // استعلام Firestore للبحث عن علاجات بنفس نوع البشرة
        CollectionReference treatmentsRef = firestore.collection(TREATMENTS_COLLECTION);
        Query query = treatmentsRef.whereEqualTo("skinType", skinType);

        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        // تصفية العلاجات بناءً على المشاكل
        for (QueryDocumentSnapshot document : documents) {
            treatment treatment = document.toObject(treatment.class);
            Map<Problem, Double> treatmentProblems = treatment.getProblems();

            // التحقق من أن جميع المشاكل في العلاج تتطابق مع المشاكل المقدمة
            boolean isMatch = problems.entrySet().stream()
                    .allMatch(entry -> treatmentProblems.containsKey(entry.getKey()) && treatmentProblems.get(entry.getKey()) > 0);

            if (isMatch) {
                return treatment;
            }
        }

        throw new RuntimeException("No treatment found for the given skin type and problems");
    }
}
