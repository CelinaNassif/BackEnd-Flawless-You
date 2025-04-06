package com.flawlessyou.backend.entity.treatments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flawlessyou.backend.config.GetUser;
import com.flawlessyou.backend.entity.product.Product;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class TreatmentService {
       @Autowired
    private Firestore firestore;
    
    @Autowired
    private GetUser getUser;

    private static final String COLLECTION_NAME = "treatment";


    // إنشاء علاج جديد
    public String createTreatment(Treatment treatment) throws ExecutionException, InterruptedException {
        DocumentReference addedDocRef = firestore.collection(COLLECTION_NAME).document();
        treatment.setTreatmentId(addedDocRef.getId());
        ApiFuture<WriteResult> future = addedDocRef.set(treatment);
        return future.get().getUpdateTime().toString();
    }

        // قراءة علاج معين بواسطة الـ ID
    public Treatment getTreatment(String treatmentId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(treatmentId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            return document.toObject(Treatment.class);
        } else {
            return null;
        }
    }

        // تعديل علاج موجود
        public String updateTreatment(String treatmentId, Treatment updatedTreatment) throws ExecutionException, InterruptedException {
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
            Treatment existingTreatment = document.toObject(Treatment.class);
        
            // 4. تحديث الحقول المطلوبة
            if (updatedTreatment.getSkinType() != null) {
                existingTreatment.setSkinType(updatedTreatment.getSkinType());
            }
            if (updatedTreatment.getdescription() != null) {
                existingTreatment.setdescription(updatedTreatment.getdescription());
            }
            if (updatedTreatment.getProductIds() != null) {
                existingTreatment.setProductIds(updatedTreatment.getProductIds());
            }
        
            // 5. حفظ العلاج المحدث في Firestore
            ApiFuture<WriteResult> updateFuture = docRef.set(existingTreatment);
            return updateFuture.get().getUpdateTime().toString();
        }


   // حذف علاج بواسطة الـ ID
        public String deleteTreatment(String treatmentId) throws ExecutionException, InterruptedException {
            ApiFuture<WriteResult> future = firestore.collection(COLLECTION_NAME).document(treatmentId).delete();
            return future.get().getUpdateTime().toString();
        }

        // جلب كل العلاجات
    public List<Treatment> getAllTreatments() throws ExecutionException, InterruptedException {
        List<Treatment> treatments = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        for (QueryDocumentSnapshot document : documents) {
            treatments.add(document.toObject(Treatment.class));
        }
        return treatments;
    }

       // جلب العلاجات حسب نوع البشرة
       public List<Treatment> getTreatmentsBySkinType(String skinType) throws ExecutionException, InterruptedException {
        List<Treatment> treatments = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).whereEqualTo("skinType", skinType).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        for (QueryDocumentSnapshot document : documents) {
            treatments.add(document.toObject(Treatment.class));
        }
        return treatments;
    }

    public List<Product> getProductsForTreatment(String treatmentId) throws ExecutionException, InterruptedException {
        // 1. Get treatment by treatmentId
        Treatment treatment = getTreatment(treatmentId);
        if (treatment == null) {
            throw new RuntimeException("Treatment not found with ID: " + treatmentId);
        }
    
        // 2. Get productIds map from treatment
        Map<String, String> productIds = treatment.getProductIds();
        if (productIds == null || productIds.isEmpty()) {
            return new ArrayList<>(); // Return empty list if no products
        }
    
        // 3. Fetch products from Firestore based on productIds (map keys)
        List<Product> products = new ArrayList<>();
        for (String productId : productIds.keySet()) {  // Iterate through map keys
            DocumentReference docRef = firestore.collection("products").document(productId);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
    
            if (document.exists()) {
                products.add(document.toObject(Product.class));
            }
        }
    
        return products;
    }
public String addProductToTreatment(String treatmentId, String productId, String productName) throws ExecutionException, InterruptedException {
    Treatment treatment = getTreatment(treatmentId);
    if (treatment == null) {
        throw new RuntimeException("Treatment not found with ID: " + treatmentId);
    }

    Map<String, String> productIds = treatment.getProductIds();
    if (productIds == null) {
        productIds = new HashMap<>(); // Initialize as HashMap instead of ArrayList
    }

    // Put the productId as key and productName as value in the map
    if (!productIds.containsKey(productId)) {
        productIds.put(productId, productName);
        treatment.setProductIds(productIds);
    }

    ApiFuture<WriteResult> future = firestore.collection(COLLECTION_NAME).document(treatmentId).set(treatment);
    return future.get().getUpdateTime().toString();
}
public String removeProductFromTreatment(String treatmentId, String productId) throws ExecutionException, InterruptedException {
    Treatment treatment = getTreatment(treatmentId);
    if (treatment == null) {
        throw new RuntimeException("Treatment not found with ID: " + treatmentId);
    }

    Map<String, String> productIds = treatment.getProductIds();
    if (productIds != null && productIds.containsKey(productId)) {
        productIds.remove(productId);  // Remove by key from the map
        treatment.setProductIds(productIds);
    }

    ApiFuture<WriteResult> future = firestore.collection(COLLECTION_NAME).document(treatmentId).set(treatment);
    return future.get().getUpdateTime().toString();
}
}
