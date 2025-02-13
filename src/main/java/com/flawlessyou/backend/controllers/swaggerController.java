package com.flawlessyou.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flawlessyou.backend.Security.Jwt.JwtUtils;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/swagger-ui")
public class swaggerController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    // @GetMapping("/")
    // public ResponseEntity<Object> redirectToSwagger() {
    //     Authentication authentication = authenticationManager.authenticate(
    //         new UsernamePasswordAuthenticationToken("mai", "password123")
    //     );

    //     SecurityContextHolder.getContext().setAuthentication(authentication);
    //     String token = jwtUtils.generateJwtToken(authentication);

    //     HttpHeaders headers = new HttpHeaders();
    //     headers.add("Authorization", "Bearer " + token);
    //     headers.add("Location", "/swagger-ui.html");

    //     return new ResponseEntity<>(headers, HttpStatus.FOUND);
    // }
    @GetMapping("/swagger-ui")
    public String redirectToSwaggerr() {
        return "redirect:/swagger-ui.html";
    }
}