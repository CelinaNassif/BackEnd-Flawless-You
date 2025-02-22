package com.flawlessyou.backend.entity.routine;

import com.flawlessyou.backend.config.GetUser;
import com.flawlessyou.backend.entity.routine.Routine;
import com.flawlessyou.backend.entity.user.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ExecutionException;
import com.google.cloud.firestore.*;

@Service
public class RoutineService {
    @Autowired
    private Firestore firestore;
  
    @Autowired
    private GetUser getUser;
    

    private static final String COLLECTION_NAME = "routines";

//     public Routine createRoutine(HttpServletRequest request , Routine routine)
// throws Exception {
// User user = getUser.userFromToken(request);
// routine.setUserId(user.getUserId());

// // Save the routine to Firestore
// ApiFuture<WriteResult> future = firestore.collection(COLLECTION_NAME).document(routine.getRoutineId()).set(routine);
// future.get(); // Wait for the operation to complete

// // Update the user's routine IDs
// //User user = getUser.userFromToken(request);
// if (user != null) {
// List<String> routineIds = user.getRoutineId();
// if (routineIds == null) {
// routineIds = new ArrayList<>();
// }
// routineIds.add(routine.getRoutineId());
// user.setRoutineId(routineIds);

// // Save the updated user back to the database
// // Assuming you have a method to update the user in Firestore
// firestore.collection("users").document(user.getUserId()).set(user).get();
// }

// return routine;
// }

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
        DocumentReference docRef = firestore.collection("routines").document(routineId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            return document.toObject(Routine.class);
        }
        return null;
    }


}