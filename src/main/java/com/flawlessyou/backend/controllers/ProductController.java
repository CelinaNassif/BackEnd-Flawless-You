package com.flawlessyou.backend.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    

//     @PreAuthorize("hasRole('ADMIN')")
// @PostMapping(value = "/product", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
// public ResponseEntity<?> addProduct(
//         @RequestPart("product") Product product,
//         @RequestParam("files") List<MultipartFile> files) throws ExecutionException, InterruptedException {
    
//     if (product == null) {
//         return ResponseEntity.badRequest().body("Product data is required");
//     }
//     System.out.println("Received product: " + product.toString());
//     List<String> photoUrls = new ArrayList<>();
    
//     try {
//         for (MultipartFile file : files) {
//             System.out.println("Received file: " + file.getOriginalFilename());

//             FileUploadUtil.assertAllowed(file, FileUploadUtil.IMAGE_PATTERN);
            
//             String fileName = StringUtils.cleanPath(
//                 product.getProductId() + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename()
//             );
            
//             CloudinaryResponse response = cloudinaryService.uploadFile(file, fileName);
//             photoUrls.add(response.getUrl());
//         }
        
//         product.setPhotos(photoUrls);
//         String productId = productService.addProduct(product);
//         return ResponseEntity.ok(product+" "+productId);
        
//     } catch (Exception e) {
//         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body("File upload failed: " + e.getMessage());
//     }
// }

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


@PreAuthorize("hasRole('ADMIN')")
@PostMapping(value = "/product", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<?> addProduct(
       @RequestPart(value = "product", required = true) @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)) Product product,
        @RequestParam(value = "files", required = false) List<MultipartFile> files,
        HttpServletRequest request) { 
    if (product == null) {
        return ResponseEntity.badRequest().body("Product data is required");
    }

    try {
        User admin = getUser.userFromToken(request);
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        product.setAdminId(admin.getUserId());

        List<String> photoUrls = new ArrayList<>();

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                FileUploadUtil.assertAllowed(file, FileUploadUtil.IMAGE_PATTERN);
                String fileName = StringUtils.cleanPath(
                    product.getProductId() + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename()
                );
                CloudinaryResponse response = cloudinaryService.uploadFile(file, fileName);
                if (response != null && response.getUrl() != null) {
                    photoUrls.add(response.getUrl());
                }
            }
        }

        product.setPhotos(photoUrls);
        String productId = productService.addProduct(product);
        return ResponseEntity.ok(Collections.singletonMap("productId", productId));

    } catch (Exception e) {
        logger.error("Error adding product", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
               .body("Error: " + e.getMessage());
    }
}
}
