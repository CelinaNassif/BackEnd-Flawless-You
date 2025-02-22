package com.flawlessyou.backend.entity.routine;

import com.flawlessyou.backend.config.GetUser;
import com.flawlessyou.backend.entity.user.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Timestamp; // استيراد الفئة
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
            List<String> routineIds = user.getRoutineId();
            if (routineIds == null) {
                routineIds = new ArrayList<>();
            }
            routineIds.add(routine.getRoutineId());
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


public List<Routine> getAllRoutinesForUser(HttpServletRequest request) throws Exception {
    List<Routine> routines = new ArrayList<>();

    CollectionReference routinesRef = firestore.collection("routines");
    Query query = routinesRef.whereEqualTo("userId", getUser.userFromToken(request).getUserId()); 
    ApiFuture<QuerySnapshot> querySnapshot = query.get();

    for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
        routines.add(document.toObject(Routine.class));
    }

    return routines;
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

    public Routine getLastRoutineForUser(String userId) throws ExecutionException, InterruptedException {
        DocumentReference userRef = firestore.collection("users").document(userId);
        ApiFuture<DocumentSnapshot> userFuture = userRef.get();
        DocumentSnapshot userDocument = userFuture.get();
        if (userDocument.exists()) {
            User user = userDocument.toObject(User.class);
            if (user != null && user.getRoutineId() != null && !user.getRoutineId().isEmpty()) {
                String lastRoutineId = user.getRoutineId().get(user.getRoutineId().size() - 1);
                return getRoutineById(lastRoutineId);
            }
        }
        return null;
    }
}