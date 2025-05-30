package com.flawlessyou.backend.entity.user;

import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class UserService {
    
    @Autowired
    private Firestore firestore;
    
    private static final String COLLECTION_NAME = "users";

    public boolean existsByUsername(String username) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("userName", username)
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
                .whereEqualTo("userName", username)
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
    public void addProfilePicture(String userId, String profilePictureUrl) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(userId);
        docRef.update("profilePicture", profilePictureUrl).get();
    }









public List<User> getUsersByRole(Role role) throws ExecutionException, InterruptedException {
    Query query = firestore.collection(COLLECTION_NAME)
            .whereEqualTo("role", role);
    
    QuerySnapshot querySnapshot = query.get().get();
    
    return querySnapshot.getDocuments().stream()
            .map(document -> document.toObject(User.class))
            .collect(Collectors.toList());
}








public List<Map<String, String>> getUsersByUsername(String searchText) throws ExecutionException, InterruptedException {
    Query query = firestore.collection(COLLECTION_NAME)
            .whereGreaterThanOrEqualTo("userName", searchText)
            .whereLessThanOrEqualTo("userName", searchText + "\uf8ff");
    
    QuerySnapshot querySnapshot = query.get().get();
    
    return querySnapshot.getDocuments().stream()
            .map(document -> {
                Map<String, String> userInfo = new HashMap<>();
                userInfo.put("id", document.getId());
                userInfo.put("userName", document.getString("userName"));
                userInfo.put("role", document.getString("role")); // Assuming 'role' is the field name in Firestore
                return userInfo;
            })
            .collect(Collectors.toList());
}
public void updateUserRole(String userId, Role newRole) throws ExecutionException, InterruptedException {
    if (userId == null || userId.isEmpty() || newRole == null) {
        throw new IllegalArgumentException("User ID and Role must not be null or empty.");
    }

    DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(userId);

    docRef.update("role", newRole).get();
}


public void updateUserInfo(String userId, String phoneNumber, Gender gender) throws ExecutionException, InterruptedException {
    // التحقق من صحة البيانات
    if (userId == null || userId.isEmpty()) {
        throw new IllegalArgumentException("User ID must not be null or empty.");
    }

    // إنشاء Map لتحديث الحقول
    Map<String, Object> updates = new HashMap<>();
    if (phoneNumber != null) {
        updates.put("phoneNumber", phoneNumber);
    }
    if (gender != null) {
        updates.put("gender", gender);
    }

    // إذا كانت هناك حقول لتحديثها
    if (!updates.isEmpty()) {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(userId);
        docRef.update(updates).get(); // تنفيذ التحديث
    }
}



    
}