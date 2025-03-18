package com.flawlessyou.backend.controllers;

import com.flawlessyou.backend.config.GetUser;
import com.flawlessyou.backend.entity.SkinAnalysis.SkinAnalysis;
import com.flawlessyou.backend.entity.product.Product;
import com.flawlessyou.backend.entity.product.Type;
import com.flawlessyou.backend.entity.treatments.Problem;
import com.flawlessyou.backend.entity.treatments.Treatment;
import com.flawlessyou.backend.entity.SkinAnalysis.SkinAnalysisService;
import com.flawlessyou.backend.entity.user.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/skin-analysis")
public class SkinAnalysisController {

    @Autowired
    private GetUser getUser;

    @Autowired
    private SkinAnalysisService skinAnalysisService;


    @PostMapping("/recommend-treatments")
    public SkinAnalysis recommendTreatments(
            @RequestParam Type skinType,
            HttpServletRequest request,
            @RequestBody Map<Problem, Double> problems) throws Exception, InterruptedException {
        // استدعاء السيرفس للحصول على العلاجات الموصى بها
        User user = getUser.userFromToken(request);
        if (user == null || user.getUserId() == null || user.getUserId().isEmpty()) {
            throw new IllegalArgumentException("Invalid user or user ID.");
        }
        return skinAnalysisService.getRecommendedTreatments(user.getUserId(), skinType, problems);
    }
    @PostMapping( "/skintreatments")
    public List<Treatment>  skintreatments(
        
            @RequestParam Type skinType
       ) throws Exception {
    
    
      
        // استدعاء الخدمة للحصول على العلاجات المقترحة
        return skinAnalysisService.getTreatmentsBySkinType(skinType);
    }
    @PostMapping(value ="/{id}/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImageAndUpdateSkinAnalysis(
            @PathVariable String id,
            @RequestParam("imageFile") MultipartFile imageFile) {
        try {
            // استدعاء الدالة لرفع الصورة وتحديث SkinAnalysis
            boolean isUpdated = skinAnalysisService.uploadImageAndUpdateSkinAnalysis(id, imageFile);

            if (isUpdated) {
                return ResponseEntity.ok("Image uploaded and updated successfully!");
            } else {
                return ResponseEntity.badRequest().body("Failed to upload image or update SkinAnalysis.");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error uploading image: " + e.getMessage());
        }
    }
       @GetMapping("/{skinAnalysisId}/products")
    public Map<String, List<Product>> getProductsBySkinAnalysisId(@PathVariable String skinAnalysisId) throws Exception{

            return skinAnalysisService.getProductsBySkinAnalysisId(skinAnalysisId);
       
    }
}