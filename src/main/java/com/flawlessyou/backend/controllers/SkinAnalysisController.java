package com.flawlessyou.backend.controllers;


import com.flawlessyou.backend.config.GetUser;
import com.flawlessyou.backend.entity.SkinAnalysis.SkinAnalysis;
import com.flawlessyou.backend.entity.product.Type;
import com.flawlessyou.backend.entity.treatments.Problem;
import com.flawlessyou.backend.entity.user.User;

import jakarta.servlet.http.HttpServletRequest;

import com.flawlessyou.backend.entity.SkinAnalysis.SkinAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


@RestController
@RequestMapping("/api/skin-analysis")
public class SkinAnalysisController {
        @Autowired
        private GetUser getUser;
    
        @Autowired
        private SkinAnalysisService skinAnalysisService;
    
        @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public SkinAnalysis analyzeSkin(
                HttpServletRequest request,
                @RequestParam Type skinType,
                @RequestParam Map<Problem, Double> problems,
                @RequestParam("imageFile") MultipartFile imageFile) throws Exception {
        
            User user = getUser.userFromToken(request);
            if (user == null || user.getUserId() == null || user.getUserId().isEmpty()) {
                throw new IllegalArgumentException("Invalid user or user ID.");
            }
        
            return skinAnalysisService.analyzeSkin(user.getUserId(), skinType, problems, imageFile);
        }
    }