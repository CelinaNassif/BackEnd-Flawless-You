package com.flawlessyou.backend.entity.product;

import com.flawlessyou.backend.controllers.ProductController;
import com.flawlessyou.backend.entity.user.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import java.util.stream.Collectors;
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
        product.setName(product.getName().toLowerCase());
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


// public List<Product> getRandomProducts(int limit) throws ExecutionException, InterruptedException {
//     Query query = firestore.collection(COLLECTION_NAME)
//             .limit(limit); 

//     ApiFuture<QuerySnapshot> future = query.get();
//     List<QueryDocumentSnapshot> documents = future.get().getDocuments();

//     List<Product> randomProducts = new ArrayList<>();
//     for (QueryDocumentSnapshot document : documents) {
//         randomProducts.add(document.toObject(Product.class));
//     }

//     return randomProducts;
// }



// public List<ProductWithSaveStatusDTO> getRandomProductsWithSaveStatus(int limit, User user) 
//     throws ExecutionException, InterruptedException {
    
//     Query query = firestore.collection(COLLECTION_NAME).limit(limit); 
//     ApiFuture<QuerySnapshot> future = query.get();
//     List<QueryDocumentSnapshot> documents = future.get().getDocuments();

//     List<ProductWithSaveStatusDTO> result = new ArrayList<>();
//     List<String> savedProductIds = user != null ? user.getSavedProductIds() : Collections.emptyList();

//     for (QueryDocumentSnapshot document : documents) {
//         Product product = document.toObject(Product.class);
//         boolean isSaved = savedProductIds.contains(product.getProductId()); 
//         result.add(new ProductWithSaveStatusDTO(product, isSaved));
//     }

//     return result;
// }
   private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

