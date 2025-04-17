package com.flawlessyou.backend.entity.routine;

import com.flawlessyou.backend.config.GetUser;
import com.flawlessyou.backend.entity.product.Product;
import com.flawlessyou.backend.entity.user.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.sql.Timestamp; // استيراد الفئة
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.Map;

@Service
public class RoutineService {
    @Autowired
    private Firestore firestore;
  
    @Autowired
    private GetUser getUser;
    

    private static final String COLLECTION_NAME = "routines";

    public Routine createRoutine(HttpServletRequest request, Routine routine) throws Exception {
        User user = getUser.userFromToken(request);
        if (user == null || user.getUserId() == null || user.getUserId().isEmpty()) {
            throw new IllegalArgumentException("Invalid user or user ID.");
        }
       
        routine.setUserId(user.getUserId());
    
        if (routine.getRoutineId() == null || routine.getRoutineId().isEmpty()) {
            throw new IllegalArgumentException("Routine ID must be a non-empty string.");
        }
    
        if (firestore == null) {
            throw new IllegalStateException("Firestore is not initialized.");
        }
    
        try {
            // Save the routine to Firestore
            ApiFuture<WriteResult> future = firestore.collection(COLLECTION_NAME).document(routine.getRoutineId()).set(routine);
            future.get(); // Wait for the operation to complete
    
            // Update the user's routine IDs
            String routineIds = routine.getRoutineId();
           
            user.setRoutineId(routineIds);
    
            // Save the updated user back to the database
            firestore.collection("users").document(user.getUserId()).set(user).get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create routine", e);
        }
    
        return routine;
    }


