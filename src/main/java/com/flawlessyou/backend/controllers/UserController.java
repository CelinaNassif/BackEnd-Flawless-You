package com.flawlessyou.backend.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.flawlessyou.backend.entity.cloudinary.CloudinaryResponse;
import com.flawlessyou.backend.entity.cloudinary.CloudinaryService;
import com.flawlessyou.backend.entity.product.Product;
import com.flawlessyou.backend.entity.user.User;
import com.flawlessyou.backend.entity.user.UserService;
import com.flawlessyou.backend.util.FileUploadUtil;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
  @Autowired
    private CloudinaryService cloudinaryService;

    // @PostMapping
    // public void createUser(@RequestBody User user) throws Exception {
    //     userService.createUser(user);
    // }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable String userId) throws Exception {
        return userService.getUserById(userId);
    }
    @PostMapping("/{userId}/profile-picture")
    public void uploadProfilePicture(@PathVariable String userId, @RequestParam("file") MultipartFile file) throws Exception {
        FileUploadUtil.assertAllowed(file, FileUploadUtil.IMAGE_PATTERN);
        final String fileName = FileUploadUtil.getFileName(file.getOriginalFilename());
        final CloudinaryResponse response = this.cloudinaryService.uploadFile(file, fileName);
        
        // تحديث صورة الملف الشخصي للمستخدم
        userService.addProfilePicture(userId, response.getUrl());
    }

}