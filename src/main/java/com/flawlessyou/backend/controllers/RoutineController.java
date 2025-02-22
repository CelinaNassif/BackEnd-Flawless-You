package com.flawlessyou.backend.controllers;


import com.flawlessyou.backend.entity.routine.Routine;
import com.flawlessyou.backend.entity.routine.RoutineService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
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


    @GetMapping("/{routineId}")
    public ResponseEntity<Routine> getRoutineById(@PathVariable String routineId) throws ExecutionException, InterruptedException {
        
        return ResponseEntity.ok(routineService.getRoutineById(routineId)) ;
    }
}
