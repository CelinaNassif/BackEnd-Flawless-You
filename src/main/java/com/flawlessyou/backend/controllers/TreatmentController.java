package com.flawlessyou.backend.controllers;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flawlessyou.backend.entity.product.Product;
import com.flawlessyou.backend.entity.treatments.treatment;
import com.flawlessyou.backend.entity.treatments.treatmentService;
import org.springframework.web.bind.annotation.RequestBody;
// import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api/treatments")
public class TreatmentController {
     @Autowired
    private treatmentService treatmentService;

    @PostMapping
    public String createTreatment(@RequestBody treatment treatment) throws ExecutionException, InterruptedException {
        return treatmentService.createTreatment(treatment);
    }

     @GetMapping("/{treatmentId}")
    public treatment getTreatment(@PathVariable String treatmentId) throws ExecutionException, InterruptedException {
        return treatmentService.getTreatment(treatmentId);
    }

    @PutMapping("/{treatmentId}")
    public String updateTreatment(@PathVariable String treatmentId, @RequestBody treatment updatedTreatment) throws ExecutionException, InterruptedException {
        return treatmentService.updateTreatment(treatmentId, updatedTreatment);
    }

     @DeleteMapping("/{treatmentId}")
    public String deleteTreatment(@PathVariable String treatmentId) throws ExecutionException, InterruptedException {
        return treatmentService.deleteTreatment(treatmentId);
    }


    @GetMapping
    public List<treatment> getAllTreatments() throws ExecutionException, InterruptedException {
        return treatmentService.getAllTreatments();
    }

    @GetMapping("/skinType/{skinType}")
    public List<treatment> getTreatmentsBySkinType(@PathVariable String skinType) throws ExecutionException, InterruptedException {
        return treatmentService.getTreatmentsBySkinType(skinType);
    }
    
    @GetMapping("/{treatmentId}/products")
public List<Product> getProductsForTreatment(@PathVariable String treatmentId) throws ExecutionException, InterruptedException {
    return treatmentService.getProductsForTreatment(treatmentId);
}

}
