package com.flawlessyou.backend.Security.Services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flawlessyou.backend.config.GetUser;
import com.flawlessyou.backend.entity.user.Role;
import com.flawlessyou.backend.entity.user.User;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private String id;
    private String username;
    private String email;
    private static final Logger logger = LoggerFactory.getLogger(GetUser.class);

    @JsonIgnore
    private String password;
    private Role role;
    // private Collection<SimpleGrantedAuthority> authorities; 
    // Constructor with all fields
    public UserDetailsImpl(String id, String username, String email, String password, Role role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Default constructor for deserialization
    public UserDetailsImpl() {
    }

    // Method to build UserDetailsImpl from User entity
    public static UserDetailsImpl build(User user) {
        if (user.getRole() == null) {
            user.setRole(Role.USER); // Default role if not set
        }

        // Create a SimpleGrantedAuthority from the user's role
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(authority);

        // Log the user role for debugging
        logger.info("User role: " + user.getRole().name());

        // Return a new UserDetailsImpl object
        return new UserDetailsImpl(
            user.getUserId(),
            user.getUserName(),
            user.getEmail(),
            user.getHashedPassword(),
            user.getRole()  // Wrap the authority in a list
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}