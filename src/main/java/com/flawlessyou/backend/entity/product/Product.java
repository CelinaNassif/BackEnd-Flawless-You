package com.flawlessyou.backend.entity.product;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.List;

import jakarta.persistence.GeneratedValue;
@Data
@NoArgsConstructor
public class Product {
    // @GeneratedValue
    private String productId;
    private String name;
    private List<Type> skinType;
    private String description;
    private List<String> ingredients;
    private String adminId;// هاد اليوزر الي ضاف البرودكت 
    private List<String> photos;
    private List<String> reviewIds;
    public Product(String name, List<Type> skinType, String description, List<String> ingredients
) {
        this.name = name;
        this.skinType = skinType;
        this.description = description;
        this.ingredients = ingredients;
      
        
    }

   


    
}