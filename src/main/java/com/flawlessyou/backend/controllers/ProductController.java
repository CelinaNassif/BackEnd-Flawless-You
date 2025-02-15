package com.flawlessyou.backend.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestBody;

import com.flawlessyou.backend.config.GetUser;
import com.flawlessyou.backend.entity.cloudinary.CloudinaryResponse;
import com.flawlessyou.backend.entity.cloudinary.CloudinaryService;
import com.flawlessyou.backend.entity.product.Product;
import com.flawlessyou.backend.entity.product.ProductService;
import com.flawlessyou.backend.entity.user.User;
import com.flawlessyou.backend.util.FileUploadUtil;
import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.media.Content;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/product")
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

        @Autowired
    private ProductService productService;
  @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
 private GetUser getUser ;

    

@GetMapping("/random")
public ResponseEntity<?> getRandomProducts(
        @RequestParam(defaultValue = "6") int limit) { 
    try {
        List<Product> randomProducts = productService.getRandomProducts(limit);
        return ResponseEntity.ok(randomProducts);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
               .body("Error: " + e.getMessage());
    }
}


@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createProduct(
            @RequestBody Product product,
            HttpServletRequest request) {
        
        try {
            User admin = getUser.userFromToken(request);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }
            
            product.setAdminId(admin.getUserId());
            product.setPhotos(new ArrayList<>()); 
              product.setReviews(new HashMap<>());
            Product createdProduct = productService.addProduct(product);
            return ResponseEntity.ok(createdProduct);
            
        } catch (Exception e) {
            logger.error("Error creating product", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body("Error: " + e.getMessage());
        }
    }


     @PostMapping(value = "/{productId}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addPhotosToProduct(
            @PathVariable String productId,
            @RequestParam("files") List<MultipartFile> files,
            HttpServletRequest request) {
        
        try {
            User admin = getUser.userFromToken(request);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }

            Product existingProduct = productService.getProductById(productId);
            if (existingProduct == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
            }

            if (!existingProduct.getAdminId().equals(admin.getUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access");
            }

            List<String> photoUrls = new ArrayList<>();
            
            for (MultipartFile file : files) {
                FileUploadUtil.assertAllowed(file, FileUploadUtil.IMAGE_PATTERN);
                String fileName = productId + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
                CloudinaryResponse response = cloudinaryService.uploadFile(file, fileName);
                photoUrls.add(response.getUrl());
            }
            
            Product updatedProduct = productService.addProductPhotos(productId, photoUrls);
            return ResponseEntity.ok(updatedProduct);
            
        } catch (Exception e) {
            logger.error("Error adding photos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body("Error: " + e.getMessage());
        }
    }
    @PostMapping(value = "/{productId}/review")
    public ResponseEntity<?> addReview(
        @PathVariable String productId,
        @RequestBody int review,
        HttpServletRequest request) {
            try {
       Product product= productService.getProductById(productId);

       User admin = getUser.userFromToken(request);
       if (admin == null) {
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
       }
       if (product.getReviews() == null) {
        product.setReviews(new HashMap<>());
    }
      product.getReviews().put(admin.getUserId(),Integer.valueOf(review));
      productService.updateProduct(product);
       Map<String,Integer> reviews=product.getReviews();
       
            return ResponseEntity.ok(reviews);

    } catch (Exception e) {
            logger.error("Error adding photos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body("Error: " + e.getMessage());
        }}
    



        @GetMapping(value = "/{productId}/review")
        public ResponseEntity<?> getReviews(
            @PathVariable String productId,
            HttpServletRequest request) {
                try {
           Product product= productService.getProductById(productId);
           Map<String, Integer>  reviews= product.getReviews();
           if (reviews == null || reviews.isEmpty()) {
            return ResponseEntity.ok(0);
        }
        int sum = 0;
        for (int rating : reviews.values()) {
            sum += rating;
        }
        
        return ResponseEntity.ok( (double) sum / reviews.size());
       
    
        } catch (Exception e) {
                logger.error("Error adding photos", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                       .body("Error: " + e.getMessage());
            }}
        


            @DeleteMapping(value = "/{productId}/review")
            public ResponseEntity<?> deleteReview(
                @PathVariable String productId,
                HttpServletRequest request) {
                    try {
                        User user = getUser.userFromToken(request);
                        if (user == null) {
                            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
                        }
            
                        Product updatedProduct = productService.deleteReview(productId, user.getUserId());
            
                        return ResponseEntity.ok(updatedProduct);
            
                    } catch (Exception e) {
                        logger.error("Error deleting review", e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body("Error: " + e.getMessage());
                    }
            }
                    
            




            @PostMapping("/{productId}/savedProduct")
            public ResponseEntity<?> SavedProduct(
                    @PathVariable String productId,
                    HttpServletRequest request) {
                
                try {
                 
                    User user = getUser.userFromToken(request);
                    if (user == null) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
                    }
        
                    productService.toggleProductForUser(user.getUserId(), productId);
        
                    return ResponseEntity.ok("Product save status toggled successfully");
        
                } catch (Exception e) {
                    logger.error("Error toggling product save status", e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                           .body("Error: " + e.getMessage());
                }
            }



            @GetMapping("/{productId}/isSaved")
    public ResponseEntity<?> isProductSaved(
            @PathVariable String productId,
            HttpServletRequest request) {
        
        try {
            User user = getUser.userFromToken(request);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }

            boolean isSaved = productService.isProductSavedByUser(user.getUserId(), productId);

            return ResponseEntity.ok(isSaved);

        } catch (Exception e) {
            logger.error("Error checking if product is saved", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body("Error: " + e.getMessage());
        }
    }






    @GetMapping("/Saved")
    public ResponseEntity<?> getSavedProduct(
           
            HttpServletRequest request) {
        
        try {
            User user = getUser.userFromToken(request);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }

            List<Product> Saved = productService.getSavedProducts(user.getUserId());

            return ResponseEntity.ok(Saved);

        } catch (Exception e) {
            logger.error("Error in getting product", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body("Error: " + e.getMessage());
        }
    }








    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(  @PathVariable String productId) {
        
        try {

            productService.deleteProduct(productId);

            return ResponseEntity.ok("deleted successfully");

        } catch (Exception e) {
            logger.error("Error checking if product is deleted", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body("Error: " + e.getMessage());
        }
    }








    @PutMapping("/product")
    public ResponseEntity<?> editProduct(  @RequestBody Product product) {
        
        try {

           Product product2 = productService.updateProduct(product);

            return ResponseEntity.ok(product2);

        } catch (Exception e) {
            logger.error("Error checking if product is deleted", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body("Error: " + e.getMessage());
        }
    }











    
    
}
