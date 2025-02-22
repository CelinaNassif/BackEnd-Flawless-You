package com.flawlessyou.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import org.springframework.http.MediaType;
import com.flawlessyou.backend.entity.cloudinary.CloudinaryResponse;
import com.flawlessyou.backend.entity.cloudinary.CloudinaryService;
import com.flawlessyou.backend.entity.user.User;
import com.flawlessyou.backend.entity.user.UserService;
import com.flawlessyou.backend.util.FileUploadUtil;
import com.flawlessyou.backend.entity.user.Gender;
import com.flawlessyou.backend.entity.user.Role; 

import jakarta.servlet.http.HttpServletRequest;

import com.flawlessyou.backend.Security.Jwt.JwtUtils;
import com.flawlessyou.backend.config.GetUser;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.flawlessyou.backend.Payload.Response.MessageResponse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private GetUser getUser;
    

    @Autowired
    private UserService userService;
    
    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser( HttpServletRequest request) {
        try {
            User user = getUser.userFromToken(request);
          
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MessageResponse("Error fetching user details: " + e.getMessage()));
        }
    }
    
    @GetMapping("/userName")
    public ResponseEntity<?> getUserName( HttpServletRequest request) {
        try {
            User user = getUser.userFromToken(request);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }
            return ResponseEntity.ok(user.getUserName());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MessageResponse("Error fetching user details: " + e.getMessage()));
        }
    }

    @PostMapping(value = "/profilePicture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProfilePicture(
        HttpServletRequest request,
        @RequestParam("file") MultipartFile file) {
    
        try {
            // الحصول على المستخدم من التوكن
            User user = getUser.userFromToken(request);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }
    
            // التحقق من صحة الملف
            FileUploadUtil.assertAllowed(file, FileUploadUtil.IMAGE_PATTERN);
    
            // إنشاء اسم فريد للملف
            String fileName = user.getUserId() + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            fileName = StringUtils.cleanPath(fileName);
    
            // رفع الملف إلى Cloudinary
            CloudinaryResponse response = cloudinaryService.uploadFile(file, fileName);
    
            // تحديث صورة البروفايل للمستخدم
            user.setProfilePicture(response.getUrl());
            userService.saveUser(user);
    
            // إرجاع النتيجة
            Map<String, String> result = new HashMap<>();
            result.put("url", response.getUrl());
            result.put("message", "Profile picture updated successfully");
    
            return ResponseEntity.ok(result);
    
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Invalid file type. Only images are allowed."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error uploading profile picture: " + e.getMessage()));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(
        HttpServletRequest request, @RequestBody User newUser
    ) {
        try {
            // Get user from token
            User user = getUser.userFromToken(request);
    
            // Debugging: Print the newUser object to check the date format
            System.out.println("New User Date of Birth: " + newUser.getDateOfBirth());
    
            // Update fields if values are not null and not empty
            if (newUser.getUserName() != null && !newUser.getUserName().isEmpty()) {
                user.setUserName(newUser.getUserName());
            }
            if (newUser.getEmail() != null && !newUser.getEmail().isEmpty()) {
                user.setEmail(newUser.getEmail());
            }
            if (newUser.getPhoneNumber() != null && !newUser.getPhoneNumber().isEmpty()) {
                user.setPhoneNumber(newUser.getPhoneNumber());
            }
            if (newUser.getGender() != null) {
                user.setGender(newUser.getGender());
            }
    
            // Save updates to the database
            userService.saveUser(user);
    
            return ResponseEntity.ok(new MessageResponse("User updated successfully"));
        } catch (Exception e) {
            // Log the full error for debugging
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error updating user: " + e.getMessage()));
        }
    }
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable String userId) {
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
  
            return ResponseEntity.ok(user);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error fetching user: " + e.getMessage()));
        }
    }

  



@GetMapping("/experts")
    public List<String> getExperts() throws ExecutionException, InterruptedException {
        // قم بجلب جميع المستخدمين الذين لديهم دور SKIN_EXPERT
        List<User> experts = userService.getUsersByRole(Role.SKIN_EXPERT);

        // استخراج أسماء الخبراء فقط
        return experts.stream()
                .map(User::getUserName)
                .collect(Collectors.toList());
    }



}