     // 1. استرجاع الروتين بواسطة routineId
     public Routine getRoutineById(String routineId) throws ExecutionException, InterruptedException {
        if (routineId == null || routineId.isEmpty()) {
            throw new IllegalArgumentException("Routine ID must be a non-empty string.");
        }
    
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(routineId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
    
        if (document.exists()) {
            return document.toObject(Routine.class);
        } else {
            // logger.error("Routine not found with ID: " + routineId);
            throw new RuntimeException("Routine not found with ID: " + routineId);
        }
    }


    public ResponseEntity<Routine> getRoutineUser(HttpServletRequest request) {
        try {
            Routine routine = getRoutineUserInternal(request);
            return ResponseEntity.ok(routine);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private Routine getRoutineUserInternal(HttpServletRequest request) throws Exception {
        String userId = getUser.userFromToken(request).getUserId();
        CollectionReference routinesRef = firestore.collection("routines");
        Query query = routinesRef.whereEqualTo("userId", userId);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        if (!documents.isEmpty()) {
            QueryDocumentSnapshot document = documents.get(0);
            return document.toObject(Routine.class);
        } else {
            throw new Exception("No routine found for the given user ID");
        }
    }

    
    // public String getAnalysisById(String analysisId) throws ExecutionException, InterruptedException {
    //     DocumentReference docRef = firestore.collection("analyses").document(analysisId);
    //     ApiFuture<DocumentSnapshot> future = docRef.get();
    //     DocumentSnapshot document = future.get();
    //     if (document.exists()) {
    //         return document.getString("analysisData"); // افتراض أن التحليل مخزن كحقل "analysisData"
    //     }
    //     return null;
    // }

 


//    public Map<RoutineTime, List<Product>> getRoutineWithProductsByTime(HttpServletRequest request) throws Exception {
//     // 1. التحقق من المستخدم والروتين
//     User user = getUser.userFromToken(request);
//     if (user == null) throw new IllegalArgumentException("User not found or invalid token.");
    
//     String routineId = user.getRoutineId();
//     if (routineId == null || routineId.isEmpty()) {
//         throw new IllegalArgumentException("Routine ID must be a non-empty string.");
//     }

//     // 2. جلب الروتين (مخزن مؤقت إذا أمكن)
//     Routine routine = getRoutineById(routineId);
//     if (routine == null) throw new RuntimeException("Routine not found with ID: " + routineId);
    
//     if (routine.getProductIds() == null || routine.getProductIds().isEmpty()) {
//         return initializeEmptyTimeMap();
//     }

//     // 3. جلب المنتجات في استعلام واحد مع التجميع
//     List<ApiFuture<QuerySnapshot>> futures = new ArrayList<>();
//     Map<RoutineTime, List<Product>> result = new EnumMap<>(RoutineTime.class);
    
//     for (RoutineTime time : RoutineTime.values()) {
//         result.put(time, new ArrayList<>());
        
//         // إنشاء استعلام لكل وقت استخدام
//         Query query = firestore.collection("products")
//             .whereIn("productId", routine.getProductIds())
//             .whereArrayContains("usageTime", time);
        
//         futures.add(query.get());
//     }

//     // 4. معالجة النتائج بشكل متوازي
//     for (int i = 0; i < futures.size(); i++) {
//         RoutineTime time = RoutineTime.values()[i];
//         List<QueryDocumentSnapshot> docs = futures.get(i).get().getDocuments();
        
//         for (QueryDocumentSnapshot doc : docs) {
//             result.get(time).add(doc.toObject(Product.class));
//         }
//     }

//     return result;
// }

// private Map<RoutineTime, List<Product>> initializeEmptyTimeMap() {
//     Map<RoutineTime, List<Product>> map = new EnumMap<>(RoutineTime.class);
//     for (RoutineTime time : RoutineTime.values()) {
//         map.put(time, new ArrayList<>());
//     }
//     return map;
// }






public Map<RoutineTime, List<Product>> getRoutineWithProductsByTime(HttpServletRequest request) throws Exception {
    try {
        // 1. التحقق من المستخدم - O(1)
        User user = getUser.userFromToken(request);
        if (user == null) {
            throw new IllegalArgumentException("User not found or invalid token.");
        }

        // 2. التحقق من وجود الروتين - O(1)
        String routineId = user.getRoutineId();
        if (routineId == null || routineId.isEmpty()) {
            throw new IllegalArgumentException("Routine ID must be a non-empty string.");
        }

        // 3. جلب الروتين - O(1) (يفترض وجود cache)
        Routine routine = getRoutineById(routineId);
        if (routine == null) {
            throw new RuntimeException("Routine not found with ID: " + routineId);
        }

        // 4. التحقق من وجود منتجات - O(1)
        if (routine.getProductIds() == null || routine.getProductIds().isEmpty()) {
            return initializeEmptyTimeMap();
        }

        // 5. تقسيم المنتجات إلى مجموعات لتجنب حدود Firestore (10 لكل مجموعة)
        List<List<String>> productIdBatches = partitionList(routine.getProductIds(), 10);

        // 6. تنفيذ استعلامات متوازية لكل مجموعة
        List<ApiFuture<QuerySnapshot>> futures = new ArrayList<>();
        for (List<String> batch : productIdBatches) {
            Query query = firestore.collection("products")
                .whereIn("productId", batch);
            futures.add(query.get());
        }

        // 7. معالجة النتائج وتصنيفها حسب الوقت
        Map<RoutineTime, List<Product>> productsByTime = initializeEmptyTimeMap();
        
        for (ApiFuture<QuerySnapshot> future : futures) {
            List<QueryDocumentSnapshot> productDocuments = future.get().getDocuments();
            
            for (QueryDocumentSnapshot doc : productDocuments) {
                Product product = doc.toObject(Product.class);
                if (product.getUsageTime() != null) {
                    for (RoutineTime time : product.getUsageTime()) {
                        productsByTime.get(time).add(product);
                    }
                }
            }
        }

        return productsByTime;
    } catch (Exception e) {
        throw e;
    }
}

// دالة مساعدة لتهيئة خريطة الأوقات الفارغة
private Map<RoutineTime, List<Product>> initializeEmptyTimeMap() {
    Map<RoutineTime, List<Product>> map = new EnumMap<>(RoutineTime.class);
    for (RoutineTime time : RoutineTime.values()) {
        map.put(time, new ArrayList<>());
    }
    return map;
}

// دالة مساعدة لتقسيم القوائم
private <T> List<List<T>> partitionList(List<T> list, int batchSize) {
    List<List<T>> batches = new ArrayList<>();
    for (int i = 0; i < list.size(); i += batchSize) {
        batches.add(list.subList(i, Math.min(i + batchSize, list.size())));
    }
    return batches;
}

}