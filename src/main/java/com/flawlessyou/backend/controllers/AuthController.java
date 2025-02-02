package com.flawlessyou.backend.controllers;

import com.flawlessyou.backend.entity.user.Role;
import com.flawlessyou.backend.entity.user.User;
import com.flawlessyou.backend.entity.user.UserService;
import com.flawlessyou.backend.Security.Jwt.JwtUtils;
import com.flawlessyou.backend.Security.Services.UserDetailsImpl;
import com.flawlessyou.backend.Payload.Request.LoginRequest;
import com.flawlessyou.backend.Payload.Request.SignupRequest;
import com.flawlessyou.backend.Payload.Response.JwtResponse;
import com.flawlessyou.backend.Payload.Response.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            return ResponseEntity.ok(new JwtResponse(
                    jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    userDetails.getAuthorities().stream()
                            .map(item -> item.getAuthority())
                            .toList()));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Authentication failed: " + e.getMessage()));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        try {
            if (userService.existsByUsername(signUpRequest.getUsername())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Username already taken!"));
            }

            if (userService.existsByEmail(signUpRequest.getEmail())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Email already in use!"));
            }

            User user = new User(
                    signUpRequest.getUsername(),
                    signUpRequest.getEmail(),
                    encoder.encode(signUpRequest.getPassword())
            );
            user.setRole(Role.USER);

            userService.saveUser(user);
            return ResponseEntity.ok(new MessageResponse("User registered successfully!"));

        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.internalServerError().body(new MessageResponse("Registration failed: " + e.getMessage()));
        }
    }

    @PutMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> passwords, HttpServletRequest request) {
        try {
            String oldPassword = passwords.get("oldPassword");
            String newPassword = passwords.get("newPassword");
            String jwt = parseJwt(request);

            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                User user = userService.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                if (!encoder.matches(oldPassword, user.getHashedPassword())) {
                    return ResponseEntity.badRequest().body(new MessageResponse("Incorrect old password"));
                }

                user.setHashedPassword(encoder.encode(newPassword));
                userService.saveUser(user);
                return ResponseEntity.ok(new MessageResponse("Password changed successfully"));
            }
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid token"));

        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.internalServerError().body(new MessageResponse("Error changing password"));
        }
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}