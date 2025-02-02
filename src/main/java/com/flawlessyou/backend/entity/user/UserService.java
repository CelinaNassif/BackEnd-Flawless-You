package com.flawlessyou.backend.entity.user;

import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {
    
    @Autowired
    private Firestore firestore;
    
    private static final String COLLECTION_NAME = "users";

    public boolean existsByUsername(String username) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("username", username)
                .limit(1);
        
        return !query.get().get().isEmpty();
    }

    public boolean existsByEmail(String email) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("email", email)
                .limit(1);
        
        return !query.get().get().isEmpty();
    }

    public void saveUser(User user) throws ExecutionException, InterruptedException {
        DocumentReference docRef;
        if (user.getUserId() == null || user.getUserId().isEmpty()) {
            docRef = firestore.collection(COLLECTION_NAME).document();
            user.setUserId(docRef.getId());
        } else {
            docRef = firestore.collection(COLLECTION_NAME).document(user.getUserId());
        }
        docRef.set(user).get();
    }

    public Optional<User> findByUsername(String username) throws ExecutionException, InterruptedException {
        QuerySnapshot querySnapshot = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("username", username)
                .limit(1)
                .get()
                .get();
        
        return querySnapshot.isEmpty() ? 
                Optional.empty() : 
                Optional.of(querySnapshot.getDocuments().get(0).toObject(User.class));
    }

    public Optional<User> findByEmail(String email) throws ExecutionException, InterruptedException {
        QuerySnapshot querySnapshot = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .get();
        
        return querySnapshot.isEmpty() ? 
                Optional.empty() : 
                Optional.of(querySnapshot.getDocuments().get(0).toObject(User.class));
    }

    public User getUserById(String userId) throws ExecutionException, InterruptedException {
        DocumentSnapshot document = firestore.collection(COLLECTION_NAME)
                .document(userId)
                .get()
                .get();
        
        return document.exists() ? document.toObject(User.class) : null;
    }
}