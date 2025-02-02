package com.flawlessyou.backend.Security.Services;

import java.util.Collection;
import java.util.Collections;

import java.util.Objects;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flawlessyou.backend.entity.user.User;

public class UserDetailsImpl implements UserDetails {
  private static final long serialVersionUID = 1L;

  private String id;

  private String username;

  private String email;

  @JsonIgnore
  private String password;

   private Collection<? extends GrantedAuthority> authorities;

  public UserDetailsImpl(String id, String username, String email, String password , Collection<? extends GrantedAuthority> authorities) {
      this.id = id;
    this.username = username;
    this.email = email;
    this.password = password;
    this.authorities=authorities;
   
  }

  public static UserDetailsImpl build(User user) {
    // List<GrantedAuthority> authorities = user.role.stream()
    //     .map(role -> new SimpleGrantedAuthority(role.name()))
    //     .collect(Collectors.toList());
    GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().name());

    return new UserDetailsImpl(
        user.getUserId(),
        user.getUserName(),
        user.getEmail(),
        user.getHashedPassword(),
        Collections.singletonList(authority) // تحويلها إلى قائمة تحتوي على عنصر واحد
    );
       
  } 

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
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
}
