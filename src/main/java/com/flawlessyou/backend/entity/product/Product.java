package com.flawlessyou.backend.entity.product;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    private String productId;
    private String name;
    private String skinType;
    private String description;
    private List<String> ingredients;
    private String adminId;// هاد اليوزر الي ضاف البرودكت 
    private List<String> photos;
    private List<String> reviewIds;

   
}