package com.flawlessyou.backend.controllers;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flawlessyou.backend.entity.treatments.treatment;
import com.flawlessyou.backend.entity.treatments.treatmentService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api/treatments")
public class TreatmentController {
     @Autowired
    private treatmentService treatmentService;

    @PostMapping
    public String createTreatment(@RequestBody treatment treatment) throws ExecutionException, InterruptedException {
        return treatmentService.createTreatment(treatment);
    }

}
