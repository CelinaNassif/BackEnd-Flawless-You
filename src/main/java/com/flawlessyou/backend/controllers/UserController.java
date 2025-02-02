package com.flawlessyou.backend.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.flawlessyou.backend.entity.user.User;
import com.flawlessyou.backend.entity.user.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public void createUser(@RequestBody User user) throws Exception {
        userService.createUser(user);
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable String userId) throws Exception {
        return userService.getUserById(userId);
    }
}