package com.flawlessyou.backend.controllers;

import com.flawlessyou.backend.entity.user.Role;
import com.flawlessyou.backend.entity.user.User;
import com.flawlessyou.backend.entity.user.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.flawlessyou.backend.Security.Jwt.JwtUtils;
import com.flawlessyou.backend.Security.Services.UserDetailsImpl;
import com.flawlessyou.backend.Payload.Request.LoginRequest;
import com.flawlessyou.backend.Payload.Request.SignupRequest;
import com.flawlessyou.backend.Payload.Response.JwtResponse;
import com.flawlessyou.backend.Payload.Response.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.Collections;

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
                loginRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(
            jwt,
            userDetails.getId(),
            userDetails.getUsername(),
            userDetails.getEmail(),
            roles
        ));

     } catch (BadCredentialsException e) {
        System.err.println("Bad credentials for user: " + loginRequest.getUsername());
        return ResponseEntity.status(401).body("Invalid username/password");
        
    } catch (Exception e) {
        System.err.println("Authentication error: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(500).body("Internal server error");
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



// @GetMapping("/user")
// @ResponseBody
// public Map<String, Object> getUserDetails(@AuthenticationPrincipal OAuth2User principal) {
//     if (principal == null) {
//         return Collections.singletonMap("message", "User is not authenticated");
//     }
//     return Collections.singletonMap("name", principal.getAttribute("name"));
// }
@PostMapping("/google")
public ResponseEntity<?> authenticateWithGoogle(@RequestBody Map<String, String> request) {
    try {
        String idToken = request.get("idToken");

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList("631393157394-vocg3facesl3ur7mgnokqd11vjhiupql.apps.googleusercontent.com")) // ضع Client ID هنا
                .build();

        GoogleIdToken googleIdToken = verifier.verify(idToken);
        if (googleIdToken == null) {
            return ResponseEntity.badRequest().body("Invalid ID token");
        }

        Payload payload = googleIdToken.getPayload();
        String email = payload.getEmail();

        Optional<User> userOptional = userService.findByEmail(email);
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            user = new User();
            user.setEmail(email);
            user.setUserName(payload.get("name").toString());
            user.setRole(Role.USER);
            userService.saveUser(user);
        }

        // تحويل User إلى UserDetailsImpl
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);

        // إنشاء Authentication باستخدام UserDetailsImpl
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        return ResponseEntity.ok(new JwtResponse(jwt, user.getUserId(), user.getUserName(), user.getEmail(), List.of(user.getRole().name())));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Google authentication failed");
    }
}

}