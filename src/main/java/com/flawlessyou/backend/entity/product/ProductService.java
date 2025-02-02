package com.flawlessyou.backend.entity.product;

import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class ProductService {
    
    @Autowired
    private Firestore firestore;
    
    private static final String COLLECTION_NAME = "products";

    public List<Product> getProductsBySkinType(String skinType) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("skinType", skinType);
        
        return query.get().get().toObjects(Product.class);
    }

    public void addProductToUserSaved(String userId, String productId) throws ExecutionException, InterruptedException {
        DocumentReference userRef = firestore.collection("users").document(userId);
        userRef.update("savedProductIds", FieldValue.arrayUnion(productId)).get();
    }
}