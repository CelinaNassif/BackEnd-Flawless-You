package com.flawlessyou.backend.config;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.flawlessyou.backend.Security.Jwt.JwtUtils;
import com.flawlessyou.backend.entity.user.User;
import com.flawlessyou.backend.entity.user.UserService;

import io.jsonwebtoken.Claims;

import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Component 
public class GetUser {
     @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(GetUser.class);

    public GetUser() {
        logger.info("GetUser bean created!");
    }
     
    public String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }

 
    public User userFromToken(HttpServletRequest authHeader) throws Exception {
    String jwt = parseJwt(authHeader);
    if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
        Claims claims = jwtUtils.getClaimsFromToken(jwt);
        String username = claims.getSubject();
        List<String> roles = claims.get("roles", List.class); // تأكد من أن الحقل اسمه "roles"

        // إذا كانت الأدوار null، قم بتعيينها إلى قائمة فارغة
        if (roles == null) {
            roles = new ArrayList<>();
        }

        // طباعة الأدوار للتحقق منها
        logger.info("Roles from token: " + roles);

        User user = userService.findByUsername(username)
            .orElseThrow(() -> new Exception("User not found"));
        user.setAuthorities(roles.stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList()));
        return user;
    }
    throw new Exception("Invalid token");
}
}
