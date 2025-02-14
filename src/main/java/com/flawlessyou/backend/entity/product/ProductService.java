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

    public Product addProduct(Product product) throws ExecutionException, InterruptedException {
        DocumentReference docRef;
        
        if (product.getProductId() == null || product.getProductId().isEmpty()) {
            docRef = firestore.collection(COLLECTION_NAME).document(); 
            product.setProductId(docRef.getId());
        } else {
            docRef = firestore.collection(COLLECTION_NAME).document(product.getProductId());
        }
        
        docRef.set(product).get();
        return product;
    }

    public Product getProductById(String productId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(productId);
        DocumentSnapshot snapshot = docRef.get().get();
        return snapshot.exists() ? snapshot.toObject(Product.class) : null;
    }

    public Product addProductPhotos(String productId, List<String> photoUrls) 
    throws ExecutionException, InterruptedException {
    
    DocumentReference productRef = firestore.collection(COLLECTION_NAME).document(productId);
    
    if (!productRef.get().get().exists()) {
        throw new IllegalArgumentException("Product not found!");
    }
    
    productRef.update("photos", FieldValue.arrayUnion(photoUrls.toArray(new String[0]))).get();
    
    return productRef.get().get().toObject(Product.class);
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