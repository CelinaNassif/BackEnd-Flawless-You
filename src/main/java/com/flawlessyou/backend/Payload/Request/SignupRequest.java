package com.flawlessyou.backend.Payload.Request;

import java.util.Set;

import com.flawlessyou.backend.entity.user.Gender;

import jakarta.validation.constraints.*;

public class SignupRequest {
  @NotBlank
  @Size(min = 3, max = 20)
  private String username;

  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  

  @NotBlank
  @Size(min = 6, max = 40 , message = " the password size must be between 6 and 40")
  private String password;

  @NotBlank
  @Size(min = 10, max = 15 , message = " the phone number size must be between 6 and 40")
  private String phoneNumber;


  @NotNull
  private Gender gender;
  public String getUsername() {
    return username;
  }

  public SignupRequest(@NotBlank @Size(min = 3, max = 20) String username,
      @NotBlank @Size(max = 50) @Email String email,
      @NotBlank @Size(min = 6, max = 40, message = " the password size must be between 6 and 40") String password,
      @NotBlank @Size(min = 10, max = 15, message = " the phone number size must be between 6 and 40") String phoneNumber,
      @NotNull Gender gender) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.phoneNumber = phoneNumber;
    this.gender = gender;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public Gender getGender() {
    return gender;
  }

  public void setGender(Gender gender) {
    this.gender = gender;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  
  }

