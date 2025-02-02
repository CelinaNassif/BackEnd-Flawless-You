package com.flawlessyou.backend.entity.user;
// entity/user/UserService.java

import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {
    
    @Autowired
    private Firestore firestore;
    
    private static final String COLLECTION_NAME = "users";

    public void createUser(User user) throws ExecutionException, InterruptedException {
        firestore.collection(COLLECTION_NAME)
                .document(user.getUserId())
                .set(user)
                .get();
    }

    public User getUserById(String userId) throws ExecutionException, InterruptedException {
        DocumentSnapshot document = firestore.collection(COLLECTION_NAME)
                .document(userId)
                .get()
                .get();
        
        return document.toObject(User.class);
    }

    // public List<User> getUsersBySkinType(String skinType) throws ExecutionException, InterruptedException {
    //     Query query = firestore.collection(COLLECTION_NAME)
    //             .whereEqualTo("skinType", skinType);
        
    //     return query.get().get().toObjects(User.class);
    // }
}
