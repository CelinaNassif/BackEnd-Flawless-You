package com.flawlessyou.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import com.flawlessyou.backend.entity.cloudinary.CloudinaryResponse;
import com.flawlessyou.backend.entity.cloudinary.CloudinaryService;
import com.flawlessyou.backend.entity.user.User;
import com.flawlessyou.backend.entity.user.UserService;
import com.flawlessyou.backend.util.FileUploadUtil;

import jakarta.servlet.http.HttpServletRequest;

import com.flawlessyou.backend.Security.Jwt.JwtUtils;
import com.flawlessyou.backend.config.GetUser;
import com.flawlessyou.backend.Payload.Response.MessageResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private GetUser getUser;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader(name = "Authorization", required = false) String authHeader) {
        try {
            User user = userFromToken(authHeader);
            Map<String, Object> userResponse = new HashMap<>();
            userResponse.put("userId", user.getUserId());
            userResponse.put("username", user.getUserName());
            userResponse.put("email", user.getEmail());
            userResponse.put("role", user.getRole());
            userResponse.put("profilePicture", user.getProfilePicture());
            
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



    @PostMapping("/profile-picture")
    public ResponseEntity<?> uploadProfilePicture(
            @RequestHeader(name = "Authorization", required = false) String authHeader,
            @RequestParam("file") MultipartFile file) {
        try {
            User user = userFromToken(authHeader);
            
            // Validate file
            FileUploadUtil.assertAllowed(file, FileUploadUtil.IMAGE_PATTERN);
            
            // Generate unique filename
            String fileName = StringUtils.cleanPath(
                user.getUserId() + "" + System.currentTimeMillis() + "" + file.getOriginalFilename()
            );
            
            // Upload to Cloudinary
            CloudinaryResponse response = cloudinaryService.uploadFile(file, fileName);
            
            // Update user profile
            user.setProfilePicture(response.getUrl());
            userService.saveUser(user);
            
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
            @RequestHeader(name = "Authorization", required = false) String authHeader,
            @RequestBody Map<String, String> updates) {
        try {
            User user = userFromToken(authHeader);
            
            if (updates.containsKey("username")) {
                String newUsername = updates.get("username");
                if (userService.existsByUsername(newUsername) && 
                    !newUsername.equals(user.getUserName())) {
                    return ResponseEntity.badRequest()
                        .body(new MessageResponse("Username already taken"));
                }
                user.setUserName(newUsername);
            }
            
            // Add more updateable fields as needed
            
            userService.saveUser(user);
            return ResponseEntity.ok(new MessageResponse("User updated successfully"));
            
        } catch (Exception e) {
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
            
            // Return only public information
            Map<String, Object> publicInfo = new HashMap<>();
            publicInfo.put("userId", user.getUserId());
            publicInfo.put("username", user.getUserName());
            publicInfo.put("profilePicture", user.getProfilePicture());
            
            return ResponseEntity.ok(publicInfo);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error fetching user: " + e.getMessage()));
        }
    }

    private User userFromToken(String authHeader) throws Exception {
        String jwt = parseJwt(authHeader);
        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
            String username = jwtUtils.getUserNameFromJwtToken(jwt);
            try {
                return userService.findByUsername(username)
                    .orElseThrow(() -> new Exception("User not found"));
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException("Error fetching user from database", e);
            }
        }
        throw new Exception("Invalid or missing JWT Token");
    }

    private String parseJwt(String authHeader) {
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}