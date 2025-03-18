package com.flawlessyou.backend.entity.user;
// import com.flawlessyou.backend.entity.user.Role;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.NoArgsConstructor;
@NoArgsConstructor
public class User {
    private String userId;
    private String userName;
    private String email;
    private String phoneNumber;
    private Gender gender;
    private String skinType;
    @JsonIgnore
    private String hashedPassword;
    public Role role;
    private String profilePicture;
    private LocalDate dateOfBirth;
    private List<String> skinAnalysisHistoryIds;// نتأكد منهم اذا سترنج ولا skinAnalisis
    private List<String> savedProductIds;// نتأكد
    private List<String> reviewIds;// نتأكد

    // private List<SimpleGrantedAuthority> authorities;   

    private String routineId;

    public User( String userName, String email,  String hashedPassword) {
       
        this.userId = UUID.randomUUID().toString();
        this.userName = userName;
        this.email = email;
        this.hashedPassword = hashedPassword;

   
    }


    
    public User(String userName, String email, String phoneNumber, Gender gender,  String hashedPassword) {
        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.hashedPassword = hashedPassword;
    }

   

    public User( String userName, String email, String phoneNumber, Gender gender, String skinType,
            String hashedPassword, String profilePicture, LocalDate dateOfBirth) {
                this.userId = UUID.randomUUID().toString();

        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.skinType = skinType;
        this.hashedPassword = hashedPassword;
        this.profilePicture = profilePicture;
        this.dateOfBirth = dateOfBirth;
    }
    // public List<SimpleGrantedAuthority> getAuthorities() {
    //     return authorities;
    // }

    // public void setAuthorities(List<SimpleGrantedAuthority> authorities) {
    //     this.authorities = authorities;
    // }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
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
    public String getSkinType() {
        return skinType;
    }
    public void setSkinType(String skinType) {
        this.skinType = skinType;
    }
    public String getHashedPassword() {
        return hashedPassword;
    }
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }
    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }
    public String getProfilePicture() {
        return profilePicture;
    }
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    public List<String> getSkinAnalysisHistoryIds() {
        return skinAnalysisHistoryIds;
    }
    public void setSkinAnalysisHistoryIds(List<String> skinAnalysisHistoryIds) {
        this.skinAnalysisHistoryIds = skinAnalysisHistoryIds;
    }
    public List<String> getSavedProductIds() {
        return savedProductIds;
    }
    public void setSavedProductIds(List<String> savedProductIds) {
        this.savedProductIds = savedProductIds;
    }
    public List<String> getReviewIds() {
        return reviewIds;
    }
    public void setReviewIds(List<String> reviewIds) {
        this.reviewIds = reviewIds;
    }



    public String getRoutineId() {
        return routineId;
    }



    public void setRoutineId(String routineId) {
        this.routineId = routineId;
    }
  
    
}