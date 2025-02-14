package com.flawlessyou.backend.entity.product;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.Collections;
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


//     public String addProduct(Product product) throws ExecutionException, InterruptedException {
        
//     ApiFuture<DocumentReference> future = firestore.collection(COLLECTION_NAME).add(product);
//     DocumentReference docRef = future.get();
//     return docRef.getId();
// }
//     public void addProductToUserSaved(String userId, String productId) throws ExecutionException, InterruptedException {
//         DocumentReference userRef = firestore.collection("users").document(userId);
//         userRef.update("savedProductIds", FieldValue.arrayUnion(productId)).get();
//     }

public String addProduct(Product product) throws ExecutionException, InterruptedException {
    DocumentReference docRef;
    
    if (product.getProductId() == null || product.getProductId().isEmpty()) {
        docRef = firestore.collection(COLLECTION_NAME).document(); 
        product.setProductId(docRef.getId());
    } else {
        docRef = firestore.collection(COLLECTION_NAME).document(product.getProductId());
    }
    
    docRef.set(product).get();
    
    return docRef.getId();
}


public List<Product> getRandomProducts(int limit) throws ExecutionException, InterruptedException {
    Query query = firestore.collection(COLLECTION_NAME)
            .limit(limit); 

    ApiFuture<QuerySnapshot> future = query.get();
    List<QueryDocumentSnapshot> documents = future.get().getDocuments();

    List<Product> randomProducts = new ArrayList<>();
    for (QueryDocumentSnapshot document : documents) {
        randomProducts.add(document.toObject(Product.class));
    }

    return randomProducts;
}
}