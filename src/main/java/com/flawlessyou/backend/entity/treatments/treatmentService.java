package com.flawlessyou.backend.entity.treatments;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flawlessyou.backend.config.GetUser;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class treatmentService {
       @Autowired
    private Firestore firestore;
    
    @Autowired
    private GetUser getUser;

    private static final String COLLECTION_NAME = "treatment";


    // إنشاء علاج جديد
    public String createTreatment(treatment treatment) throws ExecutionException, InterruptedException {
        DocumentReference addedDocRef = firestore.collection(COLLECTION_NAME).document();
        treatment.setTreatmentId(addedDocRef.getId());
        ApiFuture<WriteResult> future = addedDocRef.set(treatment);
        return future.get().getUpdateTime().toString();
    }

        // قراءة علاج معين بواسطة الـ ID
    public treatment getTreatment(String treatmentId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(treatmentId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            return document.toObject(treatment.class);
        } else {
            return null;
        }
    }

        // تعديل علاج موجود
        public String updateTreatment(String treatmentId, treatment updatedTreatment) throws ExecutionException, InterruptedException {
            // 1. التأكد من أن الـ treatmentId غير فارغ
            if (treatmentId == null || treatmentId.isEmpty()) {
                throw new IllegalArgumentException("Treatment ID must be a non-empty string.");
            }
        
            // 2. استرجاع العلاج الحالي من Firestore
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(treatmentId);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
        
            if (!document.exists()) {
                throw new RuntimeException("Treatment not found with ID: " + treatmentId);
            }
        
            // 3. تحميل العلاج الحالي
            treatment existingTreatment = document.toObject(treatment.class);
        
            // 4. تحديث الحقول المطلوبة
            if (updatedTreatment.getSkinType() != null) {
                existingTreatment.setSkinType(updatedTreatment.getSkinType());
            }
            if (updatedTreatment.getProblem() != null) {
                existingTreatment.setProblem(updatedTreatment.getProblem());
            }
            if (updatedTreatment.getProductIds() != null) {
                existingTreatment.setProductIds(updatedTreatment.getProductIds());
            }
        
            // 5. حفظ العلاج المحدث في Firestore
            ApiFuture<WriteResult> updateFuture = docRef.set(existingTreatment);
            return updateFuture.get().getUpdateTime().toString();
        }
}
