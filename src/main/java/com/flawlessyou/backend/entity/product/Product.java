package com.flawlessyou.backend.entity.product;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.List;
import java.util.Map;

import com.flawlessyou.backend.entity.routine.RoutineTime;

import jakarta.persistence.GeneratedValue;
// @Data
@NoArgsConstructor
public class Product {
    // @GeneratedValue
    private String productId;
    private String name;
    private List<Type> skinType;
    private String description;
    private String smaledescription;
    private List<String> ingredients;
    private String adminId;// هاد اليوزر الي ضاف البرودكت 
    private List<String> photos;
    private Map<String, Integer> reviews;
    private List<RoutineTime> usageTime;
    public Product(String name, List<Type> skinType, String description, List<String> ingredients, String smaledescription
) {
        this.name = name;
        this.skinType = skinType;
        this.description = description;
        this.ingredients = ingredients;
        this.smaledescription =smaledescription;
  
      
        
    }
    
    public String getProductId() {
        return productId;
    }
    public void setProductId(String productId) {
        this.productId = productId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<Type> getSkinType() {
        return skinType;
    }
    public void setSkinType(List<Type> skinType) {
        this.skinType = skinType;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public List<String> getIngredients() {
        return ingredients;
    }
    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
    public String getAdminId() {
        return adminId;
    }
    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }
    public List<String> getPhotos() {
        return photos;
    }
    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }
    public Map<String, Integer>  getReviews() {
        return reviews;
    }
    public void setReviews(Map<String, Integer>  reviews) {
        this.reviews = reviews;
    }

    public List<RoutineTime> getUsageTime() {
        return usageTime;
    }

    public void setUsageTime(List<RoutineTime> usageTime) {
        this.usageTime = usageTime;
    }

    public String getSmaledescription() {
        return smaledescription;
    }

    public void setSmaledescription(String smaledescription) {
        this.smaledescription = smaledescription;
    }

    

    
}