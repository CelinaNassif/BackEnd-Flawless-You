package com.flawlessyou.backend.controllers;


import com.flawlessyou.backend.entity.product.Product;
import com.flawlessyou.backend.entity.routine.Routine;
import com.flawlessyou.backend.entity.routine.RoutineService;
import com.flawlessyou.backend.entity.routine.RoutineTime;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController 
@RequestMapping("/api/routines")
public class RoutineController {

    @Autowired
    private RoutineService routineService;

    @PostMapping("/create")
    public ResponseEntity<Routine> createRoutine(HttpServletRequest request,
                                 @RequestBody Routine routine) throws Exception {
        return ResponseEntity.ok(routineService.createRoutine(request, routine)) ;
    }

    @PostMapping("/")
    public ResponseEntity<?> getRoutineById(@RequestBody Map<String, String> requestBody) {
        String routineId = requestBody.get("routineId");
        try {
            Routine routine = routineService.getRoutineById(routineId);
            return ResponseEntity.ok(routine);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving routine: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    // @GetMapping("/{analysisId}")
    // public String getAnalysisById(@PathVariable String analysisId) {
    //     try {
    //         String analysisData = routineService.getAnalysisById(analysisId);
    //         if (analysisData != null) {
    //             return analysisData;
    //         } else {
    //             return "Analysis not found";
    //         }
    //     } catch (ExecutionException | InterruptedException e) {
    //         e.printStackTrace();
    //         return "Error retrieving analysis: " + e.getMessage();
    //     }
    // }
    @GetMapping("/userRoutine")
    public Routine getAllRoutinesForUser(HttpServletRequest request) throws  Exception {
     return routineService.getRoutineUser(request);
    }


@GetMapping("/by-time")
    public Map<RoutineTime, List<Product>> getRoutineWithProductsByTime(HttpServletRequest request) throws Exception {
       
        return routineService.getRoutineWithProductsByTime(request);
    }
}


