package com.flawlessyou.backend.entity.treatments;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flawlessyou.backend.config.GetUser;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
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

}
