package com.flawlessyou.backend.entity.routine;

import com.flawlessyou.backend.config.GetUser;
import com.flawlessyou.backend.entity.user.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
        routine.setUserId(user.getUserId());

        // Generate a new routineId if not already set
        if (routine.getRoutineId() == null) {
            routine.setRoutineId(UUID.randomUUID().toString());
        }

        // Save the routine to Firestore
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION_NAME).document(routine.getRoutineId()).set(routine);
        future.get(); // Wait for the operation to complete

        // Update the user's routine IDs
        if (user != null) {
            List<String> routineIds = user.getRoutineId();
            if (routineIds == null) {
                routineIds = new ArrayList<>();
            }
            routineIds.add(routine.getRoutineId());
            user.setRoutineId(routineIds);

            // Save the updated user back to the database
            firestore.collection("users").document(user.getUserId()).set(user).get();
        }

        return routine;
    }

    // 1. Retrieve routine by routineId
    public Routine getRoutineById(String routineId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(routineId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            return document.toObject(Routine.class);
        }
        return null;
    }


//     public String getAnalysisById(String analysisId) throws ExecutionException, InterruptedException {
//         DocumentReference docRef = firestore.collection("analyses").document(analysisId);
//         ApiFuture<DocumentSnapshot> future = docRef.get();
//         DocumentSnapshot document = future.get();
//         if (document.exists()) {
//             return document.getString("analysisData"); // افتراض أن التحليل مخزن كحقل "analysisData"
//         }
//         return null;
// }


}