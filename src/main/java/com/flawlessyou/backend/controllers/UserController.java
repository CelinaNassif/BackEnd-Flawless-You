package com.flawlessyou.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.flawlessyou.backend.entity.cloudinary.CloudinaryResponse;
import com.flawlessyou.backend.entity.cloudinary.CloudinaryService;
import com.flawlessyou.backend.entity.user.User;
import com.flawlessyou.backend.entity.user.UserService;
import com.flawlessyou.backend.util.FileUploadUtil;
import com.flawlessyou.backend.Security.Jwt.JwtUtils;

import org.springframework.util.StringUtils;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;

    @Autowired
    private CloudinaryService cloudinaryService;

    /**
     * رفع صورة الملف الشخصي للمستخدم
     */
    @PostMapping("/{userId}/profile-picture")
    public ResponseEntity<String> uploadProfilePicture(@PathVariable String userId, 
                                                       @RequestParam("file") MultipartFile file) throws Exception {
        FileUploadUtil.assertAllowed(file, FileUploadUtil.IMAGE_PATTERN);
        final String fileName = FileUploadUtil.getFileName(file.getOriginalFilename());
        final CloudinaryResponse response = cloudinaryService.uploadFile(file, fileName);

        userService.addProfilePicture(userId, response.getUrl());
        return ResponseEntity.ok(response.getUrl());
    }

    /**
     * جلب بيانات المستخدم من التوكن
     */
    @GetMapping("/{me}")
    public ResponseEntity<User> getUser(@RequestHeader(name = "Authorization", required = false) String authHeader) throws Exception {
        User receiver = userFromToken(authHeader);
        return ResponseEntity.ok(userService.getUserById(receiver.getUserId()));
    }

    /**
     * استخراج المستخدم من التوكن
     */
    private User userFromToken(String authHeader) throws Exception {
        String jwt = parseJwt(authHeader);

        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
            String username = jwtUtils.getUserNameFromJwtToken(jwt);

            try {
                return userService.findByUsername(username)
                        .orElseThrow(() -> new Exception("User not found."));
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException("Error fetching user from Firestore", e);
            }
        }

        throw new Exception("Invalid JWT Token");
    }

    /**
     * استخراج JWT من الهيدر
     */
    private String parseJwt(String authHeader) {
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // استخراج التوكن بعد "Bearer "
        }
        return null;
    }
}
