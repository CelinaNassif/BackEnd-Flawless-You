package com.flawlessyou.backend.entity.product;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.Collections;
import java.util.HashMap;
@Service
public class ProductService {
    
    @Autowired
    private Firestore firestore;
    
    private static final String COLLECTION_NAME = "products";
    private static final String USERS_COLLECTION = "users";

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

public Product updateProduct(Product product) throws ExecutionException, InterruptedException {
    if (product.getProductId() == null || product.getProductId().isEmpty()) {
        throw new IllegalArgumentException("Product ID cannot be null or empty");
    }

    DocumentReference productRef = firestore.collection(COLLECTION_NAME).document(product.getProductId());

    if (!productRef.get().get().exists()) {
        throw new IllegalArgumentException("Product not found!");
    }

    Map<String, Object> updates = new HashMap<>();

   
    if (product.getName() != null) {
        updates.put("name", product.getName());
    }
    if (product.getSkinType() != null) {
        updates.put("skinType", product.getSkinType());
    }
    if (product.getDescription() != null) {
        updates.put("description", product.getDescription());
    }
    if (product.getIngredients() != null) {
        updates.put("ingredients", product.getIngredients());
    }
    if (product.getAdminId() != null) {
        updates.put("adminId", product.getAdminId());
    }
    if (product.getPhotos() != null) {
        updates.put("photos", product.getPhotos());
    }
    if (product.getReviews() != null) {
        updates.put("reviews", product.getReviews());
    }

    if (!updates.isEmpty()) {
        productRef.update(updates).get();
    }

    return productRef.get().get().toObject(Product.class);
}





public Product deleteReview(String productId, String userId) throws ExecutionException, InterruptedException {
    if (productId == null || productId.isEmpty()) {
        throw new IllegalArgumentException("Product ID cannot be null or empty");
    }
    DocumentReference productRef = firestore.collection(COLLECTION_NAME).document(productId);

    DocumentSnapshot snapshot = productRef.get().get();
    if (!snapshot.exists()) {
        throw new IllegalArgumentException("Product not found!");
    }

    Map<String, Integer> reviews = (Map<String, Integer>) snapshot.get("reviews");

    if (reviews == null || reviews.isEmpty()) {
        throw new IllegalArgumentException("No reviews found for this product");
    }

    if (!reviews.containsKey(userId)) {
        throw new IllegalArgumentException("User has not reviewed this product");
    }

    reviews.remove(userId);

    productRef.update("reviews", reviews).get();

    return productRef.get().get().toObject(Product.class);
}


public void toggleProductForUser(String userId, String productId) 
    throws ExecutionException, InterruptedException {

    DocumentReference userRef = firestore.collection(USERS_COLLECTION).document(userId);
    DocumentSnapshot userSnapshot = userRef.get().get();
    List<String> savedProductIds = (List<String>) userSnapshot.get("savedProductIds");

    if (savedProductIds == null) {
        savedProductIds = new ArrayList<>();
    }

    if (savedProductIds.contains(productId)) {
        savedProductIds.remove(productId); 
    } else {
        savedProductIds.add(productId); 
    }

    userRef.update("savedProductIds", savedProductIds).get();
}


public boolean isProductSavedByUser(String userId, String productId) 
throws ExecutionException, InterruptedException {

DocumentReference userRef = firestore.collection(USERS_COLLECTION).document(userId);

DocumentSnapshot userSnapshot = userRef.get().get();
List<String> savedProductIds = (List<String>) userSnapshot.get("savedProductIds");

if (savedProductIds == null || savedProductIds.isEmpty()) {
    return false; 
}

return savedProductIds.contains(productId);
}






public List<Product> getSavedProducts(String userId) throws ExecutionException, InterruptedException {
    DocumentReference userRef = firestore.collection(USERS_COLLECTION).document(userId);
    DocumentSnapshot userSnapshot = userRef.get().get();
    
    List<String> savedProductIds = (List<String>) userSnapshot.get("savedProductIds");
    
    if (savedProductIds == null || savedProductIds.isEmpty()) {
        return Collections.emptyList(); 
    }
    
    List<Product> savedProducts = new ArrayList<>();
    
    for (String productId : savedProductIds) {
        DocumentReference productRef = firestore.collection(COLLECTION_NAME).document(productId);
        DocumentSnapshot productSnapshot = productRef.get().get();
        
        if (productSnapshot.exists()) {
            savedProducts.add(productSnapshot.toObject(Product.class));
        }
    }
    
    return savedProducts;
}









public void deleteProduct(String productId) throws ExecutionException, InterruptedException {
    DocumentReference productRef = firestore.collection(COLLECTION_NAME).document(productId);
    DocumentSnapshot productSnapshot = productRef.get().get();
    
    if (productSnapshot.exists()) {
        productRef.delete().get();
        System.out.println("Product with ID " + productId + " has been deleted.");
    } else {
        System.out.println("Product with ID " + productId + " does not exist.");
    }
}





















}