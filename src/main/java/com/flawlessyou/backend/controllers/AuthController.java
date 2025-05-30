package com.flawlessyou.backend.controllers;

import com.flawlessyou.backend.entity.user.Role;
import com.flawlessyou.backend.entity.user.User;
import com.flawlessyou.backend.entity.user.UserResponse;
import com.flawlessyou.backend.entity.user.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.flawlessyou.backend.Security.Jwt.JwtUtils;
import com.flawlessyou.backend.Security.Services.UserDetailsImpl;
import com.flawlessyou.backend.config.GetUser;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
   public GetUser getUser;
   private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
  

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
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
    
            return ResponseEntity.ok(new JwtResponse(
                jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles
            ));
    
        } catch (BadCredentialsException e) {
            logger.error("Authentication failed: Invalid username or password", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MessageResponse("Invalid username or password"));
        } catch (Exception e) {
            logger.error("An error occurred during authentication", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("An error occurred during authentication: " + e.getMessage()));
        }
    }
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        try {
            if (userService.existsByUsername(signUpRequest.getUsername())) {
                return ResponseEntity.badRequest()
                    .body(new MessageResponse("Username is already taken!"));
            }
    
            if (userService.existsByEmail(signUpRequest.getEmail())) {
                return ResponseEntity.badRequest()
                    .body(new MessageResponse("Email is already in use!"));
            }
    
            String encodedPassword = encoder.encode(signUpRequest.getPassword());
            logger.info("Encoded password: " + encodedPassword);
    
            User user = new User(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                signUpRequest.getPhoneNumber(),
                signUpRequest.getGender(),
                encodedPassword
            );
    
            user.setRole(Role.USER);
            userService.saveUser(user);
            return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    
        } catch (Exception e) {
            logger.error("Registration failed: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Registration failed: " + e.getMessage()));
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MessageResponse("User is not authenticated"));
        }
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("name", principal.getAttribute("name"));
        userDetails.put("email", principal.getAttribute("email"));
        userDetails.put("picture", principal.getAttribute("picture"));
        return ResponseEntity.ok(userDetails);
    }

//     @PostMapping("/google")
// public ResponseEntity<?> authenticateWithGoogle(@RequestBody Map<String, String> request) {
//     try {
//         String email = request.get("email");

//         // التحقق من وجود البريد الإلكتروني في الطلب
//         if (email == null || email.isEmpty()) {
//             return ResponseEntity.badRequest()
//                 .body(new MessageResponse("Email is required"));
//         }

//         // البحث عن المستخدم بواسطة البريد الإلكتروني
//         User user = userService.findByEmail(email).get();

//         // إذا لم يتم العثور على المستخدم
//         if (user == null) {
//             return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                 .body(new MessageResponse("User not found with email: " + email));
//         }

//         // إرجاع معلومات المستخدم
//         return ResponseEntity.ok(user);

//     } catch (Exception e) {
//         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//             .body(new MessageResponse("Error retrieving user: " + e.getMessage()));
//     }
// }

@PostMapping("/google")
public ResponseEntity<?> authenticateWithGoogle(@RequestBody Map<String, String> request) {
    try {
        String email = request.get("email");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Email is required"));
        }

        Optional<User> userOptional = userService.findByEmail(email);

        if (userOptional.isEmpty()) { 
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new MessageResponse("User not found with email: " + email));
        }

        User user = userOptional.get();
        
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());
        
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        System.out.println("Generated JWT token for Google login: " + jwt);
        List<String> roles = userDetails.getAuthorities().stream()
        .map(item -> item.getAuthority())
        .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(
            jwt,
            user.getUserId(),
            user.getUserName(),
            user.getEmail(),  
            roles        ));

    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new MessageResponse("Error retrieving user: " + e.getMessage()));
    }
}
    @PutMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> passwords,
                                          HttpServletRequest request) throws Exception {
       
            String oldPassword = passwords.get("oldPassword");
            String newPassword = passwords.get("newPassword");
            
            if (oldPassword == null || newPassword == null) {
                return ResponseEntity.badRequest()
                    .body(new MessageResponse("Both old and new passwords are required"));
            }

          
            User user = getUser.userFromToken(request);
                if (!encoder.matches(oldPassword, user.getHashedPassword())) {
                    return ResponseEntity.badRequest()
                        .body(new MessageResponse("Current password is incorrect"));
                }

                user.setHashedPassword(encoder.encode(newPassword));
                userService.saveUser(user);
                return ResponseEntity.ok(new MessageResponse("Password changed successfully"));
    
    }

}