package com.flawlessyou.backend.controllers;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flawlessyou.backend.config.GetUser;
import com.flawlessyou.backend.entity.product.Product;
import com.flawlessyou.backend.entity.product.ProductWithSaveStatusDTO;
import com.flawlessyou.backend.entity.treatments.Treatment;
import com.flawlessyou.backend.entity.treatments.TreatmentService;
import com.flawlessyou.backend.entity.user.User;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestBody;
// import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api/treatments")
public class TreatmentController {
     @Autowired
    private TreatmentService treatmentService;
@Autowired
 private GetUser getUser ;

    @PostMapping
    public String createTreatment(@RequestBody Treatment treatment) throws ExecutionException, InterruptedException {
        return treatmentService.createTreatment(treatment);
    }

     @GetMapping("/{treatmentId}")
    public Treatment getTreatment(@PathVariable String treatmentId) throws ExecutionException, InterruptedException {
        return treatmentService.getTreatment(treatmentId);
    }

    @PutMapping("/{treatmentId}")
    public String updateTreatment(@PathVariable String treatmentId, @RequestBody Treatment updatedTreatment) throws ExecutionException, InterruptedException {
        return treatmentService.updateTreatment(treatmentId, updatedTreatment);
    }

     @DeleteMapping("/{treatmentId}")
    public String deleteTreatment(@PathVariable String treatmentId) throws ExecutionException, InterruptedException {
        return treatmentService.deleteTreatment(treatmentId);
    }


    @GetMapping
    public List<Treatment> getAllTreatments() throws ExecutionException, InterruptedException {
        return treatmentService.getAllTreatments();
    }

    @GetMapping("/skinType/{skinType}")
    public List<Treatment> getTreatmentsBySkinType(@PathVariable String skinType) throws ExecutionException, InterruptedException {
        return treatmentService.getTreatmentsBySkinType(skinType);
    }
    
    @GetMapping("/{treatmentId}/products")
public ResponseEntity<?> getProductsForTreatment(@PathVariable String treatmentId, HttpServletRequest request) throws Exception {
   
   
        User admin = getUser.userFromToken(request);
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User not found or invalid token");
        }
    return ResponseEntity.ok( treatmentService.getProductsForTreatment(treatmentId,admin));
}

@PostMapping("/{treatmentId}/products/{productId}/{productName}")
public String addProductToTreatment(@PathVariable String treatmentId, @PathVariable String productId, @PathVariable String productName) throws ExecutionException, InterruptedException {
    return treatmentService.addProductToTreatment(treatmentId, productId,productName);
}

@DeleteMapping("/{treatmentId}/products/{productId}")
public String removeProductFromTreatment(@PathVariable String treatmentId, @PathVariable String productId) throws ExecutionException, InterruptedException {
    return treatmentService.removeProductFromTreatment(treatmentId, productId);
}

}
