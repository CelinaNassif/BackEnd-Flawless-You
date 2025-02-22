package com.flawlessyou.backend.entity.routine;

import com.flawlessyou.backend.config.GetUser;
import com.flawlessyou.backend.entity.product.Product;
import com.flawlessyou.backend.entity.user.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
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


    public Routine getRoutineUser(HttpServletRequest request) throws Exception {
      
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

 



    public Map<RoutineTime, List<Product>> getRoutineWithProductsByTime(HttpServletRequest request) throws Exception {
        User user = getUser.userFromToken(request);
        String routineId = user.getRoutineId();
        
        if (routineId == null || routineId.isEmpty()) {
            throw new IllegalArgumentException("Routine ID must be a non-empty string.");
        }
        
        // استرجاع الروتين من Firestore
        Routine routine = getRoutineById(routineId);
        if (routine == null) {
            throw new RuntimeException("Routine not found with ID: " + routineId);
        }
        
        // استرجاع جميع المنتجات من Firestore
        CollectionReference productsRef = firestore.collection("products");
        ApiFuture<QuerySnapshot> productsFuture = productsRef.get();
        List<QueryDocumentSnapshot> productDocuments = productsFuture.get().getDocuments();
        
        // إنشاء Map لتخزين المنتجات المصنفة حسب الوقت
        Map<RoutineTime, List<Product>> productsByTime = new EnumMap<>(RoutineTime.class);
        productsByTime.put(RoutineTime.MORNING, new ArrayList<>());
        productsByTime.put(RoutineTime.AFTERNOON, new ArrayList<>());
        productsByTime.put(RoutineTime.NIGHT, new ArrayList<>());
        
        // تصنيف المنتجات حسب الوقت
        for (QueryDocumentSnapshot productDocument : productDocuments) {
            Product product = productDocument.toObject(Product.class);
            if (routine.getProductIds().contains(product.getProductId())) {
                List<RoutineTime> usageTimes = product.getUsageTime();
                if (usageTimes != null) {
                    for (RoutineTime time : usageTimes) {
                        switch (time) {
                            case MORNING:
                                productsByTime.get(RoutineTime.MORNING).add(product);
                                break;
                            case AFTERNOON:
                                productsByTime.get(RoutineTime.AFTERNOON).add(product);
                                break;
                            case NIGHT:
                                productsByTime.get(RoutineTime.NIGHT).add(product);
                                break;
                        }
                    }
                }
            }
        }
        
        // إرجاع Map يحتوي على المنتجات المصنفة حسب الوقت
        return productsByTime;
    }






}