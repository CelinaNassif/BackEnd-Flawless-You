package com.flawlessyou.backend.controllers;

import com.flawlessyou.backend.entity.routine.Routine;
import com.flawlessyou.backend.entity.routine.RoutineService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Map;

@RestController
@RequestMapping("/api/routines")
public class RoutineController {

    @Autowired
    private RoutineService routineService;

    @PostMapping("/create")
    public Routine createRoutine(HttpServletRequest request,
                                 @RequestParam String userId,
                                 @RequestParam Map<String, Timestamp> productIds,
                                 @RequestParam Timestamp timeAnalysis,
                                 @RequestParam String description,
                                 @RequestParam String analysisId) throws Exception {
        return routineService.createRoutine(request, userId, productIds, timeAnalysis, description, analysisId);
    }
}