public List<ProductWithSaveStatusDTO> getRandomProductsWithSaveStatus(int limit, User user) {
    try {
        logger.debug("Fetching {} random products for user {}", limit, user != null ? user.getUserId() : "anonymous");
        
        // 1. جلب جميع المنتجات مرة واحدة
        List<Product> allProducts = getAllProducts();
        
        if (allProducts.isEmpty()) {
            logger.info("No products available in database");
            return Collections.emptyList();
        }
        
        // 2. استخدام Stream API لتحسين الأداء
        List<String> savedProductIds = Optional.ofNullable(user)
                .map(User::getSavedProductIds)
                .orElse(Collections.emptyList());
        
        logger.debug("User has {} saved products", savedProductIds.size());
        
        // 3. اختيار عشوائي باستخدام Stream
        List<ProductWithSaveStatusDTO> result = new Random()
                .ints(Math.min(limit, allProducts.size()), 0, allProducts.size())
                .distinct()
                .mapToObj(i -> allProducts.get(i))
                .map(product -> {
                    boolean isSaved = product.getProductId() != null && 
                                    savedProductIds.contains(product.getProductId());
                    return new ProductWithSaveStatusDTO(product, isSaved);
                })
                .collect(Collectors.toList());
        
        logger.info("Successfully returned {} random products", result.size());
        return result;
        
    } catch (Exception e) {
        logger.error("Error in getRandomProductsWithSaveStatus: {}", e.getMessage(), e);
        throw new RuntimeException("Failed to get random products", e);
    }
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






public List<ProductWithSaveStatusDTO> getSavedProducts(String userId) throws ExecutionException, InterruptedException {
    DocumentReference userRef = firestore.collection(USERS_COLLECTION).document(userId);
    DocumentSnapshot userSnapshot = userRef.get().get();
    
    List<String> savedProductIds = (List<String>) userSnapshot.get("savedProductIds");
    
    if (savedProductIds == null || savedProductIds.isEmpty()) {
        return Collections.emptyList(); 
    }
    
    List<ProductWithSaveStatusDTO> result = new ArrayList<>();
    
    for (String productId : savedProductIds) {
        DocumentReference productRef = firestore.collection(COLLECTION_NAME).document(productId);
        DocumentSnapshot productSnapshot = productRef.get().get();
        
        if (productSnapshot.exists()) {
            Product product = productSnapshot.toObject(Product.class);
            result.add(new ProductWithSaveStatusDTO(product, true));
        }
    }
    
    return result;
}





public Integer getUserReviewForProduct(String productId, String userId) throws ExecutionException, InterruptedException {
    if (productId == null || productId.isEmpty()) {
        throw new IllegalArgumentException("Product ID cannot be null or empty");
    }

    DocumentReference productRef = firestore.collection(COLLECTION_NAME).document(productId);
    DocumentSnapshot snapshot = productRef.get().get();

    if (!snapshot.exists()) {
        throw new IllegalArgumentException("Product not found!");
    }
    Map<String, Object> reviews = (Map<String, Object>) snapshot.get("reviews");

    if (reviews == null || reviews.isEmpty()) {
        return 0;
    }
    Object userReview = reviews.get(userId);

    if (userReview instanceof Long) {
        return ((Long) userReview).intValue();
    } else if (userReview instanceof Integer) {
        return (Integer) userReview; 
    } else {
        return 0; 
    }
}
public List<Product> getAllProducts() throws ExecutionException, InterruptedException {
    ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
    
    List<QueryDocumentSnapshot> documents = future.get().getDocuments();
    
    List<Product> products = new ArrayList<>();
    for (QueryDocumentSnapshot document : documents) {
        products.add(document.toObject(Product.class));
    }
    
    return products;
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



public List<ProductWithSaveStatusDTO> searchProductsByName(String searchTerm, User user) 
    throws ExecutionException, InterruptedException {

    String lowerCaseSearchTerm = searchTerm.toLowerCase();

    Query query = firestore.collection(COLLECTION_NAME)
            .whereGreaterThanOrEqualTo("name", lowerCaseSearchTerm)
            .whereLessThanOrEqualTo("name", lowerCaseSearchTerm + "\uf8ff");

    ApiFuture<QuerySnapshot> future = query.get();
    List<QueryDocumentSnapshot> documents = future.get().getDocuments();

    List<ProductWithSaveStatusDTO> result = new ArrayList<>();
    List<String> savedProductIds = user != null ? user.getSavedProductIds() : Collections.emptyList();

    for (QueryDocumentSnapshot document : documents) {
        Product product = document.toObject(Product.class);
        boolean isSaved = savedProductIds.contains(product.getProductId());
        result.add(new ProductWithSaveStatusDTO(product, isSaved));
    }

    return result;
}


public Product updateUserReview(String productId, String userId, int newRating) 
    throws ExecutionException, InterruptedException {
    
    if (productId == null || productId.isEmpty()) {
        throw new IllegalArgumentException("Product ID cannot be null or empty");
    }

    DocumentReference productRef = firestore.collection(COLLECTION_NAME).document(productId);
    DocumentSnapshot snapshot = productRef.get().get();

    if (!snapshot.exists()) {
        throw new IllegalArgumentException("Product not found!");
    }

    Map<String, Object> reviews = (Map<String, Object>) snapshot.get("reviews");

    if (reviews == null) {
        reviews = new HashMap<>();
    }

    reviews.put(userId, newRating);

    productRef.update("reviews", reviews).get();

    return productRef.get().get().toObject(Product.class);
}
public Product removeProductPhotoByIndex(String productId, int index) 
    throws ExecutionException, InterruptedException {
    
    DocumentReference productRef = firestore.collection(COLLECTION_NAME).document(productId);
    DocumentSnapshot snapshot = productRef.get().get();
    
    if (!snapshot.exists()) {
        throw new IllegalArgumentException("Product not found");
    }
    
    List<String> photos = (List<String>) snapshot.get("photos");
    
    if (photos == null || photos.isEmpty()) {
        throw new IllegalArgumentException("No photos available");
    }
    
    if (index < 0 || index >= photos.size()) {
        throw new IndexOutOfBoundsException("Invalid photo index");
    }
    
    String photoUrl = photos.get(index);
    photos.remove(index);
    
    // تحديث مباشر للقائمة الجديدة بدون استخدام arrayRemove
    productRef.update("photos", photos).get();
    
    return productRef.get().get().toObject(Product.class);
}
public String getProductIdByName(String productName) throws Exception {
        if (productName == null || productName.isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }

        try {
            List<QueryDocumentSnapshot> documents = firestore.collection("products")
                    .whereEqualTo("name", productName)
                    .limit(1)
                    .get()
                    .get()
                    .getDocuments();

            if (!documents.isEmpty()) {
                return documents.get(0).getId(); // أو getString("productId") حسب التخزين
            } else {
                throw new RuntimeException("Product not found with name: " + productName);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new Exception("Failed to fetch product ID by name: " + e.getMessage());
        }
    }


}